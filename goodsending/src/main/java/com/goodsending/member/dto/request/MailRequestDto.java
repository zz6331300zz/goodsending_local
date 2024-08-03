package com.goodsending.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MailRequestDto {

  @NotBlank(message = "이메일이 입력되지 않았습니다.")
  @Email
  private String email;
}
