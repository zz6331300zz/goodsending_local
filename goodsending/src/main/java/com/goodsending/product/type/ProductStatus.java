package com.goodsending.product.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProductStatus {
  ONGOING(1),
  UPCOMING(2),
  ENDED(3);

  private final int rank;

}
