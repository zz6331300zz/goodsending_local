package com.goodsending.bid.repository;

import com.goodsending.bid.entity.Bid;
import com.goodsending.bid.entity.QBid;
import com.goodsending.member.entity.QMember;
import com.goodsending.product.entity.QProduct;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * @Date : 2024. 08. 01.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
@Repository
@RequiredArgsConstructor
public class BidQueryDslRepositoryImpl implements BidQueryDslRepository {
  private final JPAQueryFactory queryFactory;

  @Override
  public List<Bid> findByProductId(Long productId) {
    QBid bid = QBid.bid;
    return queryFactory
            .selectFrom(bid)
            .innerJoin(bid.product, QProduct.product).fetchJoin()
            .innerJoin(bid.member, QMember.member).fetchJoin()
            .where(bid.product.id.eq(productId))
            .orderBy(bid.price.desc())
            .fetch();
  }
}
