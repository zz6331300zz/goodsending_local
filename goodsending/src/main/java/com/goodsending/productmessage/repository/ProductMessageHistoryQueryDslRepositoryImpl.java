package com.goodsending.productmessage.repository;

import static com.goodsending.member.entity.QMember.member;
import static com.goodsending.product.entity.QProduct.product;
import static com.goodsending.productmessage.entity.QProductMessageHistory.productMessageHistory;

import com.goodsending.productmessage.dto.request.ProductMessageListRequest;
import com.goodsending.productmessage.dto.response.ProductMessageResponse;
import com.goodsending.productmessage.dto.response.QProductMessageResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

/**
 * @Date : 2024. 08. 08.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
@Repository
@RequiredArgsConstructor
public class ProductMessageHistoryQueryDslRepositoryImpl implements ProductMessageHistoryQueryDslRepository{
  private final JPAQueryFactory queryFactory;

  @Override
  public Slice<ProductMessageResponse> findByProductId(ProductMessageListRequest request) {
    List<ProductMessageResponse> list = queryFactory
        .select(new QProductMessageResponse(
            productMessageHistory.id,
            productMessageHistory.member.memberId,
            productMessageHistory.product.id,
            productMessageHistory.message,
            productMessageHistory.type
        ))
        .from(productMessageHistory)
        .innerJoin(productMessageHistory.product, product)
        .innerJoin(productMessageHistory.member, member)
        .where(
            ltProductMessageHistoryId(request.cursorId()),
            equalsProductId(request.productId())
        )
        .orderBy(productMessageHistory.id.desc())
        .limit(request.size() + 1)
        .fetch();
    boolean hasNext = list.size() > request.size();
    if(hasNext){
      list.remove(list.size() - 1);
    }

    return new SliceImpl<>(list, PageRequest.of(0, request.size()), hasNext);
  }

  private BooleanExpression ltProductMessageHistoryId(Long historyId) {
    if(historyId == null){
      return null;
    }
    return productMessageHistory.id.lt(historyId);
  }

  private BooleanExpression equalsProductId(Long productId) {
    if (productId == null) {
      return null;
    }

    return productMessageHistory.product.id.eq(productId);
  }
}
