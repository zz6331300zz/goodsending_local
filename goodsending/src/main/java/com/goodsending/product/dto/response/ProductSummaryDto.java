package com.goodsending.product.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ProductSummaryDto {

  private Long productId;
  private String name;
  private int price;
  private LocalDateTime startDateTime;
  private LocalDateTime dynamicEndDateTime;
  private LocalDateTime maxEndDateTime;
  private String thumbnailUrl;
  // TODO : 입찰 여부 필드

  @QueryProjection
  public ProductSummaryDto(Long productId, String name, int price, LocalDateTime startDateTime, LocalDateTime dynamicEndDateTime,
      LocalDateTime maxEndDateTime, String thumbnailUrl) {
    this.productId = productId;
    this.name = name;
    this.price = price;
    this.startDateTime = startDateTime;
    this.dynamicEndDateTime = dynamicEndDateTime;
    this.maxEndDateTime = maxEndDateTime;
    this.thumbnailUrl = thumbnailUrl;
  }

}
