package com.goodsending.member.controller;

import com.goodsending.member.dto.request.MailRequestDto;
import com.goodsending.member.service.MailService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
  public ResponseEntity<String> sendCode(@RequestBody @Valid MailRequestDto mailRequestDto)
      throws MessagingException, UnsupportedEncodingException {
    return mailService.sendCode(mailRequestDto);
  }

  /**
   * 인증코드 확인
   * <p>
   * 유저가 입력한 인증코드가 일치하는지 확인합니다.
   *
   * @param email, code
   * @return mailService를 반환합니다.
   * @author : 이아람
   */
  @Operation(summary = "인증코드 확인", description = "redis에 저장된 인증코드 번호와 일치하는지 확인")
  @GetMapping("/members/checkCode")
  public ResponseEntity<String> checkCode(@RequestParam String email, @RequestParam String code) {
    return mailService.checkCode(email, code);
  }

}
