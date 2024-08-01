package com.goodsending.member.entity;

import com.goodsending.global.entity.BaseEntity;
import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.member.dto.request.SignupRequestDto;
import com.goodsending.member.type.MemberRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@EqualsAndHashCode(of = "memberId")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "members")
public class Member extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_id")
  private Long memberId;

  @Column(name = "email", nullable = false, unique = true, length = 40)
  private String email;

  @Column(name = "password", nullable = false, length = 60)
  private String password;

  @Column(name = "cash", nullable = true)
  private Integer cash;

  @Column(name = "point", nullable = true)
  private Integer point;

  @Column(name = "code", nullable = true)
  private String code;

  @Column(name = "role", nullable = false)
  @Enumerated(value = EnumType.STRING)
  private MemberRole role; // 권한 (ADMIN, USER)

  @Column(name = "verify", nullable = false)
  private boolean verify; // 인증여부 (0: false, 1: true)

  @Version
  private Long version;

  @Builder
  public Member(String email, String password, String code, MemberRole role, boolean verify) {
    this.email = email;
    this.password = password;
    this.code = code;
    this.role = role;
    this.verify = verify;
  }

  public static Member from(String email, String code) {
    return Member.builder()
        .email(email)
        .password("")
        .code(code)
        .role(MemberRole.USER)
        .verify(false)
        .build();
  }

  public static Member from(SignupRequestDto signupRequestDto, String encodedPassword,
      MemberRole role, boolean verify) {
    return Member.builder()
        .email(signupRequestDto.getEmail())
        .password(encodedPassword)
        .code(signupRequestDto.getCode())
        .role(role)
        .verify(verify)
        .build();
  }
 // TODO : redis 사용하면 삭제 될 예정
  public void update(String encodedPassword, boolean verify) {
    this.password = encodedPassword;
    this.verify = verify;
  }

  public void passwordUpdate(String encodedPassword) {
    this.password = encodedPassword;
  }

  public void cashUpdate(Integer cash) {
    this.cash = cash;
  }

  public boolean isCashGreaterOrEqualsThan(Integer amount){
    if (this.cash == null || amount == null) {
      return false;
    }
    return this.cash >= amount;
  }

  public boolean isPointGreaterOrEqualsThan(Integer amount){
    if(this.point == null || amount == null) {
      return false;
    }
    return this.point >= amount;
  }

  public void addCash(int cash) {
    if(this.cash == null){
      this.cash = cash;
      return;
    }
    this.cash += cash;
  }

  public void addPoint(int point){
    if(this.point == null){
      this.point = point;
      return;
    }
    this.point += point;
  }

  public void deductCash(Integer amount) {
    if(!this.isCashGreaterOrEqualsThan(amount)){
      throw CustomException.from(ExceptionCode.USER_CASH_MUST_BE_POSITIVE);
    }

    this.cash -= amount;
  }

  public void deductPoint(Integer amount) {
    if (!this.isPointGreaterOrEqualsThan(amount)) {
      throw CustomException.from(ExceptionCode.USER_POINT_MUST_BE_POSITIVE);
    }

    this.point -= amount;
  }
}
