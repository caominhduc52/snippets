package com.duccao.demo.share.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

/**
 * Record representing detailed information about an API error.
 * This record is used to provide structured error information
 * when exceptions occur during API operations.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ApiErrorDetails(
    String field,
    String value,
    String location,
    String issue
) { }
