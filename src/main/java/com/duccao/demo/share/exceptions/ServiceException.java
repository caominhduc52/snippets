package com.duccao.demo.share.exceptions;

import lombok.Getter;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Getter
public class ServiceException extends RuntimeException implements Serializable {
  private static final long serialVersionUid = 1L;

  private final String errorId;
  private final transient List<ApiErrorDetails> errorDetails;

  public ServiceException(String errorId, String msg, List<ApiErrorDetails> errorDetails) {
    super(msg);
    this.errorId = errorId;
    this.errorDetails = errorDetails;
  }

  public ServiceException(
      String errorId, String msg, List<ApiErrorDetails> errorDetails, Throwable cause) {
    super(msg, cause);
    this.errorId = errorId;
    this.errorDetails = errorDetails;
  }

  public List<ApiErrorDetails> getErrorDetails() {
    return Collections.unmodifiableList(errorDetails);
  }
}
