package com.duccao.demo.share.exceptions;

import java.util.List;

public class BadRequestException extends ServiceException {
  private static final long serialVersionUID = -1L;
  private static final String ERROR_ID = "API_ERROR_BAD_REQUEST";
  private static final String MESSAGE = "Invalid Request";

  public BadRequestException(String errorId, String msg, List<ApiErrorDetails> errorDetails) {
    super(ERROR_ID, MESSAGE, errorDetails);
  }

  public BadRequestException(String errorId, String msg, List<ApiErrorDetails> errorDetails, Throwable cause) {
    super(ERROR_ID, MESSAGE, errorDetails, cause);
  }

  public BadRequestException(List<ApiErrorDetails> errorDetails) {
    super(ERROR_ID, MESSAGE, errorDetails);
  }
}
