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
  private final Long likeCount;
  private final LocalDateTime startDateTime;
  private final LocalDateTime maxEndDate;
  private final List<ProductImageCreateResponseDto> productImages;

  @Builder
  public ProductCreateResponseDto(Long productId, Long memberId, String name, int price,
      String introduction, LocalDateTime startDateTime, LocalDateTime maxEndDate,Long likeCount,
      List<ProductImageCreateResponseDto> productImages) {
    this.productId = productId;
    this.memberId = memberId;
    this.name = name;
    this.price = price;
    this.introduction = introduction;
    this.startDateTime = startDateTime;
    this.maxEndDate = maxEndDate;
    this.likeCount = likeCount;
    this.productImages = productImages;
  }

  public static ProductCreateResponseDto of(Product product,
      List<ProductImageCreateResponseDto> savedProductImages) {
    return ProductCreateResponseDto.builder()
        .productId(product.getId())
        .memberId(product.getMember().getMemberId())
        .name(product.getName())
        .price(product.getPrice())
        .introduction(product.getIntroduction())
        .productImages(savedProductImages)
        .likeCount(product.getLikeCount())
        .startDateTime(product.getStartDateTime())
        .maxEndDate(product.getMaxEndDateTime())
        .build();
  }
  public static ProductCreateResponseDto from(Product product) {
    return ProductCreateResponseDto.builder()
        .productId(product.getId())
        .memberId(product.getMember().getMemberId())
        .name(product.getName())
        .price(product.getPrice())
        .introduction(product.getIntroduction())
        .likeCount(product.getLikeCount())
        .startDateTime(product.getStartDateTime())
        .maxEndDate(product.getMaxEndDateTime())
        .build();
  }

}
