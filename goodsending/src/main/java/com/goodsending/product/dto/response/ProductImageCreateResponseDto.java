package com.goodsending.product.dto.response;

import com.goodsending.product.entity.ProductImage;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductImageCreateResponseDto {

  private final Long productImageId;
  private final String url;
  private final Long productId;

  @Builder
  public ProductImageCreateResponseDto(Long productImageId, String url, Long productId) {
    this.productImageId = productImageId;
    this.url = url;
    this.productId = productId;
  }


  public static ProductImageCreateResponseDto from(ProductImage productImage) {
    return ProductImageCreateResponseDto.builder()
        .productImageId(productImage.getId())
        .url(productImage.getUrl())
        .productId(productImage.getProduct().getId())
        .build();
  }
}
