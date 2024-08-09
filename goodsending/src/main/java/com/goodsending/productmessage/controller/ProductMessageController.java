package com.goodsending.productmessage.controller;

import com.goodsending.productmessage.dto.request.ProductMessageListRequest;
import com.goodsending.productmessage.dto.response.ProductMessageResponse;
import com.goodsending.productmessage.service.ProductMessageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Date : 2024. 08. 07.
 * @Team : GoodsEnding
 * @author : jieun(je-pa)
 * @Project : goodsending-be :: goodsending
 */
@Controller
@RequestMapping("/api/product-message-histories")
@RequiredArgsConstructor
public class ProductMessageController {
  private final ProductMessageService productMessageService;

  @Operation(summary = "상품별 메시지 내역 조회 기능",
      description = "상품별 메시지 내역을 커서 기반 페이징으로 조회합니다.")
  @GetMapping
  public ResponseEntity<Slice<ProductMessageResponse>> read(@RequestParam Long productId,
      @RequestParam(defaultValue = "15") int size, @RequestParam(required = false) Long cursorId){
    return ResponseEntity.ok(productMessageService.read(ProductMessageListRequest.of(
        productId, size, cursorId
    )));
  }
}
