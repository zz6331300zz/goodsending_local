package com.goodsending.bid.service;

import com.goodsending.bid.dto.request.BidListByMemberRequest;
import com.goodsending.bid.dto.request.BidRequest;
import com.goodsending.bid.dto.response.BidResponse;
import com.goodsending.bid.dto.response.BidWithProductResponse;
import java.time.LocalDateTime;
import org.springframework.data.domain.Slice;

/**
 * @Date : 2024. 07. 25.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
public interface BidService {

  BidResponse create(Long memberId, BidRequest request, LocalDateTime now);

  /**
   * 멤버별 입찰 내역 리스트 조회
   * @param request 조회에 사용되는 필드들을 담은 dto
   * @return 커서기반 페이징 처리된 입찰 내역 리스트
   * @author : jieun(je-pa)
   */
  Slice<BidWithProductResponse> readByMember(BidListByMemberRequest request);
}
