package com.goodsending.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.member.dto.request.LoginRequestDto;
import com.goodsending.member.entity.Member;
import com.goodsending.member.repository.MemberRepository;
import com.goodsending.member.type.MemberRole;
import com.goodsending.member.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final JwtUtil jwtUtil;
  private final MemberRepository memberRepository;

  public JwtAuthenticationFilter(JwtUtil jwtUtil, MemberRepository memberRepository) {
    this.jwtUtil = jwtUtil;
    this.memberRepository = memberRepository;
    setFilterProcessesUrl("/api/members/login");
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {
    log.info("로그인 시도");
    try {
      LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(),
          LoginRequestDto.class);
      // 이메일을 통해 사용자 정보를 조회합니다.
      Optional<Member> optionalMember = memberRepository.findByEmail(requestDto.getEmail());
      if (optionalMember.isPresent()) {
        Member member = optionalMember.get();
        if (member.isVerify()) { //인증 상태가 true 이면
          return getAuthenticationManager().authenticate(
              new UsernamePasswordAuthenticationToken(
                  requestDto.getEmail(),
                  requestDto.getPassword(),
                  null
              )
          );
        } else {
          throw CustomException.from(ExceptionCode.EMAIL_NOT_VERIFIED);
        }
      } else {
        throw CustomException.from(ExceptionCode.USER_NOT_FOUND);
      }
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
    MemberDetailsImpl memberDetails = (MemberDetailsImpl) authResult.getPrincipal();
    Long memberId = memberDetails.getMemberId();
    String email = memberDetails.getUsername();
    MemberRole role = memberDetails.getRole();

    String token = jwtUtil.createToken(memberId, email, role);
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
