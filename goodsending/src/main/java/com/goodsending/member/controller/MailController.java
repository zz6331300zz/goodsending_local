package com.goodsending.member.controller;

import com.goodsending.member.service.MailService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @Date : 2024. 07. 27.
 * @Team : GoodsEnding
 * @author : 이아람
 * @Project : goodsending-be :: goodsending
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MailController {

  private final MailService mailService;

  /**
   * 인증코드 발송
   * <p>
   * 유저가 입력한 email 중복 확인 후 인증코드 발송합니다.
   *
   * @param 회원가입 하려는 유저의 email
   * @return mailService를 반환합니다.
   * @author : 이아람
   */
  @Operation(summary = "인증코드 발송 기능", description = "이메일 중복검사 후 인증코드 발송")
  @PostMapping("/members/sendMail")
  public ResponseEntity<String> sendCode(@RequestParam @Valid String email)
      throws MessagingException, UnsupportedEncodingException {
    return mailService.sendCode(email);
  }
}
