package com.goodsending.order.controller;

import com.goodsending.global.security.anotation.MemberId;
import com.goodsending.order.dto.request.ReceiverInfoRequest;
import com.goodsending.order.dto.response.ReceiverInfoResponse;
import com.goodsending.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
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
}
