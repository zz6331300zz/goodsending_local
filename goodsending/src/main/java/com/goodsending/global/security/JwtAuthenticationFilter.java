package com.goodsending.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.member.dto.request.LoginRequestDto;
import com.goodsending.member.repository.SaveRefreshTokenRepository;
import com.goodsending.member.type.MemberRole;
import com.goodsending.member.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final JwtUtil jwtUtil;
  private final SaveRefreshTokenRepository saveRefreshTokenRepository;
  private final Duration REFRESH_TOKEN_EXPIRE = Duration.ofDays(14);

  public JwtAuthenticationFilter(JwtUtil jwtUtil,
      SaveRefreshTokenRepository saveRefreshTokenRepository) {
    this.jwtUtil = jwtUtil;
    this.saveRefreshTokenRepository = saveRefreshTokenRepository;
    setFilterProcessesUrl("/api/members/login");
  }

  //@Value("${front.domain}")
  //private String frontDomain;

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {
    log.info("로그인 시도");
    try {
      LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(),
          LoginRequestDto.class);

      return getAuthenticationManager().authenticate(
          new UsernamePasswordAuthenticationToken(
              requestDto.getEmail(),
              requestDto.getPassword(),
              null
          )
      );
    } catch (IOException e) {
      throw CustomException.from(ExceptionCode.USER_NOT_FOUND);
    }
  }


  @Override
  protected void successfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, FilterChain chain, Authentication authResult)
      throws IOException, ServletException {
    log.info("로그인 성공 및 JWT 생성");
    MemberDetailsImpl memberDetails = (MemberDetailsImpl) authResult.getPrincipal();
    Long memberId = memberDetails.getMemberId();
    String email = memberDetails.getUsername();
    MemberRole role = memberDetails.getRole();

    // JWT, Refresh 토큰 생성
    String token = jwtUtil.createToken(memberId, email, role);
    String refresh = jwtUtil.createRefreshToken(email);

    // header에 토큰 추가
    response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);
    // Refresh Token을 HttpOnly 쿠키에 설정
    addCookie(response, refresh);

    // redis 저장
    saveRefreshTokenRepository.setValue(email, refresh, REFRESH_TOKEN_EXPIRE);
    response.setContentType("text/plain;charset=UTF-8");
    response.getWriter().write("로그인 성공");
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed)
      throws IOException, ServletException {
    log.info("로그인 실패");
    response.setStatus(401);
    response.setContentType("text/plain;charset=UTF-8");
    response.getWriter().write("로그인 실패");
  }

  public void addCookie(HttpServletResponse response, String value) {
    ResponseCookie cookie = ResponseCookie.from(JwtUtil.REFRESH_TOKEN_NAME, value)
        .httpOnly(true)
        .secure(true)
        .sameSite("None")
        .path("/")
        .maxAge(1209600)
        .build();
    // Response Header에 쿠키 추가
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    ResponseEntity.ok().body("Cookie has been set");
  }
}
