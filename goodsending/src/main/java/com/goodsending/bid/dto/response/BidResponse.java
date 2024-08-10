package com.goodsending.bid.dto.response;

import com.goodsending.bid.entity.Bid;
import lombok.Builder;

/**
 * @Date : 2024. 07. 25.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
@Builder
public record BidResponse(

    Long bidId,

    Integer price,

    Integer usePoint,

    Long memberId,

    Long productId,

    Integer biddingCount,

    Integer bidderCount

) {

  public static BidResponse from(Bid bid) {
    return BidResponse.builder()
        .bidId(bid.getId())
        .price(bid.getPrice())
        .usePoint(bid.getUsePoint())
        .memberId(bid.getMember().getMemberId())
        .productId(bid.getProduct().getId())
        .biddingCount(bid.getProduct().getBiddingCount())
        .bidderCount(bid.getProduct().getBidderCount())
        .build();
  }
}
