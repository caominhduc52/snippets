package com.duccao.demo.share.logging;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

@Slf4j
public class MdcAwareCallableDecorator<T> implements Callable<T> {

  private final Callable<T> callable;
  private final Map<String, String> contextMap;

  public MdcAwareCallableDecorator(Callable<T> callable) {
    this.callable = callable;
    this.contextMap = MDC.getCopyOfContextMap();
  }

  public Callable<T> getCallable() {
    return callable;
  }

  public Map<String, String> getContextMap() {
    return contextMap;
  }

  @Override
  public T call() throws Exception {
    if (contextMap != null) {
      MDC.setContextMap(contextMap);
      log.trace("this=\"{}\", current_thread.name=\"{}\" context_map=\"{}\"", this, Thread.currentThread().getName(),
          this.contextMap);
    } else {
      log.error("message=\"MDC contextMap is null. {} objects must have at least the correlation ID\"",
          this.getClass().getName());
    }

    try {
      return callable.call();
    } finally {
      MDC.clear();
    }
  }

  @Override
  public String toString() {
    return "MdcAwareCallableDecorator{"
        + "callable="
        + callable
        + ", contextMap="
        + contextMap
        + '}';
  }
}
