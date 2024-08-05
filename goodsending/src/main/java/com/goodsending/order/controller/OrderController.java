package com.goodsending.order.controller;

import com.goodsending.global.security.anotation.MemberId;
import com.goodsending.order.dto.request.ReceiverInfoRequest;
import com.goodsending.order.dto.response.OrderResponse;
import com.goodsending.order.dto.response.ReceiverInfoResponse;
import com.goodsending.order.dto.response.UpdateShippingResponse;
import com.goodsending.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Date : 2024. 08. 02.
 * @Team : GoodsEnding
 * @author : jieun(je-pa)
 * @Project : goodsending-be :: goodsending
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {
  private final OrderService orderService;

  /**
   *
   * @param memberId 로그인 유저 아이디
   * @param request 수신자명, 수신자연락처, 수신자 주소를 받습니다.
   * @return 저장된 order 정보를 반환합니다.
   * @author : jieun(je-pa)
   */
  @Operation(summary = "주문 상품 수신자 정보 업데이트",
      description = "주문 상품 수신자 정보(수신자명, 수신자연락처, 수신자 주소)를 업데이트 합니다.")
  @PutMapping("/{orderId}/receiver-info")
  public ResponseEntity<ReceiverInfoResponse> updateReceiverInfo(
      @MemberId Long memberId,
      @PathVariable Long orderId,
      @RequestBody ReceiverInfoRequest request){
    return ResponseEntity.ok(orderService.updateReceiverInfo(memberId, orderId, request));
  }

  /**
   * 판매자가 주문을 배송 출발 처리 합니다.
   * @param memberId 로그인 유저 아이디 => 판매자 id
   * @param orderId 주문 id
   * @return 업데이트된 order 정보를 반환합니다.
   * @author : jieun(je-pa)
   */
  @Operation(summary = "주문 배송 출발 처리",
      description =  "판매자가 주문을 배송 출발 처리 합니다.")
  @PutMapping("/{orderId}/delivery")
  public ResponseEntity<UpdateShippingResponse> updateShipping(@MemberId Long memberId,
      @PathVariable Long orderId){
    return ResponseEntity.ok(orderService.updateShipping(memberId, orderId, LocalDateTime.now()));
  }

  /**
   * 수신자가 배송을 받은 후 거래확정을 합니다.
   * @param memberId 로그인 유저 아이디 => 수신자(=낙찰자)id
   * @param orderId 주문 id
   * @return 업데이트된 order 정보를 반환합니다.
   * @author : jieun(je-pa)
   */
  @Operation(summary = "수신자가 거래를 확정합니다.",
      description = "수신자가 배송을 받은 후 거래확정을 합니다.")
  @PutMapping("/{orderId}/confirm")
  public ResponseEntity<OrderResponse> confirmOrder(@MemberId Long memberId, @PathVariable Long orderId) {
    return ResponseEntity.ok(orderService.confirmOrder(memberId, orderId, LocalDateTime.now()));
  }

}
