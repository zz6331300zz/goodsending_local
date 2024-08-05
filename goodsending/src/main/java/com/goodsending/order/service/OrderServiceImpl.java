package com.goodsending.order.service;

import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.order.dto.request.ReceiverInfoRequest;
import com.goodsending.order.dto.response.ReceiverInfoResponse;
import com.goodsending.order.dto.response.UpdateShippingResponse;
import com.goodsending.order.entity.Order;
import com.goodsending.order.repository.OrderRepository;
import java.time.LocalDateTime;
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

  /**
   * 판매자가 주문을 배송 출발 처리 합니다.
   * @param memberId 로그인 유저 id(판매자 id)
   * @param orderId 주문 id
   * @param now 현재시간
   * @return 업데이트된 주문 정보
   * @author : jieun(je-pa)
   */
  @Override
  @Transactional
  public UpdateShippingResponse updateShipping(Long memberId, Long orderId, LocalDateTime now) {
    Order order = findOrderWithBidAndProductById(orderId);

    if(!order.isSellerId(memberId)) {
      throw CustomException.from(ExceptionCode.SELLER_ID_MISMATCH);
    }

    if(!order.isPending()){
      throw CustomException.from(ExceptionCode.ORDER_IS_NOT_PENDING);
    }

    return UpdateShippingResponse.from(order.updateShipping(now));
  }

  private Order findOrderWithBidById(Long orderId) {
    return orderRepository.findOrderWithBidById(orderId).orElseThrow(
        () -> CustomException.from(ExceptionCode.ORDER_NOT_FOUND)
    );
  }

  private Order findOrderWithBidAndProductById(Long orderId) {
    return orderRepository.findOrderWithBidAndProductById(orderId).orElseThrow(
        () -> CustomException.from(ExceptionCode.ORDER_NOT_FOUND)
    );
  }
}
