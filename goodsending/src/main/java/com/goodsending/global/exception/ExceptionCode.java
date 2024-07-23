package com.goodsending.global.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.PAYLOAD_TOO_LARGE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {

  // BAD_REQUEST:400:잘못된요청

  // Unauthorized:401:인증이슈

  // FORBIDDEN:403:권한이슈

  // NOT_FOUND:404:자원없음
  USER_NOT_FOUND(NOT_FOUND, "유저 개체를 찾지 못했습니다."),

  // PAYLOAD_TOO_LARGE:413:파일 크기 초과
  FILE_SIZE_EXCEEDED(PAYLOAD_TOO_LARGE, "파일 크기가 10MB를 초과했습니다."),

  // INTERNAL_SERVER_ERROR:500:서버 문제 발생
  LOW_DISK_SPACE(INTERNAL_SERVER_ERROR, "디스크 공간이 부족합니다."),
  FILE_UPLOAD_FAILED(INTERNAL_SERVER_ERROR, "파일 변환에 실패했습니다.");

  private final HttpStatus status;
  private final String message;

}