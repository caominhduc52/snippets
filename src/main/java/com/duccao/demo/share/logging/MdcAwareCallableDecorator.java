package com.duccao.demo.share.logging;

import java.util.Map;
import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
public class MdcAwareCallableDecorator<T> implements Callable<T> {

  private final Callable<T> callable;
  private final Map contextMap;

  public MdcAwareCallableDecorator(Callable<T> callable) {
    this.callable = callable;
    this.contextMap = MDC.getCopyOfContextMap();
    log.trace("this=\"{}\", current_thread.name=\"{}\" context_map=\"{}\"", this, Thread.currentThread().getName(), this.contextMap);
  }

  public Callable<T> getCallable() {
    return callable;
  }

  public Map getContextMap() {
    return contextMap;
  }

  @Override
  public T call() throws Exception {
    if (contextMap != null) {
      MDC.setContextMap(contextMap);
      log.trace("this=\"{}\", current_thread.name=\"{}\" context_map=\"{}\"", this, Thread.currentThread().getName(), this.contextMap);
    } else {
      log.error("message=\"MDC contextMap is null. {} objects must have at least the correlation ID\"", this.getClass().getName());
    }

    try {
      return callable.call();
    } finally {
      MDC.clear();
    }
  }

  @Override
  public String toString() {
    return "MdcAwareCallableDecorator{" +
        "callable=" + callable +
        ", contextMap=" + contextMap +
        '}';
  }
}
