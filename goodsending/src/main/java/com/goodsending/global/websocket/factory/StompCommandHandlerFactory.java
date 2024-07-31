package com.goodsending.global.websocket.factory;

import static org.springframework.messaging.simp.stomp.StompCommand.CONNECT;

import com.goodsending.global.security.MemberDetailsServiceImpl;
import com.goodsending.global.websocket.handler.ConnectCommandHandler;
import com.goodsending.global.websocket.handler.StompCommandHandler;
import com.goodsending.member.util.JwtUtil;
import jakarta.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.stereotype.Component;

/**
 * @Date : 2024. 07. 27.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
@Component
@RequiredArgsConstructor
public class StompCommandHandlerFactory {
  private static final Map<StompCommand, StompCommandHandler> handlers
      = new EnumMap<>(StompCommand.class);
  private final JwtUtil jwtUtil;
  private final MemberDetailsServiceImpl memberDetailsService;

  @PostConstruct
  private void initHandlers() {
    handlers.put(CONNECT, new ConnectCommandHandler(jwtUtil, memberDetailsService));
  }

  public StompCommandHandler getHandler(StompCommand command) {
    return handlers.get(command);
  }
}