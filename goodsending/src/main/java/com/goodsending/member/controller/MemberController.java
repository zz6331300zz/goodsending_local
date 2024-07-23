package com.goodsending.member.controller;

import com.goodsending.global.security.anotation.MemberId;
import com.goodsending.member.dto.SignupRequestDto;
import com.goodsending.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

  private final MemberService memberService;

  // 회원 가입
  @Operation(summary = "회원 가입 기능", description = "이메일, 비밀번호, 전화번호 입력하면 회원 가입 된다.")
  @PostMapping("/members/signup")
  public ResponseEntity<?> signup(@RequestBody @Valid SignupRequestDto signupRequestDto){

    return memberService.signup(signupRequestDto);
  }



}
