package com.goodsending.bid.controller;

import com.goodsending.bid.dto.request.BidListByMemberRequest;
import com.goodsending.bid.dto.request.BidRequest;
import com.goodsending.bid.dto.response.BidWithDurationResponse;
import com.goodsending.bid.dto.response.BidWithProductResponse;
import com.goodsending.bid.service.BidFacade;
import com.goodsending.bid.service.BidService;
import com.goodsending.global.security.anotation.MemberId;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Date : 2024. 07. 25.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bids")
public class BidController {

  private final BidFacade bidFacade;
  private final BidService bidService;

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

  /**
   * 멤버별 입찰 내역 리스트를 조회합니다.
   * @param loginMemberId 로그인 유저 id
   * @param memberId 입찰 내열 조회할 멤버 id
   * @param cursorId 사용자에게 응답해준 마지막 데이터 id
   * @return 입찰 정보 리스트
   * @author : jieun(je-pa)
   */
  @Operation(summary = "멤버별 입찰 내역 리스트를 조회합니다.",
      description = "본인의 입찰 내역 리스트만 조회할 수 있습니다.")
  @GetMapping
  public ResponseEntity<Slice<BidWithProductResponse>> readByMember(
      @MemberId Long loginMemberId,
      @RequestParam Long memberId,
      @RequestParam(required = false) Long cursorId,
      @RequestParam(defaultValue = "15") Integer pageSize) {
    return ResponseEntity.ok(bidService.readByMember(
        new BidListByMemberRequest(loginMemberId, memberId, cursorId, pageSize)));
  }
}
