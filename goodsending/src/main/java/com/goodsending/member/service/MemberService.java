package com.goodsending.member.service;

import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.member.dto.request.CashRequestDto;
import com.goodsending.member.dto.request.PasswordRequestDto;
import com.goodsending.member.dto.request.SignupRequestDto;
import com.goodsending.member.dto.response.MemberInfoDto;
import com.goodsending.member.entity.Member;
import com.goodsending.member.repository.BlackListAccessTokenRepository;
import com.goodsending.member.repository.MemberRepository;
import com.goodsending.member.repository.SaveMailAndCodeRepository;
import com.goodsending.member.repository.SaveRefreshTokenRepository;
import com.goodsending.member.type.MemberRole;
import com.goodsending.member.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : 이아람
 * @Date : 2024. 07. 23. / 2024. 07. 29
 * @Team : GoodsEnding
 * @Project : goodsending-be :: goodsending
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final SaveMailAndCodeRepository saveMailAndCodeRepository;
  private final JwtUtil jwtUtil;
  private final SaveRefreshTokenRepository saveRefreshTokenRepository;
  private final BlackListAccessTokenRepository blackListAccessTokenRepository;

  // TODO : 관리자 할 경우 ADMIN_TOKEN 생성
  //private final String ADMIN_TOKEN = "1234";

  /**
   * 회원가입
   * <p>
   * 입력한 코드가 redis에 저장된 코드와 일치하면 비밀번호 암호화해서 DB에 저장합니다.
   *
   * @param SignupRequestDto
   * @return 가입완료 문구 반환합니다.
   * @author : 이아람
   */
  public ResponseEntity<String> signup(SignupRequestDto signupRequestDto) {
    // 이메일 중복 확인
    Optional<Member> checkEmail = memberRepository.findByEmail(signupRequestDto.getEmail());
    if (checkEmail.isPresent()) {
      throw CustomException.from(ExceptionCode.EMAIL_ALREADY_EXISTS);
    }

    // redis에 저장되어있는 code와 일치하는지 확인
    String storedCode = saveMailAndCodeRepository.getValueByKey(signupRequestDto.getEmail());
    if (storedCode == null) {
      throw CustomException.from(ExceptionCode.CODE_EXPIRED_OR_INVALID);
    }
    if (!storedCode.equals(signupRequestDto.getCode())) {
      throw CustomException.from(ExceptionCode.VERIFICATION_CODE_MISMATCH);
    }

    // 비밀번호 일치 확인
    if (!signupRequestDto.getPassword().equals(signupRequestDto.getConfirmPassword())) {
      throw CustomException.from(ExceptionCode.PASSWORD_MISMATCH);
    }
    // 비밀번호 암호화
    String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());

    // TODO : 관리자 할 경우 사용자 ROLE 확인
    MemberRole role = MemberRole.USER;
//        if (signupRequestDto.isAdmin()) {
//            if (!ADMIN_TOKEN.equals(signupRequestDto.getAdminToken())) {
//                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
//            }
//            role = MemberRole.ADMIN;
//        }
    Member member = Member.from(signupRequestDto, encodedPassword, role);
    memberRepository.save(member);
    // redis에서 삭제
    saveMailAndCodeRepository.deleteByKey(signupRequestDto.getEmail());
    return ResponseEntity.ok("가입 완료");
  }


  /**
   * 회원 정보 조회
   * <p>
   * DB에서 memberId 값 확인 후 MemberInfoDto 가져옵니다.
   *
   * @param 로그인 한 회원희 memberId
   * @return MemberInfoDto 반환합니다.
   * @author : 이아람
   */
  public MemberInfoDto getMemberInfo(Long memberId) {
    Member member = findByMemberId(memberId);
    return new MemberInfoDto(member);
  }

  /**
   * 회원 비밀번호 변경
   * <p>
   * DB에서 memberId 값 확인 후 현재 비밀번호가 DB 비밀번호와 일치하면 새로운 비밀번호로 변경됩니다.
   *
   * @param 로그인 한 유저의 memberId, PasswordRequestDto
   * @return status 상태코드 반환합니다.
   * @author : 이아람
   */
  @Transactional
  public ResponseEntity<Void> updatePassword(Long pathMemberId, Long memberId,
      PasswordRequestDto passwordRequestDto) {
    if (!pathMemberId.equals(memberId)) {
      throw CustomException.from(ExceptionCode.MEMBER_ID_MISMATCH);
    }
    Member member = findByMemberId(memberId);
    if (member == null) {
      throw CustomException.from(ExceptionCode.MEMBER_NOT_FOUND);
    }
    // DB에 있는 비밀번호와 현재 비밀번호 일치하는지 확인
    if (!passwordEncoder.matches(passwordRequestDto.getCurrentPassword(), member.getPassword())) {
      throw CustomException.from(ExceptionCode.MEMBER_PASSWORD_INCORRECT);
    }
    // 입력한 새로운 비밀번호 확인
    if (!passwordRequestDto.getPassword().equals(passwordRequestDto.getConfirmPassword())) {
      throw CustomException.from(ExceptionCode.PASSWORD_MISMATCH);
    }
    // 비밀번호 암호화
    String encodedPassword = passwordEncoder.encode(passwordRequestDto.getPassword());
    member.passwordUpdate(encodedPassword);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  /**
   * 캐시 충전
   * <p>
   * 로그인 한 회원은 입력한 금액 만큼 캐시를 충전 할 수 있다.
   *
   * @param 로그인 한 회원 memberId, CashRequestDto
   * @return status 상태코드 반환합니다.
   * @author : 이아람
   */
  @Transactional
  public ResponseEntity<Void> updateCash(Long pathMemberId, Long memberId,
      CashRequestDto cashRequestDto) {
    if (!pathMemberId.equals(memberId)) {
      throw CustomException.from(ExceptionCode.MEMBER_ID_MISMATCH);
    }
    Member member = findByMemberId(memberId);
    if (member == null) {
      throw CustomException.from(ExceptionCode.MEMBER_NOT_FOUND);
    }
    // DB에서 가져온 cash 값이 null인 경우 0으로 처리
    Integer currentCash = (member.getCash() != null) ? member.getCash() : 0;
    member.cashUpdate(cashRequestDto.getCash() + currentCash);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  // memberId 검색
  private Member findByMemberId(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> CustomException.from(ExceptionCode.USER_NOT_FOUND));
  }

  /**
   * Access Token 재발급
   * <p>
   * Access Token이 만료 된 회원은 Refresh Token 기간이 남아 있다면 재발급 받을 수 있다.
   *
   * @param refreshToken
   * @return status 상태코드 반환합니다.
   * @author : 이아람
   */
  public ResponseEntity<Void> tokenReissue(String refreshToken) {
    // 쿠키에서 가져온 refresh token에서 email 정보 추출
    String email;
    try {
      Claims claims = jwtUtil.getUserInfoFromToken(refreshToken);
      email = claims.getSubject();
      log.info("재발급 1 : " + email);

    } catch (JwtException e) {
      log.error("재발급 1번 에러 email 추출 실패");
      throw CustomException.from(ExceptionCode.INVALID_TOKEN);
    }

    // redis에 저장된 refresh token 가져오기
    String redisRefreshToken = saveRefreshTokenRepository.getValueByKey(email);
    log.info("재발급 2 : " + redisRefreshToken);

    if (redisRefreshToken == null) {
      log.error("재발급 2번 에러 redisRefreshToken is null");
      throw CustomException.from(ExceptionCode.STORED_TOKEN_HAS_EXPIRED);
    }

    if (!refreshToken.equals(redisRefreshToken)) {
      log.error("재발급 2번 에러 저장된 refresh 토큰과 쿠키 토큰 불일치");
      throw CustomException.from(ExceptionCode.TOKEN_MISMATCH);
    }

    Optional<Member> memberOptional = memberRepository.findByEmail(email);
    log.info("재발급 3 : " + memberOptional.isPresent());

    if (memberOptional.isEmpty()) {
      log.error("재발급 3번 에러 memberOptional is Empty");
      throw CustomException.from(ExceptionCode.MEMBER_NOT_FOUND);
    }

    Member member = memberOptional.get();
    log.info("재발급 4 : " + member);

    // 새로운 Access Token 생성
    String newAccessToken = jwtUtil.createToken(member.getMemberId(), member.getEmail(),
        member.getRole());
    log.info("재발급 5 : " + newAccessToken);

    // header에 추가
    HttpHeaders header = new HttpHeaders();
    header.set(JwtUtil.AUTHORIZATION_HEADER, newAccessToken);
    log.info("재발급 6 : " + header);

    return new ResponseEntity<>(header, HttpStatus.OK);
  }

  /**
   * 로그아웃
   * <p>
   * Refresh Token 삭제 & Access Token blacklist에 저장 및 사용 불가된다.
   *
   * @param refreshToken, HttpServletRequest, HttpServletResponse
   * @return status 상태코드 반환합니다.
   * @author : 이아람
   */
  public ResponseEntity<Void> deleteRefreshToken(String refreshToken, HttpServletRequest request,
      HttpServletResponse response) {
    // email 추출
    Claims claims = jwtUtil.getUserInfoFromToken(refreshToken);
    String email = claims.getSubject();
    log.info("로그아웃 1 : " + email);
    // redis에서 삭제
    saveRefreshTokenRepository.deleteByKey(email);
    // refresh token 만료 시간 0으로 변경 = 삭제
    ResponseCookie cookie = ResponseCookie.from(JwtUtil.REFRESH_TOKEN_NAME)
        .httpOnly(true)
        .secure(true)
        .sameSite("None")
        .path("/")
        .maxAge(0)
        .build();
    // Response Header에 쿠키 추가
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    String accessToken = jwtUtil.getJwtFromHeader(request);
    log.info("로그아웃 2 : " + accessToken);
    // access token 만료 시간 계산
    long expirationTime = jwtUtil.getExpirationTime(accessToken) - System.currentTimeMillis();
    blackListAccessTokenRepository.setValue(accessToken, "true", Duration.ofMillis(expirationTime));
    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
