package com.goodsending.bid.repository;

import com.goodsending.bid.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Date : 2024. 07. 25.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
public interface BidRepository extends JpaRepository<Bid, Long>, BidQueryDslRepository {

}
