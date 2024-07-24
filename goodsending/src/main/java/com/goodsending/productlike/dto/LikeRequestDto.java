package com.goodsending.productlike.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LikeRequestDto {

    @NotNull
    private Long productId;
    @NotNull
    private boolean clicked;


}
