package com.example.openapi.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Aspect
@Component
@Slf4j
public class ExceptionHandlingAspect {

    private static final String ERROR_LOG_FILE = "error-log.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @AfterThrowing(pointcut = "@annotation(com.example.openapi.aspect.HandleException)", 
                   throwing = "exception")
    public void handleException(JoinPoint joinPoint, Exception exception) {
        String methodName = joinPoint.getSignature().toShortString();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String errorMessage = exception.getMessage();
        String timestamp = LocalDateTime.now().format(FORMATTER);

        log.error("Exception in {}.{}: {} - {}", className, methodName, 
                  exception.getClass().getSimpleName(), errorMessage, exception);

        writeToFile(timestamp, className, methodName, exception);
    }

    private void writeToFile(String timestamp, String className, String methodName, Exception exception) {
        try (FileWriter writer = new FileWriter(ERROR_LOG_FILE, true)) {
            writer.write(String.format("[%s] Exception in %s.%s: %s - %s%n",
                    timestamp, className, methodName, 
                    exception.getClass().getSimpleName(), exception.getMessage()));
            writer.write(String.format("Stack trace: %s%n%n", 
                    getStackTraceString(exception)));
        } catch (IOException e) {
            log.error("Failed to write exception to file", e);
        }
    }

    private String getStackTraceString(Exception exception) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : exception.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}

