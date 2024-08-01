package com.goodsending.global.config;

import com.goodsending.global.websocket.interceptor.WebSocketInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * @Date : 2024. 07. 27.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
@Order(Ordered.HIGHEST_PRECEDENCE) // WebSocketInterceptor가 먼저 처리될 수 있도록 설정
@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker // 메시지 브로커가 지원하는 WebSocket 메시지 처리를 활성화
public class WebSocketBrokerConfig implements WebSocketMessageBrokerConfigurer {

  private final WebSocketInterceptor webSocketInterceptor;

  /**
   * STOMP에서 사용하는 메시지 브로커를 설정하는 부분
   * <p>
   * enableSimpleBroker: 스프링이 제공하는 내장 브로커 사용 설정 . subscribe 메시지 접두사 설정. prefix가 붙은 메시지를 발행 시
   * SimpleBroker가 처리
   * <p>
   * setApplicationDestinationPrefixes: 바로 브로커로 가는 경우가 아닌 메시지 가공이 필요한 경우 핸들러를 거치게 처리
   *
   * @param registry
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/topic");
    registry.setApplicationDestinationPrefixes("/app");
  }

  /**
   * addEndpoint: 웹소켓 엔드포인트 지정
   * <p>
   * withSockJS: SockJS를 사용해 브라우저에서 websocket을 지원하지 않을 경우 대체 옵션 지원
   *
   * @param registry
   */
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(webSocketInterceptor);
  }
}
