package com.goodsending.product.repository;

import com.goodsending.product.dto.request.ProductSearchCondition;
import com.goodsending.product.dto.response.ProductSummaryDto;
import com.goodsending.product.entity.Product;
import com.goodsending.product.type.ProductStatus;
import com.goodsending.productlike.dto.ProductRankingDto;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCustomRepository {

  List<Product> findTop5ByStartDateTimeAfterOrderByLikeCountDesc(LocalDateTime currentDateTime);

  Slice<ProductSummaryDto> findByFiltersAndSort(ProductSearchCondition productSearchCondition, Pageable pageable);

  List<Product> findAllByStatusAndStartDateTime(ProductStatus status, LocalDateTime startDateTime);

  ProductRankingDto findRankingDtoById(Long productId);
}
