package com.goodsending.global.websocket.handler;

import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.member.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * @Date : 2024. 08. 09.
 * @Team : GoodsEnding
 * @author : jieun(je-pa)
 * @Project : goodsending-be :: goodsending
 */
@Slf4j
@Service("sendCommandHandler")
@RequiredArgsConstructor
public class SendCommandHandler implements StompCommandHandler{
  private final JwtUtil jwtUtil;
  private final UserDetailsService memberDetailsService;

  /**
   * SEND Command에 대한 핸들러 입니다.
   *
   * 서비스의 회원에게만 허용합니다.
   * @param accessor STOMP Command, Destination, Session ID, User, Sub ID, Native Headers, Heartbeat
   * @author jieun(je-pa)
   */
  @Override
  public void handle(StompHeaderAccessor accessor) {
    String accessToken = resolveAccessTokenFromStompHeaderAccessor(accessor);
    log.info("[ConnectCommandHandler][handle]" + accessToken);

    if (!jwtUtil.validateToken(accessToken)) {
      throw CustomException.from(ExceptionCode.INVALID_TOKEN);
    }

    accessor.setUser(createAuthentication(jwtUtil.getUserInfoFromToken(accessToken).getSubject()));
  }

  private String resolveAccessTokenFromStompHeaderAccessor(StompHeaderAccessor accessor) {
    String authorizationValue = accessor.getFirstNativeHeader(JwtUtil.AUTHORIZATION_HEADER);
    if (!ObjectUtils.isEmpty(authorizationValue)
        && authorizationValue.startsWith(JwtUtil.BEARER_PREFIX)) {
      return authorizationValue.substring(JwtUtil.BEARER_PREFIX.length());
    }
    return null;
  }

  private Authentication createAuthentication(String email) {
    UserDetails userDetails = memberDetailsService.loadUserByUsername(email);
    return new UsernamePasswordAuthenticationToken(userDetails, null,
        userDetails.getAuthorities());
  }
}
