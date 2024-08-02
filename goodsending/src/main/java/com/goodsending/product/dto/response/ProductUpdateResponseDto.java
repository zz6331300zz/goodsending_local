package com.goodsending.product.dto.response;

import com.goodsending.product.entity.Product;
import com.goodsending.product.entity.ProductImage;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductUpdateResponseDto {

  private final Long productId;
  private final Long memberId;
  private final String name;
  private final int price;
  private final String introduction;
  private final Long likeCount;
  private final LocalDateTime startDateTime;
  private final LocalDateTime maxEndDateTime;
  private final List<ProductImageUpdateResponseDto> productImages;

  @Builder
  public ProductUpdateResponseDto(Long productId, Long memberId, String name, int price,
      String introduction, Long likeCount, LocalDateTime startDateTime, LocalDateTime maxEndDateTime,
      List<ProductImageUpdateResponseDto> productImages) {
    this.productId = productId;
    this.memberId = memberId;
    this.name = name;
    this.price = price;
    this.introduction = introduction;
    this.likeCount = likeCount;
    this.startDateTime = startDateTime;
    this.maxEndDateTime = maxEndDateTime;
    this.productImages = productImages;
  }

  public static ProductUpdateResponseDto from(Product product, List<ProductImage> productImageList) {
    List<ProductImageUpdateResponseDto> productImageUpdateResponseDtoList = new ArrayList<>();
    for (ProductImage productImage : productImageList) {
      ProductImageUpdateResponseDto productImageUpdateResponseDto = ProductImageUpdateResponseDto.from(productImage);
      productImageUpdateResponseDtoList.add(productImageUpdateResponseDto);
    }

    return ProductUpdateResponseDto.builder()
        .productId(product.getId())
        .memberId(product.getMember().getMemberId())
        .name(product.getName())
        .price(product.getPrice())
        .introduction(product.getIntroduction())
        .likeCount(product.getLikeCount())
        .startDateTime(product.getStartDateTime())
        .maxEndDateTime(product.getMaxEndDateTime())
        .productImages(productImageUpdateResponseDtoList)
        .build();
  }
}
