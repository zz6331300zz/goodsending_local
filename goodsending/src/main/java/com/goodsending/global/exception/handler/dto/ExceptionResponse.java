package com.goodsending.global.exception.handler.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ExceptionResponse {

  private int code;
  private HttpStatus status;
  private String message;

  public ExceptionResponse(HttpStatus status, Exception ex) {
    this.code = status.value();
    this.status = status;
    this.message = ex.getMessage();
  }

  public static ExceptionResponse of(HttpStatus httpStatus, Exception ex) {
    return new ExceptionResponse(httpStatus, ex);
  }

}