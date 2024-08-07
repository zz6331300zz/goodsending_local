package com.goodsending.global.security;

import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.member.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final MemberDetailsServiceImpl memberDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
      FilterChain filterChain) throws ServletException, IOException {

    String tokenValue = jwtUtil.getJwtFromHeader(req);
    // 토큰이 존재하고 Refresh Token이 아닌 경우에만 처리
    if (StringUtils.hasText(tokenValue)&& !isRefreshToken(tokenValue)) {

      if (!jwtUtil.validateToken(tokenValue)) {
        log.error("Token Error");
        return;
      }

      Claims info = jwtUtil.getUserInfoFromToken(tokenValue);

      try {
        setAuthentication(info.getSubject());
      } catch (Exception e) {
        log.error(e.getMessage());
        return;
      }
    }

    filterChain.doFilter(req, res);
  }

  private boolean isRefreshToken(String token) {
    try {
      // getUserInfoFromToken 메서드를 사용하여 클레임 추출
      Claims claims = jwtUtil.getUserInfoFromToken(token);
      // 'token_type' 클레임이 'refresh'인지 확인
      String tokenType = (String) claims.get("token_type");
      return "refresh".equals(tokenType);

    } catch (JwtException e) {
      throw CustomException.from(ExceptionCode.INVALID_TOKEN);
    }
  }

  // 인증 처리
  public void setAuthentication(String email) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    Authentication authentication = createAuthentication(email);
    context.setAuthentication(authentication);

    SecurityContextHolder.setContext(context);
  }

  // 인증 객체 생성
  private Authentication createAuthentication(String email) {
    UserDetails userDetails = memberDetailsService.loadUserByUsername(email);
    return new UsernamePasswordAuthenticationToken(userDetails, null,
        userDetails.getAuthorities());
  }
}
