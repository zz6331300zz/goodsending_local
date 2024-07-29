package com.goodsending.productlike.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LikeRequestDto {

    @NotNull(message = "상품Id를 입력해주세요.")
    private Long productId;

    @NotNull(message = "클릭 여부를 입력해주세요.")
    private boolean press;


}
