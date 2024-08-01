package com.goodsending.bid.service;

import com.goodsending.bid.dto.request.BidRequest;
import com.goodsending.bid.dto.response.BidResponse;
import java.time.LocalDateTime;

/**
 * @Date : 2024. 07. 25.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
public interface BidService {

  BidResponse create(Long memberId, BidRequest request, LocalDateTime now);
}
