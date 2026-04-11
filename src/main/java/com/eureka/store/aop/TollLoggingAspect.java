package com.eureka.store.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class TollLoggingAspect {

    @Around("@annotation(com.eureka.store.annotation.LogEvent)")
    public Object logTollActivity(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        long start = System.currentTimeMillis();

        log.info(">>> [START] Method: {} | Payload: {}", methodName, Arrays.toString(args));

        try {
            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - start;
            log.info("<<< [SUCCESS] Method: {} | Duration: {}ms", methodName, duration);
            return result;
        } catch (Exception ex) {
            log.error("!!! [FAILED] Method: {} | Error: {}", methodName, ex.getMessage());
            throw ex;
        }
    }
}