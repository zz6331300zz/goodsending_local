package com.goodsending.product.repository;

import com.goodsending.product.dto.response.ProductSummaryDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ProductCustomRepository {
  Slice<ProductSummaryDto> findByKeywordOrAllOrderByIdDescSlice(String keyword, Long cursorId, Pageable pageable);
}
