package com.goodsending.global.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.PAYLOAD_TOO_LARGE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.atn.SemanticContext.OR;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {

  // BAD_REQUEST:400:잘못된요청
  INSUFFICIENT_USER_CASH(BAD_REQUEST, "입력한 금액이 유저 캐시보다 큽니다."),
  INSUFFICIENT_USER_POINT(BAD_REQUEST, "입력한 금액이 유저 포인트보다 큽니다."),
  EXCESSIVE_POINT(BAD_REQUEST, "포인트가 신청 입찰금을 초과합니다."),
  INSUFFICIENT_BID_AMOUNT(BAD_REQUEST, "경매 기본가가 입력한 금액보다 큽니다."),
  BID_AMOUNT_LESS_THAN_CURRENT_MAX(BAD_REQUEST, "현재 최고 입찰 금액이 입력한 금액보다 큽니다."),
  USER_CASH_MUST_BE_POSITIVE(BAD_REQUEST, "유저 캐시는 양수여야 합니다."),
  USER_POINT_MUST_BE_POSITIVE(BAD_REQUEST, "유저 포인트는 양수여야 합니다."),
  AUCTION_ALREADY_WON(BAD_REQUEST, "이미 낙찰된 경매입니다."),
  AUCTION_ALREADY_CLOSED(BAD_REQUEST, "이미 마감된 경매입니다."),
  AUCTION_NOT_STARTED(BAD_REQUEST, "경매가 아직 시작되지 않았습니다."),
  FILE_COUNT_EXCEEDED(BAD_REQUEST, "상품 이미지 개수가 최대 5개를 초과했습니다."),
  PASSWORD_MISMATCH(BAD_REQUEST, "두 비밀번호가 일치하지 않습니다."),
  VERIFICATION_CODE_MISMATCH(BAD_REQUEST, "인증코드가 일치하지 않습니다."),
  MEMBER_ID_MISMATCH(BAD_REQUEST, "회원 아이디가 일치하지 않습니다."),
  BIDDER_ALREADY_EXIST(BAD_REQUEST, "입찰자가 이미 존재합니다."),

  // Unauthorized:401:인증이슈
  EMAIL_NOT_VERIFIED(UNAUTHORIZED, "이메일 인증이 되지 않았습니다."),
  MEMBER_PASSWORD_INCORRECT(UNAUTHORIZED, "현재 비밀번호가 일치하지 않습니다."),
  INVALID_TOKEN(UNAUTHORIZED, "토큰이 유효하지 않습니다."),

  // FORBIDDEN:403:권한이슈

  // NOT_FOUND:404:자원없음
  PRODUCT_NOT_FOUND(NOT_FOUND, "경매 상품 개체를 찾지 못했습니다."),
  MEMBER_NOT_FOUND(NOT_FOUND, "회원 개체를 찾지 못했습니다."),
  USER_NOT_FOUND(NOT_FOUND, "유저 개체를 찾지 못했습니다."),
  PRODUCTIMAGE_NOT_FOUND(NOT_FOUND, "경매 상품 이미지를 찾지 못했습니다."),
  LIKE_NOT_FOUND(NOT_FOUND, "찜 개체를 찾지 못했습니다."),
  BID_NOT_FOUND(NOT_FOUND, "입찰 개체를 찾지 못했습니다."),
  STOMP_HEADER_ACCESSOR_NOT_FOUND_EXCEPTION(NOT_FOUND, "메시지에서 STOMP 헤더 접근자를 가져오지 못했습니다."),
  CODE_EXPIRED_OR_INVALID(NOT_FOUND, "인증 코드가 만료되었거나 존재하지 않습니다."),

  // CONFLICT:409:충돌
  EMAIL_ALREADY_EXISTS(CONFLICT, "중복된 이메일 입니다."),

  // PAYLOAD_TOO_LARGE:413:파일 크기 초과
  FILE_SIZE_EXCEEDED(PAYLOAD_TOO_LARGE, "파일 크기가 10MB를 초과했습니다."),

  // UNPROCESSABLE_ENTITY:422:의미론적 오류
  FILENAME_DECODE_FAILED(UNPROCESSABLE_ENTITY, "파일명 디코드에 실패했습니다."),

  // INTERNAL_SERVER_ERROR:500:서버 문제 발생
  LOW_DISK_SPACE(INTERNAL_SERVER_ERROR, "디스크 공간이 부족합니다."),
  FILE_UPLOAD_FAILED(INTERNAL_SERVER_ERROR, "파일 변환에 실패했습니다."),
  ALGORITHM_NOT_AVAILABLE(INTERNAL_SERVER_ERROR, "알고리즘을 찾을 수 없습니다."),
  EMAIL_SENDING_FAILED(INTERNAL_SERVER_ERROR, "이메일 전송에 실패했습니다.");

  private final HttpStatus status;
  private final String message;

}