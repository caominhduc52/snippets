package com.duccao.demo.share.tracing;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class TraceMethodExecutionTimeLogger {
  private static final String TRACING_LOG_TEMPLATE = "func={}, elapsed_time_ms={}";

  @Around("@annotation(com.duccao.demo.share.tracing.TraceMethodExecutionTime)")
  public Object traceMethodExecution(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    final long startTime = System.currentTimeMillis();
    final String methodName = proceedingJoinPoint.getSignature().toShortString();
    final Object result = proceedingJoinPoint.proceed();
    final long elapsedTime = System.currentTimeMillis() - startTime;
    log.debug(TRACING_LOG_TEMPLATE, methodName, elapsedTime);
    return result;
  }
}
