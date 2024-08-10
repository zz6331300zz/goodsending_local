package com.goodsending.productlike.dto;

import com.goodsending.product.type.ProductStatus;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ProductRankingDto {

  private Long productId;
  private String productName;
  private LocalDateTime startDateTime;
  private LocalDateTime maxEndDateTime;
  private int price;
  private String url;
  private ProductStatus status;

  @QueryProjection
  public ProductRankingDto(Long productId, String productName, LocalDateTime startDateTime,
      LocalDateTime maxEndDateTime, int price, String url, ProductStatus status) {
    this.productId = productId;
    this.productName = productName;
    this.startDateTime = startDateTime;
    this.maxEndDateTime = maxEndDateTime;
    this.price = price;
    this.url = url;
    this.status = status;
  }
}