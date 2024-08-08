package com.goodsending.global.security;

import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.member.repository.BlackListAccessTokenRepository;
import com.goodsending.member.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final BlackListAccessTokenRepository blackListAccessTokenRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
      FilterChain filterChain) throws ServletException, IOException {

    String tokenValue = jwtUtil.getJwtFromHeader(req);

    if (StringUtils.hasText(tokenValue)) {
      try {
        // 블랙리스트 확인
        if (isTokenBlacklisted(tokenValue)) {
          throw CustomException.from(ExceptionCode.EXPIRED_JWT_TOKEN);
        }
        // 1. 토큰 유효성 검사
        jwtUtil.validateToken(tokenValue);

        // 2. Refresh Token인지 확인
        if (isRefreshToken(tokenValue)) {
          throw CustomException.from(ExceptionCode.NOT_AN_ACCESS_TOKEN);
        }

        // 액세스 토큰인 경우, 사용자 정보 추출 및 인증 설정
        Claims info = jwtUtil.getUserInfoFromToken(tokenValue);
        try {
          setAuthentication(info.getSubject());
        } catch (Exception e) {
          log.error(e.getMessage());
          throw CustomException.from(ExceptionCode.INVALID_TOKEN);
        }

      } catch (CustomException e) {
        // 예외 발생 시, jwtAuthenticationEntryPoint를 사용하여 응답 처리
        jwtAuthenticationEntryPoint.commence(req, res,
            new AuthenticationException(e.getMessage()) {});
        return;
      }
    }
    // 필터 체인 진행
    filterChain.doFilter(req, res);
  }

  private boolean isRefreshToken(String token) {
    // getUserInfoFromToken 메서드를 사용하여 클레임 추출
    Claims claims = jwtUtil.getUserInfoFromToken(token);
    // 'token_type' 클레임이 'refresh'인지 확인
    String tokenType = (String) claims.get("token_type");
    return "refresh".equals(tokenType);
  }

  // Access Token이 블랙리스트에 있는지 확인
  public boolean isTokenBlacklisted(String accessToken) {
    return blackListAccessTokenRepository.hasKey(accessToken);
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
