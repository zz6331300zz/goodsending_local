package com.goodsending.member.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CashRequestDto {

  @NotNull
  private Integer cash;

}
