package com.goodsending.productmessage.dto.request;

/**
 * @Date : 2024. 08. 08.
 * @Team : GoodsEnding
 * @author : jieun(je-pa)
 * @Project : goodsending-be :: goodsending
 */
public record ProductMessageListRequest(
    Long productId,
    int size,
    Long cursorId
) {
  public static ProductMessageListRequest of(Long productId, int size, Long cursorId){
    return new ProductMessageListRequest(productId, size, cursorId);
  }
}
