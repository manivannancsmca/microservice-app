package com.inventory_service.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

     // Pointcut: all methods in controller package
    @Pointcut("execution(* com.inventory_service.controller..*(..))")
    public void controllerMethods() {}
    
    // Before advice
    @Before("controllerMethods()")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Entering method: {} with arguments: {}",
                joinPoint.getSignature(),
                joinPoint.getArgs());
    }

    // After returning advice
    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Method {} executed successfully, return value: {}",
                joinPoint.getSignature(),
                result);
    }

    // After throwing advice
    @AfterThrowing(pointcut = "controllerMethods()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        log.error("Method {} threw exception: {}",
                joinPoint.getSignature(),
                ex.getMessage(), ex);
    }

    // After (finally) advice
    @After("controllerMethods()")
    public void logAfterFinally(JoinPoint joinPoint) {
        log.info("Exiting method: {}", joinPoint.getSignature());
    }

    // Around advice for execution time
    @Around("controllerMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            log.info("Method {} executed in {} ms, return value: {}",
                    joinPoint.getSignature(),
                    duration,
                    result);
            return result;
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - start;
            log.error("Method {} failed after {} ms with exception: {}",
                    joinPoint.getSignature(),
                    duration,
                    ex.getMessage(), ex);
            throw ex;
        }
    }
}

