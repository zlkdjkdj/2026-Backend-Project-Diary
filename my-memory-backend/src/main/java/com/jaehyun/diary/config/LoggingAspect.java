package com.jaehyun.diary.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(com.jaehyun.diary.api..*) || within(com.jaehyun.diary.service..*)")
    public void diaryTrackerPointcut() {
    }

    @Around("diaryTrackerPointcut()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.info("[AOP START] Executing: {}.{}", className, methodName);

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            log.info("[AOP END] Completed: {}.{} in {} ms", className, methodName, duration);
            return result;
        } catch (Throwable throwable) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("[AOP ERROR] Failed: {}.{} in {} ms with exception: {}", 
                    className, methodName, duration, throwable.getMessage());
            throw throwable;
        }
    }
}
