package com.goodsending.bid.dto.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * 커서 기반으로 멤버별 입찰 내역을 조회하기 위한 필드를 담은 dto 입니다.
 * @Date : 2024. 08. 05.
 * @Team : GoodsEnding
 * @author : jieun(je-pa)
 * @Project : goodsending-be :: goodsending
 */
public record BidListByMemberRequest(
    Long loginMemberId,

    Long memberId,

    Long cursorId,

    int pageSize
) {
  public Pageable getPageable() {
    return PageRequest.of(0, this.pageSize);
  }
}
