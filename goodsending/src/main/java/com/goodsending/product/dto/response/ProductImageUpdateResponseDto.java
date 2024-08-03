package com.goodsending.product.dto.response;

import com.goodsending.product.entity.ProductImage;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductImageUpdateResponseDto {

  private final Long productImageId;
  private final String url;
  private final Long productId;

  @Builder
  public ProductImageUpdateResponseDto(Long productImageId, String url, Long productId) {
    this.productImageId = productImageId;
    this.url = url;
    this.productId = productId;
  }

  public static ProductImageUpdateResponseDto from(ProductImage productImage) {
    return ProductImageUpdateResponseDto.builder()
        .productImageId(productImage.getId())
        .url(productImage.getUrl())
        .productId(productImage.getProduct().getId())
        .build();
  }
}
