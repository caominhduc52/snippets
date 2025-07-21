package com.duccao.demo.share.exceptions;

import java.io.Serial;
import lombok.Getter;

@Getter
public class BadRequestOnDownstreamException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;

  private final ApiError apiError;

  public BadRequestOnDownstreamException(String message, DownstreamException cause) {
    super(message, cause);
    this.apiError = cause.getApiError();
  }
}
