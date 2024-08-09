package com.goodsending.productmessage.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @Date : 2024. 08. 07.
 * @Team : GoodsEnding
 * @author : jieun(je-pa)
 * @Project : goodsending-be :: goodsending
 */
@RequiredArgsConstructor
public enum MessageType {
  BID("입찰 메시지",
             """
             %s님이 %d원에 입찰하셨습니다.
             """.stripIndent()), // 이메일은 별표 처리
  AUCTION_WINNER("경매 낙찰자 탄생 메시지",
             """
             %s님이 %d원에 낙찰되었습니다.
             """.stripIndent()),
  GENERAL_CHAT("일반 채팅","없음"),
  ANNOUNCEMENT("공지","없음")
  ;

  private final String name;

  @Getter
  private final String pattern;

}