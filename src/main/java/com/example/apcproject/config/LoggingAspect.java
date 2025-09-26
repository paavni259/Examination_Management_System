package com.example.apcproject.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.example.apcproject.service..*(..)) || execution(* com.example.apcproject.controller..*(..))")
    public void before(JoinPoint jp) {
        log.info("Entering: {} args={} ", jp.getSignature(), jp.getArgs());
    }

    @AfterReturning(pointcut = "execution(* com.example.apcproject.service..*(..)) || execution(* com.example.apcproject.controller..*(..))", returning = "result")
    public void after(JoinPoint jp, Object result) {
        log.info("Exiting: {} return={} ", jp.getSignature(), result);
    }
}


