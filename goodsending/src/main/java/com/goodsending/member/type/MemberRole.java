package com.goodsending.member.type;

public enum MemberRole {

  ADMIN(Authority.ADMIN),
  USER(Authority.USER);

  private final String authority;

  MemberRole(String authority) {

    this.authority = authority;
  }

  public String getAuthority() {

    return this.authority;
  }

  public static class Authority {

    public static final String ADMIN = "ROLE_ADMIN";
    public static final String USER = "ROLE_USER";
  }
}
