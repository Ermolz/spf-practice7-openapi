package com.example.openapi.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
@Order(1)
public class AuditLoggingAspect {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Before("@annotation(com.example.openapi.aspect.AuditLog)")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String username = getCurrentUsername();
        Object[] args = joinPoint.getArgs();

        String logMessage = String.format(
            "[AUDIT] [%s] User: %s | Action: %s.%s | Parameters: %s",
            LocalDateTime.now().format(FORMATTER),
            username != null ? username : "ANONYMOUS",
            className,
            methodName,
            Arrays.toString(args)
        );

        log.info(logMessage);
    }

    @After("@annotation(com.example.openapi.aspect.AuditLog)")
    public void logAfter(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String username = getCurrentUsername();
        
        String returnType = "void";
        if (joinPoint.getSignature() instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            returnType = methodSignature.getReturnType().getSimpleName();
        }

        String logMessage = String.format(
            "[AUDIT] [%s] User: %s | Action: %s.%s | Result: SUCCESS | Return type: %s",
            LocalDateTime.now().format(FORMATTER),
            username != null ? username : "ANONYMOUS",
            className,
            methodName,
            returnType
        );

        log.info(logMessage);
    }

    @AfterReturning(pointcut = "@annotation(com.example.openapi.aspect.AuditLog)", 
                    returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String username = getCurrentUsername();

        String resultStr = result != null ? result.toString() : "null";
        if (resultStr.length() > 200) {
            resultStr = resultStr.substring(0, 200) + "...";
        }

        String logMessage = String.format(
            "[AUDIT] [%s] User: %s | Action: %s.%s | Returned value: %s",
            LocalDateTime.now().format(FORMATTER),
            username != null ? username : "ANONYMOUS",
            className,
            methodName,
            resultStr
        );

        log.info(logMessage);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() 
                && !authentication.getPrincipal().equals("anonymousUser")) {
            return authentication.getName();
        }
        return null;
    }
}

