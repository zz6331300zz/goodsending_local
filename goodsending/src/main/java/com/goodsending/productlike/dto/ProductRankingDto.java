package com.goodsending.productlike.dto;

import com.goodsending.product.entity.Product;
import com.goodsending.product.entity.ProductImage;
import com.goodsending.product.type.ProductStatus;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductRankingDto {

  private Long productId;
  private String productName;
  private int price;
  private LocalDateTime startDateTime;
  private LocalDateTime maxEndDateTime;
  private ProductStatus status;
  private String url;

  @Builder
  @QueryProjection
  public ProductRankingDto(Long productId, String productName, int price,
      LocalDateTime startDateTime,
      LocalDateTime maxEndDateTime, ProductStatus status, String url) {
    this.productId = productId;
    this.productName = productName;
    this.price = price;
    this.startDateTime = startDateTime;
    this.maxEndDateTime = maxEndDateTime;
    this.status = status;
    this.url = url;
  }

  public static ProductRankingDto of(Product product, ProductImage productImage) {
    return ProductRankingDto.builder()
        .productId(product.getId())
        .productName(product.getName())
        .price(product.getPrice())
        .startDateTime(product.getStartDateTime())
        .maxEndDateTime(product.getMaxEndDateTime())
        .status(product.getStatus())
        .url(productImage.getUrl())
        .build();
  }
}