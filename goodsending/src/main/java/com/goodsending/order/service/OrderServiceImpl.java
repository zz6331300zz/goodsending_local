package com.goodsending.order.service;

import com.goodsending.charge.entity.ChargeHistory;
import com.goodsending.charge.repository.ChargeHistoryRepository;
import com.goodsending.deposit.entity.Deposit;
import com.goodsending.deposit.repository.DepositRepository;
import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.order.dto.request.ReceiverInfoRequest;
import com.goodsending.order.dto.response.OrderResponse;
import com.goodsending.order.dto.response.ReceiverInfoResponse;
import com.goodsending.order.dto.response.UpdateShippingResponse;
import com.goodsending.order.entity.Order;
import com.goodsending.order.repository.OrderRepository;
import java.time.LocalDateTime;
import com.goodsending.product.entity.Product;
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
  private final ChargeHistoryRepository chargeHistoryRepository;
  private final DepositRepository depositRepository;
  private static final double CHARGE_PERCENT = 0.05;
  private static final double POINT_PERCENT = 0.025;

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

  /**
   * 수신자가 배송을 받은 후 거래확정을 합니다.
   * @param memberId 수신자(=낙찰자) id
   * @param orderId 주문 id
   * @param now 현재시간
   * @return 업데이트된 order 정보를 반환합니다.
   */
  @Override
  @Transactional
  public OrderResponse confirmOrder(Long memberId, Long orderId, LocalDateTime now) {
    Order order = findOrderWithBidAndProductAndSellerById(orderId);

    if(!order.isReceiverId(memberId)) {
      throw CustomException.from(ExceptionCode.RECEIVER_ID_MISMATCH);
    }

    if(!order.isShipping()){
      throw CustomException.from(ExceptionCode.ORDER_IS_NOT_SHIPPING);
    }

    processSettlement(order);

    return OrderResponse.from(order.processConfirm(now));
  }

  // TODO: 동시성 문제 처리 필요
  // 수수료, 포인트, 판매자 수익, 보증금 정산
  private void processSettlement(Order order) {
    Integer price = order.getBid().getPrice();
    int charge = (int)(price * CHARGE_PERCENT);
    int point = (int)(price * POINT_PERCENT);
    int sellerRevenue = price - charge;
    Deposit deposit = findDepositByProduct(order.getBid().getProduct());
    int depositPrice = deposit.getPrice();

    // 판매자 수익 + 보증금 환불
    order.getBid().getProduct().getMember().addCash(sellerRevenue + depositPrice);
    deposit.processReturn();

    // 수신자 포인트 적립
    order.getBid().getMember().addPoint(point);

    // TODO: 누적금액 보증금 수정되면 같이 맞춰서 수수료도 수정필요(ex 누적금액)
    // 수수료 내역 추가
    chargeHistoryRepository.save(ChargeHistory.of(order, charge, charge));
  }

  private Deposit findDepositByProduct(Product product) {
    return depositRepository.findByProduct(product);
  }

  private Order findOrderWithBidAndProductAndSellerById(Long orderId) {
    return orderRepository.findOrderWithBidAndProductAndSellerById(orderId).orElseThrow(
        () -> CustomException.from(ExceptionCode.ORDER_NOT_FOUND)
    );
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
