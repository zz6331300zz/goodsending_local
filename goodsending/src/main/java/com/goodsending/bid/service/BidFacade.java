package com.goodsending.bid.service;

import com.goodsending.bid.dto.request.BidRequest;
import com.goodsending.bid.dto.response.BidWithDurationResponse;
import java.time.LocalDateTime;

/**
 * @Date : 2024. 07. 30.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
public interface BidFacade {
  BidWithDurationResponse create(Long memberId, BidRequest request, LocalDateTime now)
      throws InterruptedException;
}
