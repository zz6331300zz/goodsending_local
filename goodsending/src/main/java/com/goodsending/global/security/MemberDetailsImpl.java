package com.goodsending.global.security;

import com.goodsending.member.dto.MemberDetailsDto;
import com.goodsending.member.type.MemberRole;
import java.util.ArrayList;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
public class MemberDetailsImpl implements UserDetails {

  private final MemberDetailsDto memberDetailsDto;

  @Override
  public String getPassword() {

    return memberDetailsDto.getPassword();
  }

  @Override
  public String getUsername() {

    return memberDetailsDto.getEmail();
  }

  public MemberRole getRole() {
    return memberDetailsDto.getRole();
  }

  public Long getMemberId() {
    return memberDetailsDto.getMemberId();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    MemberRole role = memberDetailsDto.getRole();
    String authority = role.getAuthority();

    SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(simpleGrantedAuthority);

    return authorities;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
