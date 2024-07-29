package com.goodsending.member.entity;

import com.goodsending.global.entity.BaseEntity;
import com.goodsending.member.dto.request.SignupRequestDto;
import com.goodsending.member.type.MemberRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
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

  @Column(name = "phone_number", nullable = false, length = 14)
  private String phoneNumber;

  @Column(name = "cash", nullable = true)
  private Integer cash;

  @Column(name = "point", nullable = true)
  private Integer point;

  @Column(name = "code", nullable = true)
  private Long code;

  @Column(nullable = false)
  @Enumerated(value = EnumType.STRING)
  private MemberRole role; // 권한 (ADMIN, USER)

  @Builder
  private Member(String email, String password, String phoneNumber, MemberRole role) {
    this.email = email;
    this.password = password;
    this.phoneNumber = phoneNumber;
    this.role = role;
  }

  public static Member from(SignupRequestDto signupRequestDto, String encodedPassword,
      MemberRole role) {
    return Member.builder()
        .email(signupRequestDto.getEmail())
        .password(encodedPassword)
        .phoneNumber(signupRequestDto.getPhoneNumber())
        .role(role)
        .build();
  }
}
