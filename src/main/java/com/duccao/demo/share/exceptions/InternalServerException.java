package com.duccao.demo.share.exceptions;

import java.util.List;

public class InternalServerException extends ServiceException {
  private static final long serialVersionUID = -1L;
  private static final String ERROR_ID = "API_ERROR_INTERNAL_SERVER";
  private static final String MESSAGE = "Inter Server Error";

  public InternalServerException(String errorId, String msg, List<ApiErrorDetails> errorDetails) {
    super(ERROR_ID, MESSAGE, errorDetails);
  }

  public InternalServerException(String errorId, String msg, List<ApiErrorDetails> errorDetails, Throwable cause) {
    super(ERROR_ID, MESSAGE, errorDetails, cause);
  }

  public InternalServerException(List<ApiErrorDetails> errorDetails) {
    super(ERROR_ID, MESSAGE, errorDetails);
  }
}
