package com.goodsending.product.repository;

import static com.goodsending.product.entity.QProduct.product;
import static com.goodsending.product.entity.QProductImage.productImage;

import com.goodsending.product.dto.response.MyProductSummaryDto;
import com.goodsending.product.dto.response.ProductSummaryDto;
import com.goodsending.product.dto.response.QMyProductSummaryDto;
import com.goodsending.product.dto.response.QProductSummaryDto;
import com.goodsending.product.entity.Product;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
  public Slice<ProductSummaryDto> findByFiltersAndSort(LocalDateTime now, String openProduct, String closedProduct,
      String keyword, LocalDateTime cursorStartDateTime, Long cursorId, Pageable pageable) {

    boolean open = false;
    if (openProduct != null && openProduct.equals("true")) {
      open = true;
    }

    boolean closed = false;
    if (closedProduct != null && closedProduct.equals("true")) {
      closed = true;
    }

    if (open == closed) {
      open = true;
      closed = true;
    }

    // 키워드 검색
    BooleanBuilder keywordBuilder = new BooleanBuilder();
    if (keyword != null && !keyword.isEmpty()) {
      keywordBuilder.and(product.name.containsIgnoreCase(keyword));
    }

    // 커서 기반 페이징
    BooleanBuilder cursorBuilder = new BooleanBuilder();
    if (cursorStartDateTime != null && cursorId != null) {
      cursorBuilder.and(
          product.startDateTime.gt(cursorStartDateTime)
          .or(product.startDateTime.eq(cursorStartDateTime).and(product.id.gt(cursorId))));
    }

    // 구매 가능한 상품 중 시작 시간이 가장 가까운 상품
    List<Product> firstFetch = jpaQueryFactory
        .selectFrom(product)
        .where(openBuilderExpression(now), keywordBuilder)
        .limit(1)
        .orderBy(product.startDateTime.asc())
        .fetch();
    Product firstOpenProduct = null;
    if (firstFetch.size() > 0) {
      firstOpenProduct = firstFetch.get(0);
    }

    // 구매 가능한 상품 중 시작 시간이 가장 먼 상품
    List<Product> lastFetch = jpaQueryFactory
        .selectFrom(product)
        .where(openBuilderExpression(now), keywordBuilder)
        .limit(1)
        .orderBy(
            new OrderSpecifier<>(Order.DESC, product.startDateTime),
            new OrderSpecifier<>(Order.DESC, product.id)
        )
        .fetch();
    Product lastOpenProduct = null;
    if (lastFetch.size() > 0) {
      lastOpenProduct = lastFetch.get(0);
    }

    // 구매 가능한 상품 목록
    List<ProductSummaryDto> openFetch = new ArrayList<>();
    if (open && openCase(firstOpenProduct, lastOpenProduct, cursorStartDateTime, cursorId)) {
      openFetch = jpaQueryFactory
          .select( new QProductSummaryDto(
              product.id,
              product.name,
              product.price,
              product.startDateTime,
              product.dynamicEndDateTime,
              product.maxEndDateTime,
              productImage.url))
          .from(product)
          .leftJoin(productImage).on(productImage.product.eq(product))
          .where(openBuilderExpression(now), cursorBuilder.and(keywordBuilder), productImage.id.eq(
              JPAExpressions.select(productImage.id.min()).from(productImage).where(productImage.product.eq(product))))
          .limit(pageable.getPageSize() + 1)
          .orderBy(product.startDateTime.asc())
          .fetch();
    }

    boolean hasNext = false;
    if (openFetch.size() > pageable.getPageSize()) {
      openFetch.remove(pageable.getPageSize());
      hasNext = true;
    }

    // cursorId가 null 이거나
    // 구매 가능 목록과 마감 목록을 함께 조회해 리스트에 담아야 할 경우
    // cursorBuilder 를 초기화하여 사용하지 않음
    if (cursorId != null && open && (
        cursorId == lastOpenProduct.getId() || (openFetch.size() < pageable.getPageSize() && openFetch.size() > 0))) {
      cursorBuilder = new BooleanBuilder();
    }

    // 마감된 상품 목록
    List<ProductSummaryDto> closedFetch = new ArrayList<>();
    if (closed && openFetch.size() < pageable.getPageSize()) {
      closedFetch = jpaQueryFactory
          .select( new QProductSummaryDto(
              product.id,
              product.name,
              product.price,
              product.startDateTime,
              product.dynamicEndDateTime,
              product.maxEndDateTime,
              productImage.url))
          .from(product)
          .leftJoin(productImage).on(productImage.product.eq(product))
          .where(closedBuilderExpression(now), cursorBuilder.and(keywordBuilder), productImage.id.eq(
              JPAExpressions.select(productImage.id.min()).from(productImage).where(productImage.product.eq(product))))
          .limit(closedProductPageSize(open, openFetch.size(), pageable.getPageSize()))
          .orderBy(product.startDateTime.asc())
          .fetch();
    }

    if (!hasNext && openFetch.size() + closedFetch.size() > pageable.getPageSize()) {
      closedFetch.remove(pageable.getPageSize() - openFetch.size());
      hasNext = true;
    }

    // 구매 가능 상품 목록과 마감된 상품 목록을 합치기
    List<ProductSummaryDto> combinedProducts = new ArrayList<>();
    combinedProducts.addAll(openFetch);
    combinedProducts.addAll(closedFetch);

    return new SliceImpl<>(combinedProducts, pageable, hasNext);
  }

  @Override
  public Slice<MyProductSummaryDto> findProductByMember(Long memberId, Pageable pageable, Long cursorId) {

    BooleanBuilder cursorBuilder = new BooleanBuilder();
    if (cursorId != null) {
      cursorBuilder.and(product.id.lt(cursorId));
    }

    List<MyProductSummaryDto> myFetch = jpaQueryFactory
        .select(new QMyProductSummaryDto(
            product.id,
            product.name,
            product.price,
            product.startDateTime,
            product.dynamicEndDateTime,
            product.maxEndDateTime,
            productImage.url))
        .from(product)
        .leftJoin(productImage).on(productImage.product.eq(product))
        .where(product.member.memberId.eq(memberId).and(cursorBuilder)
            .and(productImage.id.eq(JPAExpressions
                                    .select(productImage.id.min())
                                    .from(productImage)
                                    .where(productImage.product.eq(product))
                                    )
            )
        )
        .limit(pageable.getPageSize() + 1)
        .orderBy(product.id.desc())
        .fetch();

    boolean hasNext = false;
    if (myFetch.size() > pageable.getPageSize()) {
      hasNext = true;
      myFetch.remove(pageable.getPageSize());
    }

    return new SliceImpl<>(myFetch, pageable, hasNext);
  }

  private boolean openCase(Product firstOpenProduct, Product lastOpenProduct,
      LocalDateTime cursorStartDateTime, Long cursorId) {
    if (cursorId == null && cursorStartDateTime == null) {
      return true;
    } else if (firstOpenProduct == null) {
      return  false;
    } else if (cursorStartDateTime.isAfter(firstOpenProduct.getStartDateTime()) || cursorStartDateTime.isEqual(firstOpenProduct.getStartDateTime())) {
      // 판매 마감 상품 cursor 사용 시 조회되지 않도록
      // 구매 가능한 첫 번째 상품보다 늦거나 같아야 한다
      if (cursorId == lastOpenProduct.getId()) { // 구매 가능한 상품 중 가장 마지막 상품이 커서라면 조회하지 않는다.
        return false;
      }
      return true;
    }
    return false;
  }

  private long closedProductPageSize(boolean open, int openSize, int pageSize) {
    // 마감 된 상품의 조회 개수 설정
    if (open && openSize < pageSize) { // 구매 가능 상품과 함께 조회하여 반환해야 할 경우
      return pageSize - openSize +1;
    } else {
      return pageSize + 1;
    }
  }

  private BooleanExpression openBuilderExpression(LocalDateTime now) {
    // 압찰을 진행하지 않았거나, 입찰 마감 시간이 남은 상품
    return product.maxEndDateTime.after(now)
            .and(product.dynamicEndDateTime.isNull()
                .or(product.dynamicEndDateTime.after(now)
                )
            );
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

  private BooleanExpression closedBuilderExpression(LocalDateTime now) {
    // 입찰 마감 시간이 지난 상품
    return product.maxEndDateTime.before(now)
        .or(product.dynamicEndDateTime.isNotNull().and(product.dynamicEndDateTime.before(now)));
  }
}
