package com.goodsending.order.service;

import com.goodsending.order.dto.request.ReceiverInfoRequest;
import com.goodsending.order.dto.response.ReceiverInfoResponse;

/**
 * @author : jieun(je-pa)
 * @Date : 2024. 08. 02.
 * @Team : GoodsEnding
 * @Project : goodsending-be :: goodsending
 */
public interface OrderService {

  /**
   * 낙찰자가 주문에 대한 배송 정보를 입력합니다.
   * @param memberId 로그인 유저 아이디
   * @param request 배송지 주소, 연락처, 수신자명
   * @return 저장된 주문 정보
   * @author : jieun(je-pa)
   */
  ReceiverInfoResponse updateReceiverInfo(Long memberId, ReceiverInfoRequest request);
}
