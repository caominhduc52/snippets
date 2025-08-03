package com.duccao.demo.share.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = "httpStatus")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiError implements Serializable {
  private static final long serialVersionUID = 1L;
  private String errorId;
  private String message;
  private String informationLink;
  private List<ApiErrorDetails> details;

  @JsonIgnore
  private HttpStatus httpStatus;

  public List<ApiErrorDetails> getDetails() {
    return Collections.unmodifiableList(details);
  }
}
