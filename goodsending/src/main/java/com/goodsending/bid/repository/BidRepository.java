package com.goodsending.bid.repository;

import com.goodsending.bid.entity.Bid;
import com.goodsending.product.entity.Product;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @Date : 2024. 07. 25.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
public interface BidRepository extends JpaRepository<Bid, Long> {
  @Query("SELECT COUNT(b) FROM Bid b WHERE b.product = :product")
  Long countByProduct(@Param("product") Product product);
}
