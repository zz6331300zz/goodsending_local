package com.goodsending.order.repository;

import static com.goodsending.bid.entity.QBid.bid;
import static com.goodsending.member.entity.QMember.member;
import static com.goodsending.order.entity.QOrder.order;
import static com.goodsending.product.entity.QProduct.product;

import com.goodsending.order.entity.Order;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * @author : jieun(je-pa)
 * @Date : 2024. 08. 03.
 * @Team : GoodsEnding
 * @Project : goodsending-be :: goodsending
 */
@Repository
@RequiredArgsConstructor
public class OrderQueryDslRepositoryImpl implements  OrderQueryDslRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public Optional<Order> findOrderWithBidById(Long orderId) {
    return Optional.ofNullable(queryFactory
        .selectFrom(order)
        .leftJoin(order.bid, bid).fetchJoin()
        .where(order.id.eq(orderId))
        .fetchOne());
  }

  @Override
  public Optional<Order> findOrderWithBidAndProductById(Long orderId) {
    return Optional.ofNullable(queryFactory
        .selectFrom(order)
        .innerJoin(order.bid, bid).fetchJoin()
        .innerJoin(order.bid.product, product).fetchJoin()
        .where(order.id.eq(orderId))
        .fetchOne());
  }

  @Override
  public Optional<Order> findOrderWithBidAndProductAndSellerById(Long orderId) {
    return Optional.ofNullable(queryFactory
        .selectFrom(order)
        .innerJoin(order.bid, bid).fetchJoin()
        .innerJoin(order.bid.product, product).fetchJoin()
        .innerJoin(product.member, member).fetchJoin()
        .where(order.id.eq(orderId))
        .fetchOne());
  }
}
