package com.goodsending.product.dto.response;

import com.goodsending.product.entity.Product;
import com.goodsending.product.entity.ProductImage;
import com.goodsending.product.type.ProductStatus;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProductInfoDto {

  private Long productId;
  private Long memberId;
  private String name;
  private int price;
  private String introduction;
  private LocalDateTime startDateTime;
  private LocalDateTime maxEndDateTime;
  private LocalDateTime dynamicEndDateTime;
  private Duration remainingExpiration;
  private int biddingCount;
  private int bidderCount;
  private Long likeCount;
  private ProductStatus status;
  private List<ProductImageInfoDto> productImages;

  public static ProductInfoDto of(Product product, List<ProductImage> productImageList,
      Duration remainingExpiration) {

    List<ProductImageInfoDto> productImages = new ArrayList<>();
    for (ProductImage productImage : productImageList) {
      ProductImageInfoDto productImageInfoDto = ProductImageInfoDto.from(productImage);
      productImages.add(productImageInfoDto);
    }

    return ProductInfoDto.builder()
        .productId(product.getId())
        .memberId(product.getMember().getMemberId())
        .name(product.getName())
        .price(product.getPrice())
        .introduction(product.getIntroduction())
        .startDateTime(product.getStartDateTime())
        .maxEndDateTime(product.getMaxEndDateTime())
        .dynamicEndDateTime(product.getDynamicEndDateTime())
        .remainingExpiration(remainingExpiration)
        .biddingCount(product.getBiddingCount())
        .bidderCount(product.getBidderCount())
        .likeCount(product.getLikeCount())
        .status(product.getStatus())
        .productImages(productImages)
        .build();
  }
}
