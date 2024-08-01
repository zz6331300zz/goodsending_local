package com.goodsending.product.repository;

import com.goodsending.product.dto.response.ProductSummaryDto;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ProductCustomRepository {
  Slice<ProductSummaryDto> findByFiltersAndSort(LocalDateTime now, String openProduct, String closedProduct, String keyword,
      LocalDateTime cursorStartDateTime, Long cursorId, Pageable pageable);
}
