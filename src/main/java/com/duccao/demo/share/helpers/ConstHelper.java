package com.duccao.demo.share.helpers;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConstHelper {
  public static final String TRACE_LOG_TEMPLATE = "func={}, msg={}";
  public static final String TRACE_LOG_WITH_COR_TEMPLATE = "correlationId={}, func={}, msg={}";
  public static final String TRACE_ERROR_LOG_TEMPLATE = "func={}, msg={}, alocRates={}";
}
