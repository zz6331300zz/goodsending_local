package com.goodsending.product.dto.response;

import com.goodsending.product.entity.Product;
import com.goodsending.product.entity.ProductImage;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductInfoDto {

  private Long productId;
  private Long memberId;
  private String name;
  private int price;
  private String introduction;
  private LocalDateTime auctionEndDate;
  private int biddingCount;
  private List<ProductImageInfoDto> productImages;
  // TODO : 입찰 여부 필드

  @Builder
  public ProductInfoDto(Long productId, Long memberId, String name, int price, String introduction,
      LocalDateTime auctionEndDate, int biddingCount, List<ProductImageInfoDto> productImages) {
    this.productId = productId;
    this.memberId = memberId;
    this.name = name;
    this.price = price;
    this.introduction = introduction;
    this.auctionEndDate = auctionEndDate;
    this.biddingCount = biddingCount;
    this.productImages = productImages;
  }


  public static ProductInfoDto of(Product product, List<ProductImage> productImageList) {

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
        .auctionEndDate(product.getAuctionEndDate())
        .biddingCount(product.getBiddingCount())
        .productImages(productImages)
        .build();
  }
}
