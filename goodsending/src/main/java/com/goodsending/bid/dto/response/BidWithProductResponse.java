package com.goodsending.bid.dto.response;

import com.goodsending.product.dto.response.ProductSummaryDto;
import com.querydsl.core.annotations.QueryProjection;

/**
 * @Date : 2024. 08. 05.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
public record BidWithProductResponse(

    Long bidId,

    Integer bidPrice,

    Integer usePoint,

    Long memberId,

    ProductSummaryDto productSummaryDto

) {

  @QueryProjection
  public BidWithProductResponse {
  }

  public Integer getUseCash(){
    return this.bidPrice - this.usePoint;
  }
}
