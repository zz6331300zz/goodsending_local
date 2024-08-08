package com.goodsending.global.websocket.dto;

import com.goodsending.productmessage.entity.ProductMessageHistory;
import com.goodsending.productmessage.type.MessageType;
import lombok.Builder;

/**
 * @Date : 2024. 08. 07.
 * @Team : GoodsEnding
 * @author : jieun(je-pa)
 * @Project : goodsending-be :: goodsending
 */
@Builder
public record ProductMessageDto(
    Long memberId,
    Long productId,
    String message,
    int price,
    int biddingCount,
    int bidderCount,
    MessageType type
) {
  public static ProductMessageDto of(ProductMessageHistory history, int price){
    return ProductMessageDto.builder()
        .memberId(history.getMember().getMemberId())
        .productId(history.getProduct().getId())
        .message(history.getMessage())
        .price(price)
        .biddingCount(history.getProduct().getBiddingCount())
        .bidderCount(history.getProduct().getBidderCount())
        .type(history.getType())
        .build();
  }
}
