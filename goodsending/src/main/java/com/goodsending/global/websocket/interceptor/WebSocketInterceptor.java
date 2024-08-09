package com.goodsending.global.websocket.interceptor;

import static org.springframework.messaging.simp.stomp.StompCommand.SEND;

import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.global.websocket.handler.StompCommandHandler;
import jakarta.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

/**
 * @author : jieun(je-pa)
 * @Date : 2024. 07. 27.
 * @Team : GoodsEnding
 * @Project : goodsending-be :: goodsending
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketInterceptor implements ChannelInterceptor {

  private static final Map<StompCommand, String> handlers = new EnumMap<>(StompCommand.class);
  private final ApplicationContext applicationContext;

  /**
   * command 가 무엇으로 들어오는지에 따라 다른 구현체 bean을 전달해주기 위한 작업입니다.
   * @author jieun(je-pa)
   */
  @PostConstruct
  private void initHandlers() {
    handlers.put(SEND, "sendCommandHandler");
  }

  /**
   * 메시지가 채널을 통해 전송되기 전에 호출되는 메서드 입니다.
   * <p>
   * 메시지를 변경하거나, 전송을 중지시킬 수 있습니다.
   *
   * @param message Payload: 메시지의 실제 데이터, Headers: 메시지 관련 메타데이터(메시지 ID, 타임스탬프, 컨텐츠 타입, 사용자 정의 헤더 등)
   * @param channel 메시지가 전송될 채널
   * @return message
   */
  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor
        .getAccessor(message, StompHeaderAccessor.class);

    if (accessor == null) {
      log.info("[ConnectCommandHandler][preSend] accessor is null");
      throw CustomException.from(ExceptionCode.STOMP_HEADER_ACCESSOR_NOT_FOUND_EXCEPTION);
    }
    String handlerName = handlers.get(accessor.getCommand());
    if (handlerName != null) {
      StompCommandHandler handler = this.applicationContext
          .getBean(handlerName, StompCommandHandler.class);
      handler.handle(accessor);
    }

    return message;
  }
}
