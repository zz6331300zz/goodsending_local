package com.goodsending.global.redis.handler;

import com.goodsending.bid.entity.Bid;
import com.goodsending.bid.repository.BidRepository;
import com.goodsending.bid.repository.ProductBidPriceMaxRepository;
import com.goodsending.bid.type.BidStatus;
import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.member.entity.Member;
import com.goodsending.order.entity.Order;
import com.goodsending.order.repository.OrderRepository;
import com.goodsending.product.entity.Product;
import com.goodsending.product.type.ProductStatus;
import com.goodsending.productmessage.event.CreateProductMessageEvent;
import com.goodsending.productmessage.type.MessageType;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : jieun
 * @Date : 2024. 08. 01.
 * @Team : GoodsEnding
 * @Project : goodsending-be :: goodsending
 */
@Service("bidPriceMaxKeyExpirationHandler")
@RequiredArgsConstructor
public class BidPriceMaxKeyExpirationHandler implements RedisMessageHandler {

  private final BidRepository bidRepository;
  private final OrderRepository orderRepository;
  private final ApplicationEventPublisher eventPublisher;

  /**
   * 낙찰자 자동 선정 후 주문 생성
   * <p>
   * <p>입찰 후 5분동안 추가 입찰이 없으면 마지막 입찰자가 낙찰자가 됩니다.
   * <p>입찰의 상태가 낙찰자는 SUCCESS, 낙찰자를 제외한 입찰은 FAIL로 업데이트 됩니다.
   * <p>낙찰자의 주문이 자동 생성됩니다.
   * <p>낙찰자를 제외한 유저는 환불처리(포인트, 캐시) 됩니다.
   *
   * @param message PRODUCT_BID_PRICE_MAX:{productId}
   */
  @Override
  @Transactional
  public void handle(String message) {
    long productId = Long.parseLong(
        message.substring(ProductBidPriceMaxRepository.KEY_PREFIX.length()));
    List<Bid> bids = bidRepository.findByProductId(productId);
    if (bids.isEmpty()) {
      throw CustomException.from(ExceptionCode.BID_NOT_FOUND);
    }

    setProduct(bids);
    handlerBids(bids);
  }

  private void setProduct(List<Bid> bids) {
    Product product = bids.get(0).getProduct();
    product.setDynamicEndDateTime(LocalDateTime.now());
    product.setStatus(ProductStatus.ENDED);
  }

  private void handlerBids(List<Bid> bids) {
    // 입찰금이 제일 큰 사람은 낙찰 성공해서 주문이 진행된다.
    Bid winningBid = bids.get(0);
    saveOrderByBid(winningBid);
    winningBid.setStatus(BidStatus.SUCCESSFUL);

    // 낙찰 메시지 이벤트 발행
    eventPublisher.publishEvent(CreateProductMessageEvent.of(
        winningBid.getMember().getMemberId(),
        winningBid.getProduct().getId(),
        MessageType.AUCTION_WINNER,
        winningBid.getPrice()));

    // 나머지 입찰자들에 대한 환불 처리
    Map<Member, Integer> cashRefunds = new HashMap<>();
    Map<Member, Integer> pointRefunds = new HashMap<>();

    for (int i = 1; i < bids.size(); i++) {
      Bid bid = bids.get(i);
      bid.setStatus(BidStatus.FAILED);

      int price = (bid.getPrice() != null) ? bid.getPrice() : 0;
      int usePoint = (bid.getUsePoint() != null) ? bid.getUsePoint() : 0;
      int refundCash = price - usePoint;

      Member member = bid.getMember();
      cashRefunds.merge(member, refundCash, Integer::sum);
      pointRefunds.merge(member, usePoint, Integer::sum);
    }

    // Cash와 Point를 한 번에 업데이트
    for (Map.Entry<Member, Integer> entry : cashRefunds.entrySet()) {
      Member member = entry.getKey();
      member.addCash(entry.getValue());
    }

    for (Map.Entry<Member, Integer> entry : pointRefunds.entrySet()) {
      Member member = entry.getKey();
      member.addPoint(entry.getValue());
    }
  }

  private Order saveOrderByBid(Bid bid) {
    return orderRepository.save(Order.from(bid));
  }
}
