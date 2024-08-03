package com.goodsending.order.repository;

import com.goodsending.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Date : 2024. 08. 01.
 * @Team : GoodsEnding
 * @author : jieun(je-pa)
 * @Project : goodsending-be :: goodsending
 */
public interface OrderRepository extends JpaRepository<Order, Long>, OrderQueryDslRepository {

}
