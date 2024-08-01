package com.goodsending.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {
    // TODO 로그인 사이트 완성되면 redirect로 변경 예정
    //response.sendRedirect("/login");

    response.setContentType("application/json;charset=utf-8");

    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> map = new LinkedHashMap<>();

    map.put("status", HttpServletResponse.SC_UNAUTHORIZED);
    map.put("error", "Unauthorized");
    map.put("message", "인증이 필요합니다.");

    response.getWriter().write(mapper.writeValueAsString(map));

  }
}
