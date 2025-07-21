package com.duccao.demo.share.helpers;

import com.duccao.demo.share.exceptions.ApiError;
import com.duccao.demo.share.exceptions.ApiErrorDetails;
import com.duccao.demo.share.exceptions.BadRequestException;
import com.duccao.demo.share.exceptions.BadRequestOnDownstreamException;
import com.duccao.demo.share.exceptions.DownstreamException;
import com.duccao.demo.share.exceptions.ErrorDetails;
import com.duccao.demo.share.exceptions.InternalServerException;
import com.duccao.demo.share.exceptions.UnAuthorizedException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ErrorHelper {

  public BadRequestOnDownstreamException mapBadRequestErrors(ErrorDetails errorResponse,
                                                             HttpStatusCode httpStatusCode) {
    HttpStatus httpStatus = HttpStatus.valueOf(httpStatusCode.value());
    errorResponse.setHttpStatus(httpStatus);
    return new BadRequestOnDownstreamException("Client error", new DownstreamException(
        "Downstream business error: ", buildApiError(errorResponse, httpStatus)
    ));
  }

  public DownstreamException mapInternalServerErrors(ErrorDetails errorResponse,
                                                     HttpStatusCode httpStatusCode) {
    HttpStatus httpStatus = HttpStatus.valueOf(httpStatusCode.value());
    errorResponse.setHttpStatus(httpStatus);
    return new DownstreamException("Downstream server error: ", buildApiError(errorResponse, httpStatus));
  }

  public static InternalServerException buildInternalException(Exception initCause, String issue) {
    return (InternalServerException) new InternalServerException(
        List.of(ApiErrorDetails.builder().issue(issue).build()))
        .initCause(initCause);
  }

  public static BadRequestException buildBadRequestException(String fieldName, String value, String issue) {
    return new BadRequestException(
        List.of(ApiErrorDetails.builder()
            .field(fieldName)
            .issue(issue)
            .value(value)
            .location("query")
            .build())
    );
  }

  public static BadRequestException buildBadRequestException(String fieldName, String issue) {
    return new BadRequestException(
        List.of(ApiErrorDetails.builder()
            .field(fieldName)
            .issue(issue)
            .location("query")
            .build())
    );
  }

  public static BadRequestException buildBadRequestException(Exception initCause, String fieldName, String issue,
                                                             String value) {
    return (BadRequestException) new BadRequestException(
        List.of(ApiErrorDetails.builder()
            .field(fieldName)
            .issue(issue)
            .value(value)
            .location("query")
            .build())
    ).initCause(initCause);
  }

  public static UnAuthorizedException buildUnauthorizedException(String fieldName, String value, String issue) {
    return new UnAuthorizedException(
        List.of(ApiErrorDetails.builder()
            .field(fieldName)
            .issue(issue)
            .value(value)
            .build())
    );
  }

  private ApiError buildApiError(ErrorDetails errorResponse, HttpStatus httpStatus) {
    log.info("message=\"Error response={}\"", errorResponse);
    return ApiError.builder()
        .details(errorResponse.getDetails())
        .message(errorResponse.getMessage())
        .errorId(errorResponse.getErrorId())
        .httpStatus(httpStatus)
        .build();
  }
}
