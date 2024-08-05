package com.goodsending.order.repository;

import com.goodsending.order.entity.Order;
import java.util.Optional;

/**
 * @author : jieun(je-pa)
 * @Date : 2024. 08. 03.
 * @Team : GoodsEnding
 * @Project : goodsending-be :: goodsending
 */
public interface OrderQueryDslRepository {
  Optional<Order> findOrderWithBidById(Long orderId);

  Optional<Order> findOrderWithBidAndProductById(Long orderId);
}
