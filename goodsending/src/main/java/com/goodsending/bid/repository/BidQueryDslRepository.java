package com.goodsending.bid.repository;

import com.goodsending.bid.dto.request.BidListByMemberRequest;
import com.goodsending.bid.dto.response.BidWithProductResponse;
import com.goodsending.bid.entity.Bid;
import java.util.List;
import org.springframework.data.domain.Slice;

/**
 * @Date : 2024. 08. 01.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
public interface BidQueryDslRepository {

  Long countByProduct(Long productId);

  Long countDistinctMembersByProduct(Long productId);

  List<Bid> findByProductId(Long productId);

  Slice<BidWithProductResponse> findBidWithProductResponseList(BidListByMemberRequest bidListRequest);
}
