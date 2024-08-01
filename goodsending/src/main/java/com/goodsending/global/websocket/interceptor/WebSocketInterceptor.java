package com.goodsending.global.websocket.interceptor;

import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.global.websocket.factory.StompCommandHandlerFactory;
import com.goodsending.global.websocket.handler.StompCommandHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

/**
 * @Date : 2024. 07. 27.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketInterceptor implements ChannelInterceptor {

  private final StompCommandHandlerFactory handlerFactory;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor
        .getAccessor(message, StompHeaderAccessor.class);

    if (accessor == null) {
      log.info("[ConnectCommandHandler][preSend] accessor is null");
      throw CustomException.from(ExceptionCode.STOMP_HEADER_ACCESSOR_NOT_FOUND_EXCEPTION);
    }

    StompCommandHandler handler = handlerFactory.getHandler(accessor.getCommand());

    if (handler != null) {
      handler.handle(accessor);
    }

    return message;
  }
}
