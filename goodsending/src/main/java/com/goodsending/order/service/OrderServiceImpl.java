package com.goodsending.order.service;

import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.order.dto.request.ReceiverInfoRequest;
import com.goodsending.order.dto.response.ReceiverInfoResponse;
import com.goodsending.order.entity.Order;
import com.goodsending.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Date : 2024. 08. 02.
 * @Team : GoodsEnding
 * @author : jieun(je-pa)
 * @Project : goodsending-be :: goodsending
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

  private final OrderRepository orderRepository;

  /**
   * 낙찰자가 주문에 대한 배송 정보를 입력합니다.
   * @param memberId 로그인 유저 아이디
   * @param request 배송지 주소, 연락처, 수신자명
   * @return 저장된 주문 정보
   * @author : jieun(je-pa)
   */
  @Override
  @Transactional
  public ReceiverInfoResponse updateReceiverInfo(Long memberId, Long orderId, ReceiverInfoRequest request) {
    Order order = findOrderWithBidById(orderId);

    if(!order.isReceiverId(memberId)) {
      throw CustomException.from(ExceptionCode.RECEIVER_ID_MISMATCH);
    }

    return ReceiverInfoResponse.from(order.updateReceiverInfo(request));
  }

  private Order findOrderWithBidById(Long orderId) {
    return orderRepository.findOrderWithBidById(orderId).orElseThrow(
        () -> CustomException.from(ExceptionCode.ORDER_NOT_FOUND)
    );
  }
}
