package com.goodsending.global.exception.handler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.handler.dto.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionResponse> handleException(Exception e, HttpServletRequest request) {
    HttpStatus status = INTERNAL_SERVER_ERROR;
    log(e, request, status);
    return ResponseEntity.status(status).body(ExceptionResponse.of(status, e));
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ExceptionResponse> handleException(RuntimeException e, HttpServletRequest request) {
    HttpStatus status = BAD_REQUEST;
    log(e, request, status);
    return ResponseEntity.status(status).body(ExceptionResponse.of(status, e));
  }

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ExceptionResponse> handleException(CustomException e, HttpServletRequest request) {
    log(e, request, e.getStatusCode());
    return ResponseEntity.status(e.getStatusCode()).body(
        ExceptionResponse.of(e.getStatusCode(), e));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException e, HttpServletRequest request) {
    HttpStatus status = BAD_REQUEST;
    log(e, request, status);
    return ResponseEntity.status(status).body(ExceptionResponse.of(status, e));
  }

  // TODO: security 적용후 주석 제거
//  @ExceptionHandler(AuthorizationDeniedException.class)
//  public ResponseEntity<ApiResponse<Void>> handleException(
//      AuthorizationDeniedException e, HttpServletRequest request) {
//    HttpStatus status = FORBIDDEN;
//    log(e, request, status);
//    return ResponseEntity.status(status).body(ApiResponse.of(status, e));
//  }

  private static void log(Exception e, HttpServletRequest request, HttpStatus status) {
    log.error("{}:{}:{}:{}"
        , request.getRequestURI(), status.value(), e.getClass().getSimpleName(), e.getMessage());
  }
}
