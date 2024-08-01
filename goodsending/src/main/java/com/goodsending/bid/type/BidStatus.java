package com.goodsending.bid.type;

import lombok.RequiredArgsConstructor;

/**
 * @Date : 2024. 07. 25.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
@RequiredArgsConstructor
public enum BidStatus {
  SUCCESSFUL("낙찰 됨"),
  FAILED("낙찰 안됨");

  private final String name;
}
