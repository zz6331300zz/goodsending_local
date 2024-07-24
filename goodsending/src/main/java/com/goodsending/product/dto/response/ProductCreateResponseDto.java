package com.goodsending.product.dto.response;

import com.goodsending.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ProductCreateResponseDto {

  private final Long productId;
  private final Long memberId;
  private final String name;
  private final int price;
  private final String introduction;
  private final LocalDateTime auctionEndDate;
  private final List<ProductImageInfoDto> productImages;

  @Builder
  public ProductCreateResponseDto(Long productId, Long memberId, String name, int price, String introduction,
      LocalDateTime auctionEndDate, List<ProductImageInfoDto> productImages) {
    this.productId = productId;
    this.memberId = memberId;
    this.name = name;
    this.price = price;
    this.introduction = introduction;
    this.auctionEndDate = auctionEndDate;
    this.productImages = productImages;
  }

  public static ProductCreateResponseDto of(Product product,
      List<ProductImageInfoDto> savedProductImages) {
    return ProductCreateResponseDto.builder()
        .productId(product.getId())
        .memberId(product.getMember().getMemberId())
        .name(product.getName())
        .price(product.getPrice())
        .introduction(product.getIntroduction())
        .auctionEndDate(product.getAuctionEndDate())
        .productImages(savedProductImages)
        .build();
  }
}
