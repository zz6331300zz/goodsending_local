package com.goodsending.bid.dto.response;

import java.time.Duration;
import lombok.Builder;

/**
 * @Date : 2024. 07. 25.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
@Builder
public record BidWithDurationResponse(

    Long bidId,

    Integer price,

    Integer usePoint,

    Long memberId,

    Long productId,

    Integer biddingCount,

    Duration remainDuration
) {

  public static BidWithDurationResponse of(BidResponse bid, Duration remainDuration) {
    return BidWithDurationResponse.builder()
        .bidId(bid.bidId())
        .price(bid.price())
        .usePoint(bid.usePoint())
        .memberId(bid.memberId())
        .productId(bid.productId())
        .biddingCount(bid.biddingCount())
        .remainDuration(remainDuration)
        .build();
  }
}
