package com.goodsending.member.service;

import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.member.dto.request.SignupRequestDto;
import com.goodsending.member.dto.response.MemberInfoDto;
import com.goodsending.member.entity.Member;
import com.goodsending.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @Date : 2024. 07. 23. / 2024. 07. 29
 * @Team : GoodsEnding
 * @author : 이아람
 * @Project : goodsending-be :: goodsending
 */

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  // TODO : 관리자 할 경우 ADMIN_TOKEN 생성
  //private final String ADMIN_TOKEN = "1234";

  /**
   * 회원가입
   * <p>
   * 입력한 이메일, 코드 값 DB에서 확인 후 일치하면 비밀번호 암호화, verify = true로 DB update
   *
   * @param SignupRequestDto
   * @return 가입완료 문구 반환합니다.
   * @author : 이아람
   */
  public ResponseEntity<String> signup(SignupRequestDto signupRequestDto) {

    Optional<Member> checkEmail = memberRepository.findByEmail(signupRequestDto.getEmail());
    // 이메일, 코드 확인
    if (checkEmail.isPresent()) {
      if (!checkEmail.get().getCode().equals(signupRequestDto.getCode())) {
        throw CustomException.from(ExceptionCode.VERIFICATION_CODE_MISMATCH);
      }
    }
    // 비밀번호 일치 확인
    if (!signupRequestDto.getPassword().equals(signupRequestDto.getConfirmPassword())) {
      throw CustomException.from(ExceptionCode.PASSWORD_MISMATCH);
    }
    // 비밀번호 암호화
    String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());

    // TODO : 관리자 할 경우 사용자 ROLE 확인
    //MemberRole role = MemberRole.USER;
//        if (signupRequestDto.isAdmin()) {
//            if (!ADMIN_TOKEN.equals(signupRequestDto.getAdminToken())) {
//                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
//            }
//            role = MemberRole.ADMIN;
//        }
    boolean verify = true;
    Member member = checkEmail.get();
    member.update(encodedPassword, verify);
    memberRepository.save(member);
    return ResponseEntity.ok("가입 완료");
  }

  /**
   * 회원 정보 조회
   * <p>
   *DB에서 memberId 값 확인 후 MemberInfoDto 가져옵니다.
   *
   * @param 로그인 한 회원희 memberId
   * @return MemberInfoDto 반환합니다.
   * @author : 이아람
   */
  public MemberInfoDto getMemberInfo(Long memberId) {
    Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);
    Member member = optionalMember.orElseThrow(
        () -> CustomException.from(ExceptionCode.USER_NOT_FOUND));
    return new MemberInfoDto(member);
  }
}
