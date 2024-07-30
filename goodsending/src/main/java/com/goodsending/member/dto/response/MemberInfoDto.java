package com.goodsending.member.dto.response;

import com.goodsending.member.entity.Member;
import com.goodsending.member.type.MemberRole;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberInfoDto {
  private Long memberId;
  private String email;
  private Integer cash;
  private Integer point;
  private MemberRole role;

  public MemberInfoDto(Member member) {
    this.memberId = member.getMemberId();
    this.email = member.getEmail();
    this.cash = member.getCash();
    this.point = member.getPoint();
    this.role = member.getRole();
  }
}
