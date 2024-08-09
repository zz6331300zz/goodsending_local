package com.goodsending.productmessage.dto.request;

import com.goodsending.product.dto.request.ChatMessageRequest;
import com.goodsending.productmessage.type.MessageType;
import lombok.Builder;

/**
 * @Date : 2024. 08. 09.
 * @Team : GoodsEnding
 * @author : jieun(je-pa)
 * @Project : goodsending-be :: goodsending
 */
@Builder
public record ProductMessageRequest(
    String memberEmail,
    Long productId,
    String message,
    MessageType type
) {
  public static ProductMessageRequest of(ChatMessageRequest request, String memberEmail){
    return ProductMessageRequest.builder()
        .memberEmail(memberEmail)
        .productId(request.productId())
        .message(request.message())
        .type(request.type())
        .build();
  }
}
