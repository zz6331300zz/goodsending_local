package com.goodsending.bid.repository;

import com.goodsending.bid.entity.Bid;
import java.util.List;

/**
 * @Date : 2024. 08. 01.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
public interface BidQueryDslRepository {
  List<Bid> findByProductId(Long productId);
}
