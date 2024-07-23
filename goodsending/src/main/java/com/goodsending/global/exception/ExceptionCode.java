package com.goodsending.global.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

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
  PRODUCT_NOT_FOUND(NOT_FOUND, "상품 개체를 찾지 못했습니다."),
  MEMBER_NOT_FOUND(NOT_FOUND, "회원 개체를 찾지 못했습니다."),
  NEGATIVE_LIKE_COUNT(BAD_REQUEST, "좋아요 수는 음수가 될 수 없습니다."),
  CANNOT_DECREASE_LIKE_COUNT(BAD_REQUEST, "좋아요 수를 차감할 수 없는 상태입니다.");

  private final HttpStatus status;
  private final String message;

}