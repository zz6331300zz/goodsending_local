package com.goodsending.global.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException{
  private final HttpStatus statusCode;

  public static CustomException of(ExceptionCode exceptionCode) {
    return new CustomException(exceptionCode.getStatus(), exceptionCode.getMessage());
  }

  private CustomException(HttpStatus statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public HttpStatus getStatusCode() {
    return statusCode;
  }
}
