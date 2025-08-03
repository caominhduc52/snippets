package com.duccao.demo.share.tracing;

import com.duccao.demo.share.helpers.ConstHelper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
public class TraceMethodExecutionLogger {

  @Around("@annotation(com.duccao.demo.share.tracing.TraceMethodExecution)")
  public Object traceMethodExecution(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();

    String methodName = methodSignature.getName();
    log.debug(ConstHelper.TRACE_LOG_TEMPLATE, methodName, "Starting");
    String className = methodSignature.getDeclaringType().getSimpleName();
    StopWatch stopWatch = new StopWatch();
    stopWatch.start(methodName);
    Object result = proceedingJoinPoint.proceed();
    stopWatch.stop();
    log.debug("message=\"Invoked '{} -> {}'\", elapsed_time_ms=\"{}\"ms", className, methodName,
        stopWatch.getTotalTimeMillis());
    log.debug(ConstHelper.TRACE_LOG_TEMPLATE, methodName, "Ending");
    return result;
  }
}
