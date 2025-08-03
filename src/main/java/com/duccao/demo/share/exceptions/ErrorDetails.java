package com.duccao.demo.share.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorDetails {
  private String errorId;
  private String message;
  private List<ApiErrorDetails> details;

  @JsonIgnore
  private HttpStatus httpStatus;

  public List<ApiErrorDetails> getDetails() {
    return Collections.unmodifiableList(details);
  }
}
