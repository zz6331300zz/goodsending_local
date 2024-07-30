package com.goodsending.product.repository;

import static com.goodsending.product.entity.QProduct.product;
import static com.goodsending.product.entity.QProductImage.productImage;

import com.goodsending.product.dto.response.ProductSummaryDto;
import com.goodsending.product.entity.Product;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
  public Slice<ProductSummaryDto> findByKeywordOrAllOrderByIdDescSlice(String keyword, Long cursorId,
      Pageable pageable) {
    List<Product> fetch = jpaQueryFactory
        .selectFrom(product)
        .leftJoin(product.productImages, productImage).fetchJoin()
        .where(searchConditionEquals(keyword, cursorId))
        .limit(pageable.getPageSize()+1)
        .orderBy(product.id.desc())
        .fetch();

    List<ProductSummaryDto> productSummaryDtoList = new ArrayList<>();
    for (Product product : fetch) {
      ProductSummaryDto summaryDto = ProductSummaryDto.from(product);
      productSummaryDtoList.add(summaryDto);
    }

    boolean hasNext = false;
    if (productSummaryDtoList.size() > pageable.getPageSize()) {
      productSummaryDtoList.remove(pageable.getPageSize());
      hasNext = true;
    }

    return new SliceImpl<>(productSummaryDtoList, pageable, hasNext);
  }

  private BooleanBuilder searchConditionEquals(String keyword, Long cursorId) {
    return nameContainsKeyword(keyword).and(cursorIdEquals(cursorId));
  }

  private BooleanBuilder cursorIdEquals(Long cursorId) {
    if (cursorId == null) {
      return new BooleanBuilder();
    }
    return new BooleanBuilder(product.id.lt(cursorId));
  }

  private BooleanBuilder nameContainsKeyword(String keyword) {
    if (keyword == null || keyword.equals("")) {
      return new BooleanBuilder();
    }
    return new BooleanBuilder(product.name.containsIgnoreCase(keyword));
  }

}
