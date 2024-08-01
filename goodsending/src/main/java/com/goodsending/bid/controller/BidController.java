package com.goodsending.bid.controller;

import com.goodsending.bid.dto.request.BidRequest;
import com.goodsending.bid.dto.response.BidWithDurationResponse;
import com.goodsending.bid.service.BidFacade;
import com.goodsending.global.security.anotation.MemberId;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Date : 2024. 07. 25.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bid")
public class BidController {

  private final BidFacade bidFacade;

  /**
   * 입찰을 신청합니다.
   * @param memberId 로그인 유저 id
   * @param request 입찰 신청 정보
   * @return 생성된 입찰 정보
   * @author : jieun
   */
  @Operation(summary = "입찰 신청 기능", description = "유저가 캐시와 포인트를 사용하여 입찰합니다.")
  @PostMapping
  public ResponseEntity<BidWithDurationResponse> create(
      @MemberId(required = true) Long memberId, @RequestBody @Valid BidRequest request)
      throws InterruptedException {
    return ResponseEntity.ok(bidFacade.create(memberId, request, LocalDateTime.now()));
  }
}
