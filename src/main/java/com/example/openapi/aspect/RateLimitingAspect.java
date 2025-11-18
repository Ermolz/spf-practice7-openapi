package com.example.openapi.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Aspect
@Component
@Slf4j
public class RateLimitingAspect {

    private final Map<String, CallTracker> callCounts = new ConcurrentHashMap<>();
    private static final long ONE_MINUTE_MS = 60_000;

    @Before("@annotation(com.example.openapi.aspect.RateLimit)")
    public void checkRateLimit(JoinPoint joinPoint) {
        RateLimit rateLimit = getRateLimitAnnotation(joinPoint);
        if (rateLimit == null) {
            return;
        }

        String userId = getCurrentUserId();
        if (userId == null) {
            return;
        }

        String methodName = joinPoint.getSignature().toShortString();
        String key = userId + "_" + methodName;
        int maxCalls = rateLimit.maxCalls();
        long windowMs = rateLimit.windowMinutes() * ONE_MINUTE_MS;

        CallTracker tracker = callCounts.computeIfAbsent(key, k -> new CallTracker());

        synchronized (tracker) {
            long now = System.currentTimeMillis();
            
            if (now - tracker.getFirstCallTime() > windowMs) {
                tracker.reset(now);
            }

            if (tracker.getCount() >= maxCalls) {
                log.warn("Rate limit exceeded for user {} on method {}. Calls: {}/{}", 
                        userId, methodName, tracker.getCount(), maxCalls);
                throw new RateLimitExceededException(
                    String.format("Rate limit exceeded: maximum %d calls per %d minute(s) for user %s", 
                            maxCalls, rateLimit.windowMinutes(), userId));
            }

            tracker.increment();
        }
    }

    private RateLimit getRateLimitAnnotation(JoinPoint joinPoint) {
        try {
            return joinPoint.getSignature()
                    .getDeclaringType()
                    .getMethod(joinPoint.getSignature().getName(),
                            ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature())
                                    .getParameterTypes())
                    .getAnnotation(RateLimit.class);
        } catch (Exception e) {
            return null;
        }
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() 
                && !authentication.getPrincipal().equals("anonymousUser")) {
            return authentication.getName();
        }
        return null;
    }

    private static class CallTracker {
        private final AtomicInteger count = new AtomicInteger(0);
        private volatile long firstCallTime = System.currentTimeMillis();

        public void increment() {
            if (count.get() == 0) {
                firstCallTime = System.currentTimeMillis();
            }
            count.incrementAndGet();
        }

        public int getCount() {
            return count.get();
        }

        public long getFirstCallTime() {
            return firstCallTime;
        }

        public void reset(long time) {
            count.set(0);
            firstCallTime = time;
        }
    }
}

