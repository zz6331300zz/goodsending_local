package com.goodsending.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodsending.member.dto.request.LoginRequestDto;
import com.goodsending.member.type.MemberRole;
import com.goodsending.member.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final JwtUtil jwtUtil;

  public JwtAuthenticationFilter(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
    setFilterProcessesUrl("/api/members/login");
  }

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
      log.error(e.getMessage());
      throw new RuntimeException(e.getMessage());
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, FilterChain chain, Authentication authResult)
      throws IOException, ServletException {
    log.info("로그인 성공 및 JWT 생성");
    String email = ((MemberDetailsImpl) authResult.getPrincipal()).getUsername();
    MemberRole role = ((MemberDetailsImpl) authResult.getPrincipal()).getRole();

    String token = jwtUtil.createToken(email, role);
    response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);
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

}
