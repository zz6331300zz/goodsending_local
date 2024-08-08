package com.goodsending.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {

    response.setContentType("application/json;charset=utf-8");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> map = new LinkedHashMap<>();
    // 기본 메시지를 설정
    String defaultMessage;
    if (authException instanceof InsufficientAuthenticationException) {
      defaultMessage = "잘못된 접근입니다.";
    } else {
      defaultMessage = authException.getMessage();
    }

    map.put("status", HttpServletResponse.SC_UNAUTHORIZED);
    map.put("error", "Unauthorized");
    map.put("message", defaultMessage);

    response.getWriter().write(mapper.writeValueAsString(map));

  }
}
