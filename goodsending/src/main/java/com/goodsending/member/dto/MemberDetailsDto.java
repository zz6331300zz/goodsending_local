package com.goodsending.member.dto;

import com.goodsending.member.entity.Member;
import com.goodsending.member.type.MemberRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberDetailsDto {
  private final Long memberId;
  private final String email;
  private final String password;
  private final MemberRole role;

  public static MemberDetailsDto from(Member member) {
    return new MemberDetailsDto(
        member.getMemberId(),
        member.getEmail(),
        member.getPassword(),
        member.getRole()
    );
  }
}
