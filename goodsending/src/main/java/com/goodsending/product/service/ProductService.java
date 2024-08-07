package com.goodsending.product.service;

import com.goodsending.product.dto.request.ProductCreateRequestDto;
import com.goodsending.product.dto.request.ProductUpdateRequestDto;
import com.goodsending.product.dto.response.MyProductSummaryDto;
import com.goodsending.product.dto.response.ProductCreateResponseDto;
import com.goodsending.product.dto.response.ProductInfoDto;
import com.goodsending.product.dto.response.ProductSummaryDto;
import com.goodsending.product.dto.response.ProductUpdateResponseDto;
import java.time.LocalDateTime;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

  ProductCreateResponseDto createProduct(ProductCreateRequestDto requestDto,
      List<MultipartFile> productImages, Long memberId);

  ProductInfoDto getProduct(Long productId);

  Slice<ProductSummaryDto> getProductSlice(LocalDateTime now, String openProduct, String closedProduct, String keyword,
      LocalDateTime cursorStartDateTime, Long cursorId, int size);

  ProductUpdateResponseDto updateProduct(Long productId, ProductUpdateRequestDto requestDto, List<MultipartFile> productImages, Long memberId,
      LocalDateTime now);

  void deleteProduct(Long productId, Long memberId, LocalDateTime now);

  Slice<MyProductSummaryDto> getMyProductSlice(Long memberId, int size, Long cursorId);
}
