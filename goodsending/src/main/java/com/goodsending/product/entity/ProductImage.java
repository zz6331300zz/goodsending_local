package com.goodsending.product.entity;

import com.goodsending.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_imgs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "img_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  Product product;

  @Column(name = "url", nullable = false, length = 255)
  private String url;

  @Builder
  public ProductImage(Product product, String url) {
    this.product = product;
    this.url = url;
  }

  public static ProductImage of(Product product, String storedFileName) {
    return ProductImage.builder()
        .product(product)
        .url(storedFileName)
        .build();
  }
}
