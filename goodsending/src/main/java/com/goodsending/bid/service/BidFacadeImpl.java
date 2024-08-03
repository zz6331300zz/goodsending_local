package com.goodsending.bid.service;

import com.goodsending.bid.dto.request.BidRequest;
import com.goodsending.bid.dto.response.BidResponse;
import com.goodsending.bid.dto.response.BidWithDurationResponse;
import com.goodsending.bid.repository.ProductBidPriceMaxRepository;
import com.goodsending.global.websocket.DestinationPrefix;
import jakarta.persistence.OptimisticLockException;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * @Date : 2024. 07. 30.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BidFacadeImpl implements BidFacade {
  private final BidService bidService;
  private final ProductBidPriceMaxRepository productBidPriceMaxRepository;
  private final SimpMessagingTemplate messagingTemplate;

  @Override
  public BidWithDurationResponse create(Long memberId, BidRequest request, LocalDateTime now)
      throws InterruptedException {
    while(true){
      try {
        BidResponse bidResponse = bidService.create(memberId, request, now);

        // 현재 최고 금액을 업데이트 해준다.
        productBidPriceMaxRepository.setValueWithDuration(request.productId(), request.bidPrice());

        // 경매 마감까지 남은 시간: 업데이트 메시지를 보낸다.
        Duration remainingExpiration = productBidPriceMaxRepository.getRemainingExpiration(
            bidResponse.productId());
        messagingTemplate.convertAndSend(
            DestinationPrefix.TIME_REMAINING + bidResponse.productId(),
            remainingExpiration);

        // 입찰자 수: 변동 사항을 알려준다.
        messagingTemplate.convertAndSend(
            DestinationPrefix.BIDDER_COUNT + bidResponse.productId(),
            bidResponse.biddingCount());

        return BidWithDurationResponse.of(bidResponse, remainingExpiration);
      } catch (OptimisticLockException | OptimisticLockingFailureException e){
        Thread.sleep(50);
        log.info("OptimisticLockException");
      }
    }
  }
}
