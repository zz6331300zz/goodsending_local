package com.goodsending.global.redis.handler;

/**
 * @Date : 2024. 08. 01.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
public interface RedisMessageHandler {
  void handle(String message);
}
