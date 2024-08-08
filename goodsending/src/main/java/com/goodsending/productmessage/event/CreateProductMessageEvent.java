package com.goodsending.productmessage.event;

import com.goodsending.productmessage.type.MessageType;

/**
 * @Date : 2024. 08. 07.
 * @Team : GoodsEnding
 * @author : jieun(je-pa)
 * @Project : goodsending-be :: goodsending
 */
public record CreateProductMessageEvent(
    Long memberId,
    Long productId,
    MessageType type,
    int price
) {
  public static CreateProductMessageEvent of
      (Long memberId, Long productId, MessageType type, int price){
    return new CreateProductMessageEvent(memberId,productId,type,price);
  }

  public String getMessageBy(String email){
    return String.format(this.type.getPattern(), maskEmail(email), this.price);
  }

  private static String maskEmail(String email) {
    // '@'로 이메일을 나눔
    int atIndex = email.indexOf('@');
    String user = email.substring(0, atIndex);
    String domain = email.substring(atIndex);

    // 사용자 이름의 절반 또는 그 이상을 *로 대체
    int lengthToMask = user.length() - 2;
    String maskedUser = user.substring(0, 2) + "*".repeat(lengthToMask);

    // 도메인과 결합하여 마스킹된 이메일 반환
    return maskedUser + domain;
  }
}
