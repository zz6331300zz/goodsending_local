package com.goodsending.bid.repository;

import static com.goodsending.bid.entity.QBid.bid;
import static com.goodsending.member.entity.QMember.member;
import static com.goodsending.product.entity.QProduct.product;
import static com.goodsending.product.entity.QProductImage.productImage;

import com.goodsending.bid.dto.request.BidListByMemberRequest;
import com.goodsending.bid.dto.response.BidWithProductResponse;
import com.goodsending.bid.dto.response.QBidWithProductResponse;
import com.goodsending.bid.entity.Bid;
import com.goodsending.bid.entity.QBid;
import com.goodsending.product.dto.response.QProductSummaryDto;
import com.goodsending.product.entity.QProductImage;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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
            .innerJoin(bid.product, product).fetchJoin()
            .innerJoin(bid.member, member).fetchJoin()
            .where(bid.product.id.eq(productId))
            .orderBy(bid.price.desc())
            .fetch();
  }

  /**
   * 커서 기반으로 페이징한 멤버별 입찰 내역 리스트 조회 쿼리
   * @param bidListRequest 조회에 필요한 필드를 담은 dto 입니다.
   * @return
   */
  @Override
  public Slice<BidWithProductResponse> findBidWithProductResponseList(
      BidListByMemberRequest bidListRequest) {
    QProductImage subProductImage = new QProductImage("subProductImage");
    List<BidWithProductResponse> list = queryFactory
        .select(new QBidWithProductResponse(
            bid.id,
            bid.price,
            bid.usePoint,
            bid.member.memberId,
            new QProductSummaryDto(
                product.id,
                product.name,
                product.price,
                product.startDateTime,
                product.dynamicEndDateTime,
                product.maxEndDateTime,
                productImage.url
            )
        ))
        .from(bid)
        .innerJoin(bid.product, product)
        .leftJoin(productImage).on(productImage.id.eq(
            JPAExpressions.select(subProductImage.id.min())
                .from(subProductImage)
                .where(subProductImage.product.id.eq(product.id))
        ))
        .where(
            ltBidId(bidListRequest.cursorId()),
            equalsBidderId(bidListRequest.memberId())
        )
        .orderBy(bid.id.desc())
        .limit(bidListRequest.pageSize() + 1)
        .fetch();

    boolean hasNext = list.size() > bidListRequest.pageSize();
    if (hasNext) {
      list.remove(list.size() - 1);
    }

    return new SliceImpl<>(list, bidListRequest.getPageable(), hasNext);
  }

  private BooleanExpression ltBidId(Long bidId) {
    if (bidId == null) {
      return null;
    }

    return bid.id.lt(bidId);
  }

  private BooleanExpression equalsBidderId(Long memberId) {
    if (memberId == null) {
      return null;
    }

    return bid.member.memberId.eq(memberId);
  }
}
