package com.goodsending.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class LoginRequestDto {

  @NotBlank(message = "이메일이 입력되지 않았습니다.")
  @Email
  private String email;

  @NotBlank(message = "비밀번호가 입력되지 않았습니다.")
  @ValidPassword
  private String password;
}
