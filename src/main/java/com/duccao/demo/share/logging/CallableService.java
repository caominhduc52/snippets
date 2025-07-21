package com.duccao.demo.share.logging;

import java.util.concurrent.Callable;

public interface CallableService {
  <T> Callable<T> wrap(Callable<T> callable);
}
