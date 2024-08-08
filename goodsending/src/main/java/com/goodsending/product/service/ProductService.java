package com.goodsending.product.service;

import com.goodsending.product.dto.request.ProductCreateRequestDto;
import com.goodsending.product.dto.request.ProductSearchCondition;
import com.goodsending.product.dto.request.ProductUpdateRequestDto;
import com.goodsending.product.dto.response.ProductCreateResponseDto;
import com.goodsending.product.dto.response.ProductInfoDto;
import com.goodsending.product.dto.response.ProductSummaryDto;
import com.goodsending.product.dto.response.ProductUpdateResponseDto;
import com.goodsending.product.type.ProductStatus;
import java.time.LocalDateTime;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

  ProductCreateResponseDto createProduct(ProductCreateRequestDto requestDto,
      List<MultipartFile> productImages, Long memberId);

  ProductInfoDto getProduct(Long productId);

  Slice<ProductSummaryDto> getProductSlice(ProductSearchCondition productSearchCondition);

  ProductUpdateResponseDto updateProduct(Long productId, ProductUpdateRequestDto requestDto, List<MultipartFile> productImages, Long memberId,
      LocalDateTime now);

  void deleteProduct(Long productId, Long memberId, LocalDateTime now);

  void updateProductStatus(ProductStatus status, LocalDateTime startDateTime);

}
