package com.goodsending.product.dto.request;

import com.goodsending.productmessage.type.MessageType;

/**
 * @Date : 2024. 08. 09.
 * @Team : GoodsEnding
 * @author : jieun(je-pa)
 * @Project : goodsending-be :: goodsending
 */
public record ChatMessageRequest(
    Long productId,
    String message,
    MessageType type
) {

}
