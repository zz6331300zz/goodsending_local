package com.goodsending.member.service;

import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.member.dto.request.MailRequestDto;
import com.goodsending.member.entity.Member;
import com.goodsending.member.repository.MemberRepository;
import com.goodsending.member.repository.SaveMailAndCodeRepository;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 *
 * @Date : 2024. 07. 27.
 * @Team : GoodsEnding
 * @author : 이아람
 * @Project : goodsending-be :: goodsending
 */

@Service
@RequiredArgsConstructor
public class MailService {

  private final JavaMailSender mailSender;
  private final MemberRepository memberRepository;
  private final SaveMailAndCodeRepository saveMailAndCodeRepository;

  @Value("${spring.mail.username}")
  private String fromEmail;

  /**
   * 메일 중복 확인 및 인증코드 전송, DB저장
   * <p>
   * 회원가입 하려는 유저의 이메일이 중복이 아닐 경우 메일주소로 인증코드를 전송하고 DB에 저장합니다.
   *
   * @param 회원가입 하려는 유저의 이메일
   * @return 인증완료 문구를 반환합니다.
   * @author : 이아람
   */
  public ResponseEntity<String> sendCode(MailRequestDto mailRequestDto) throws MessagingException, UnsupportedEncodingException {

    // 이메일 중복 확인
    Optional<Member> checkEmail = memberRepository.findByEmail(mailRequestDto.getEmail());
    if (checkEmail.isPresent()) {
      throw CustomException.from(ExceptionCode.EMAIL_ALREADY_EXISTS);
    }
    String code = this.createCode();

    // redis 저장 (5분 동안 유효)
    saveMailAndCodeRepository.setValue(mailRequestDto.getEmail(), code, Duration.ofMinutes(5));

    // 인증코드 메일 전송
    createMail(mailRequestDto.getEmail(), code);

    return ResponseEntity.ok("인증코드 전송 완료");
  }

  // 인증번호 만들기
  private String createCode() {
    int length = 6;
    Random random;
    try {
      random = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {
      throw CustomException.from(ExceptionCode.ALGORITHM_NOT_AVAILABLE);
    }

    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < length; i++) {
      builder.append(random.nextInt(10));
    }
    return builder.toString();
  }


  /**
   * 메일 생성 및 전송
   * <p>
   * 양식에 맞춰 메일을 생성하고 전송합니다.
   *
   * @param 이메일
   * @param 인증코드
   * @author : 이아람
   */
  public void createMail(String email, String code)
      throws MessagingException, UnsupportedEncodingException {
    MimeMessage mail = mailSender.createMimeMessage();
    String mailContent = createEmailContent(code);

    mail.addRecipients(Message.RecipientType.TO, email); // 받는 사람
    mail.setSubject("GoodsEnding 회원가입 인증 번호", "utf-8"); // 제목
    mail.setText(mailContent, "utf-8", "html"); // 내용
    mail.setFrom(new InternetAddress(fromEmail, "GoodsEnding")); // 보내는 사람
    mailSender.send(mail);
  }

  private String createEmailContent(String code) {
    return "<h3>GoodsEnding</h3>" +
        "<h1>이메일 인증번호 안내</h1><br>" +
        "<p>본 메일은 GoodsEnding 회원가입을 위한 이메일 인증입니다.</p>" +
        "<p>아래의 인증 번호를 입력하여 본인확인을 해주시기 바랍니다.</p><br>" +
        code;
  }
}