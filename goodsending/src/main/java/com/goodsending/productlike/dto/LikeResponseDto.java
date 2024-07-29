package com.goodsending.productlike.dto;

import com.goodsending.productlike.entity.Like;
import lombok.Getter;

@Getter
public class LikeResponseDto {
    private Long id;
    public LikeResponseDto(Like like) {
        this.id = like.getId();
    }
}
