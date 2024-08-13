package com.goodsending.productlike.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LikeStatus {
  ALREADY_LIKE(1),
  DELETED_LIKE(2),
  CREATE_SUCCESS(3),
  REMOVE_SUCCESS(4);

  private final int rank;

}
