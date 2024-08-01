package com.goodsending.product.dto.response;

import com.goodsending.product.entity.Product;
import com.goodsending.product.entity.ProductImage;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

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

  @Builder
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

  public static ProductSummaryDto from(Product product) {
    ProductImage thumbnailProductImage = product.getProductImages().get(0);
    String thumbnailUrl = thumbnailProductImage.getUrl();

    return ProductSummaryDto.builder()
        .productId(product.getId())
        .name(product.getName())
        .price(product.getPrice())
        .startDateTime(product.getStartDateTime())
        .dynamicEndDateTime(product.getDynamicEndDateTime())
        .maxEndDateTime(product.getMaxEndDateTime())
        .thumbnailUrl(thumbnailUrl)
        .build();
  }

  public static Page<ProductSummaryDto> from(Page<Product> productPage) {
    Page<ProductSummaryDto> productSummaryDtoPage = productPage.map(product -> ProductSummaryDto.from(product));
    return productSummaryDtoPage;
  }
}
