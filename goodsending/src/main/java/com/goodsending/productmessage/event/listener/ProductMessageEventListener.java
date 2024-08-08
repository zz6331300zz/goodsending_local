package com.goodsending.productmessage.event.listener;

import com.goodsending.global.websocket.DestinationPrefix;
import com.goodsending.global.websocket.dto.ProductMessageDto;
import com.goodsending.productmessage.event.CreateProductMessageEvent;
import com.goodsending.productmessage.service.ProductMessageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @Date : 2024. 07. 30.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
@Component
@RequiredArgsConstructor
public class ProductMessageEventListener {
  private final ProductMessageServiceImpl productMessageService;
  private final SimpMessagingTemplate messagingTemplate;

  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void handleCreateProductMessageEvent(CreateProductMessageEvent event) {
    ProductMessageDto productMessageDto = productMessageService.create(event);

    // product에 메시지를 보낸다
        messagingTemplate.convertAndSend(
        DestinationPrefix.PRODUCT + productMessageDto.productId(), productMessageDto);
  }
}
