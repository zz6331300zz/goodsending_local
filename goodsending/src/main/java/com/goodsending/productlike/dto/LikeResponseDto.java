package com.goodsending.productlike.dto;

import com.goodsending.productlike.type.LikeStatus;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class LikeResponseDto {
    private final int code;
    private final HttpStatus status;
    private final LikeStatus message;
    private LikeResponseDto(HttpStatus status, LikeStatus message) {
        this.code = status.value();
        this.status = status;
        this.message = message;
    }

    public static LikeResponseDto of(HttpStatus httpStatus, LikeStatus message) {
        return new LikeResponseDto(httpStatus, message);
    }

}
