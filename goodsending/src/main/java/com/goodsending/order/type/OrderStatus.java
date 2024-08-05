package com.goodsending.order.type;

import lombok.RequiredArgsConstructor;

/**
 * @author : jieun(je-pa)
 * @Date : 2024. 08. 02.
 * @Team : GoodsEnding
 * @Project : goodsending-be :: goodsending
 */
@RequiredArgsConstructor
public enum OrderStatus {
  CANCELLED("취소된"),
  COMPLETED("완료된"),
  SHIPPING("배송중"),
  PENDING("배송전");

  private final String name;

  public String getName() {
    return name;
  }
}
