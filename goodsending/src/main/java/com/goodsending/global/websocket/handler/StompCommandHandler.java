package com.goodsending.global.websocket.handler;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

/**
 * @Date : 2024. 07. 27.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
public interface StompCommandHandler {
  void handle(StompHeaderAccessor accessor);
}
