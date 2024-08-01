package com.goodsending.product.repository;

import com.goodsending.product.dto.response.ProductSummaryDto;
import com.goodsending.product.entity.Product;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ProductCustomRepository {
  Slice<ProductSummaryDto> findByKeywordOrAllOrderByIdDescSlice(String keyword, Long cursorId, Pageable pageable);

  List<Product> findTop5ByStartDateTimeAfterOrderByLikeCountDesc(LocalDateTime currentDateTime);
}
