package com.goodsending.productlike.entity;

public class ProductLikeWithScore {
  private ProductLikeDto productLikeDto;
  private Double score;

  public ProductLikeWithScore(ProductLikeDto productLikeDto, Double score) {
    this.productLikeDto = productLikeDto;
    this.score = score;
  }

  public ProductLikeDto getProductLikeDto() {
    return productLikeDto;
  }

  public void setProductLikeDto(ProductLikeDto productLikeDto) {
    this.productLikeDto = productLikeDto;
  }

  public Double getScore() {
    return score;
  }

  public void setScore(Double score) {
    this.score = score;
  }
}
