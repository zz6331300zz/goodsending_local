package com.goodsending.productlike.entity;

import com.goodsending.productlike.dto.ProductRankingDto;

public class ProductLikeWithScore {
  private ProductRankingDto productRankingDto;
  private Double score;

  public ProductLikeWithScore(ProductRankingDto productRankingDto, Double score) {
    this.productRankingDto = productRankingDto;
    this.score = score;
  }

  public ProductRankingDto getProductLikeDto() {
    return productRankingDto;
  }

  public void setProductLikeDto(ProductRankingDto productRankingDto) {
    this.productRankingDto = productRankingDto;
  }

  public Double getScore() {
    return score;
  }

  public void setScore(Double score) {
    this.score = score;
  }
}
