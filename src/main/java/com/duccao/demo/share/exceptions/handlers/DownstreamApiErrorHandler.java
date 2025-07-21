package com.duccao.demo.share.exceptions.handlers;

import com.duccao.demo.share.exceptions.ErrorDetails;
import com.duccao.demo.share.helpers.ErrorHelper;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class DownstreamApiErrorHandler {
  private final ErrorHelper errorHelper;

  public Function<ClientResponse, Mono<? extends Throwable>> handleError(String commandName) {
    return response -> {
      HttpStatusCode httpStatusCode = response.statusCode();
      return response.bodyToMono(ErrorDetails.class)
          .flatMap(apiError -> {
            if (log.isDebugEnabled()) {
              log.debug("message=\"Error occurs with commandName={}, errorId={}, statusCode={}\"", commandName,
                  apiError.getErrorId(), httpStatusCode);
            }
            return buildErrorResponse(apiError, httpStatusCode);
          });
    };
  }

  private Mono<? extends Throwable> buildErrorResponse(ErrorDetails apiError, HttpStatusCode httpStatusCode) {
    if (httpStatusCode.is4xxClientError()) {
      return Mono.just(errorHelper.mapBadRequestErrors(apiError, httpStatusCode));
    }
    return Mono.just(errorHelper.mapInternalServerErrors(apiError, httpStatusCode));
  }
}
