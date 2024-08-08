package com.goodsending.productlike.entity;

import com.goodsending.product.type.ProductStatus;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ProductLikeDto {

  private String productName;
  private LocalDateTime startDateTime;
  private LocalDateTime maxEndDateTime;
  private int price;
  private String url;
  private ProductStatus status;

  public ProductLikeDto(String productName, LocalDateTime startDateTime,
      LocalDateTime maxEndDateTime, int price, String url, ProductStatus status) {
    this.productName = productName;
    this.startDateTime = startDateTime;
    this.maxEndDateTime = maxEndDateTime;
    this.price = price;
    this.url = url;
    this.status = status;
  }
}