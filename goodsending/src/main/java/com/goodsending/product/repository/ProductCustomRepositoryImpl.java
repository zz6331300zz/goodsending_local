package com.goodsending.product.repository;

import static com.goodsending.product.entity.QProduct.product;
import static com.goodsending.product.entity.QProductImage.productImage;

import com.goodsending.product.dto.request.ProductSearchCondition;
import com.goodsending.product.dto.response.ProductSummaryDto;
import com.goodsending.product.dto.response.QProductSummaryDto;
import com.goodsending.product.entity.Product;
import com.goodsending.product.type.ProductStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductCustomRepositoryImpl implements ProductCustomRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Slice<ProductSummaryDto> findByFiltersAndSort(ProductSearchCondition productSearchCondition, Pageable pageable) {

    NumberExpression<Integer> statusRank = statusRankCaseBuilder();

    JPAQuery<ProductSummaryDto> query = jpaQueryFactory
        .select(new QProductSummaryDto(
            product.id,
            product.name,
            product.price,
            product.startDateTime,
            product.dynamicEndDateTime,
            product.maxEndDateTime,
            product.status,
            productImage.url
        ))
        .from(product)
        .leftJoin(productImage).on(productImage.product.eq(product))
        .where(productImageEq(), searchConditionEq(productSearchCondition, statusRank))
        .orderBy(statusRank.asc(), product.startDateTime.asc(), product.id.asc())
        .limit(pageable.getPageSize()+1);

    List<ProductSummaryDto> productSummaryDtoList = query.fetch();

    boolean hasNext = false;
    if (productSummaryDtoList.size() > pageable.getPageSize()) {
      hasNext = true;
      productSummaryDtoList.remove(pageable.getPageSize());
    }

    return new SliceImpl<>(productSummaryDtoList, pageable, hasNext);
  }

  @Override
  public List<Product> findAllByStatusAndStartDateTime(ProductStatus status, LocalDateTime startDateTime) {
    return jpaQueryFactory
        .selectFrom(product)
        .where(product.status.eq(status).and(startDateTimeEq(startDateTime)))
        .fetch();
  }

  @Override
  public List<Product> findTop5ByStartDateTimeAfterOrderByLikeCountDesc(
      LocalDateTime currentDateTime) {
    return jpaQueryFactory.selectFrom(product)
        .where(product.startDateTime.gt(currentDateTime))
        .orderBy(product.likeCount.desc())
        .limit(5)
        .fetch();
  }

  private BooleanBuilder searchConditionEq(ProductSearchCondition condition,
      NumberExpression<Integer> statusRank) {

    BooleanBuilder cursorBuilder = cursorBuilder(condition.getCursorStatus(),
        condition.getCursorStartDateTime(),
        condition.getCursorId(), statusRank);

    BooleanBuilder searchConditionBuilder = cursorBuilder
                                            .and(keywordBuilder(condition.getKeyword()))
                                            .and(memberBuilder(condition.getMemberId()));

    boolean open = condition.isOpenProduct();
    boolean closed = condition.isClosedProduct();
    if (open != closed) {
      searchConditionBuilder.and(openBuilder(open)).and(closedBuilder(closed));
    }

    return searchConditionBuilder;
  }

  private BooleanExpression productImageEq(){
    return productImage.id.eq(
        JPAExpressions
            .select(productImage.id.min())
            .from(productImage)
            .where(productImage.product.eq(product)));
  }

  private BooleanExpression startDateTimeEq(LocalDateTime startDateTime) {
    return startDateTime != null ? product.startDateTime.eq(startDateTime) : null;
  }

  private NumberExpression<Integer> statusRankCaseBuilder() {
    NumberExpression<Integer> statusRank = new CaseBuilder()
        .when(product.status.eq(ProductStatus.ONGOING)).then(ProductStatus.ONGOING.getRank())
        .when(product.status.eq(ProductStatus.UPCOMING)).then(ProductStatus.UPCOMING.getRank())
        .when(product.status.eq(ProductStatus.ENDED)).then(ProductStatus.ENDED.getRank())
        .otherwise(4);
    return statusRank;
  }

  private BooleanBuilder memberBuilder(Long memberId) {
    BooleanBuilder memberBuilder = new BooleanBuilder();
    if (memberId != null) {
      memberBuilder.and(product.member.memberId.eq(memberId));
    }
    return memberBuilder;
  }

  private BooleanBuilder keywordBuilder(String keyword) {
    // 키워드 검색
    BooleanBuilder keywordBuilder = new BooleanBuilder();
    if (keyword != null && !keyword.isEmpty()) {
      keywordBuilder.and(product.name.containsIgnoreCase(keyword));
    }
    return keywordBuilder;
  }

  private BooleanBuilder cursorBuilder(ProductStatus cursorStatus, LocalDateTime cursorStartDateTime, Long cursorId, NumberExpression<Integer> statusRank) {
    BooleanBuilder cursorBuilder = new BooleanBuilder();
    if (cursorStatus != null && cursorStartDateTime != null && cursorId != null) {
      cursorBuilder.and(
          statusRank.gt(cursorStatus.getRank())
              .or(statusRank.eq(cursorStatus.getRank()).and(product.startDateTime.gt(cursorStartDateTime)))
              .or(statusRank.eq(cursorStatus.getRank()).and(product.startDateTime.eq(cursorStartDateTime)).and(product.id.gt(cursorId)))
      );
    }
    return cursorBuilder;
  }

  private BooleanBuilder openBuilder(boolean open) {
    BooleanBuilder openBuilder = new BooleanBuilder();
    if (open) {
      openBuilder.and(product.status.eq(ProductStatus.UPCOMING)
          .or(product.status.eq(ProductStatus.ONGOING)));
    }
    return openBuilder;
  }

  private BooleanBuilder closedBuilder(boolean closed) {
    BooleanBuilder closedBuilder = new BooleanBuilder();
    if (closed) {
      closedBuilder.and(product.status.eq(ProductStatus.ENDED));
    }
    return closedBuilder;
  }
}
