package com.goodsending.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class PasswordRequestDto {

  @NotBlank(message = "비밀번호가 입력되지 않았습니다.")
  private String currentPassword; // 현재 비밀번호

  @NotBlank(message = "비밀번호가 입력되지 않았습니다.")
  @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]{8,15}$")
  private String password; // 새로운 비밀번호

  @NotBlank(message = "비밀번호가 입력되지 않았습니다.")
  private String confirmPassword; // DB x, 단순 체크 용
}
