package com.goodsending.productlike.entity;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ProductLikeDto {

  private String productName;
  private LocalDateTime startDateTime;
  private LocalDateTime maxEndDateTime;
  private int price;
  private String url;

  public ProductLikeDto(String productName, LocalDateTime startDateTime,
      LocalDateTime maxEndDateTime, int price, String url) {
    this.productName = productName;
    this.startDateTime = startDateTime;
    this.maxEndDateTime = maxEndDateTime;
    this.price = price;
    this.url = url;
  }
}