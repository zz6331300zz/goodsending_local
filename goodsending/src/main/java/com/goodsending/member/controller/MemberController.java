package com.goodsending.member.controller;

import com.goodsending.global.security.anotation.MemberId;
import com.goodsending.member.dto.request.SignupRequestDto;
import com.goodsending.member.dto.response.MemberInfoDto;
import com.goodsending.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @Date : 2024. 07. 23. / 2024. 07. 29
 * @Team : GoodsEnding
 * @author : 이아람
 * @Project : goodsending-be :: goodsending
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

  private final MemberService memberService;

  /**
   * 회원가입
   * <p>
   * 이메일 인증 이후 코드, 비밀번호 입력하면 회원가입 된다.
   *
   * @param SignupRequestDto
   * @return MemberService 반환합니다.
   * @author : 이아람
   */
  @Operation(summary = "회원 가입 기능", description = "이메일, 코드, 비밀번호 입력하면 회원 가입 된다.")
  @PutMapping("/members/signup")
  public ResponseEntity<String> signup(@RequestBody @Valid SignupRequestDto signupRequestDto) {

    return memberService.signup(signupRequestDto);
  }


  /**
   * 회원 정보 조회
   * <p>
   * 로그인 한 회원의 이메일, 캐시, 포인트, 권한을 조회할 수 있다.
   *
   * @param 로그인 한 유저의 memberId
   * @return MemberService 반환합니다.
   * @author : 이아람
   */
  @Operation(summary = "회원 정보 조회 기능", description = "로그인 한 회원 이메일, 캐시, 포인트, 권한 조회")
  @GetMapping("/member-info")
  public ResponseEntity<MemberInfoDto> getMemberInfo(@MemberId Long memberId) {

    return ResponseEntity.ok(memberService.getMemberInfo(memberId));
  }
}


