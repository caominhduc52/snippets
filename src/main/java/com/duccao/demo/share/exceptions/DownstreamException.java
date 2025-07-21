package com.duccao.demo.share.exceptions;

import lombok.Getter;

@Getter
public class DownstreamException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  private final ApiError apiError;

  public DownstreamException(String message, ApiError apiError) {
    super(message);
    this.apiError = apiError;
  }
}
