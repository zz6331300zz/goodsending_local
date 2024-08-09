package com.goodsending.productmessage.controller;

import com.goodsending.global.websocket.DestinationPrefix;
import com.goodsending.global.websocket.dto.ProductMessageDto;
import com.goodsending.product.dto.request.ChatMessageRequest;
import com.goodsending.productmessage.dto.request.ProductMessageRequest;
import com.goodsending.productmessage.service.ProductMessageService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Date : 2024. 08. 09.
 * @Team : GoodsEnding
 * @author : jieun(je-pa)
 * @Project : goodsending-be :: goodsending
 */
@RestController
@RequiredArgsConstructor
public class ChatMessageController {
  private final ProductMessageService productMessageService;
  private final SimpMessagingTemplate messagingTemplate;

  /**
   * 상품 상세페이지에서 채팅을 보냅니다.
   * @param principal 유저정보
   * @param request 메시지 정보
   * @author jieun(je-pa)
   */
  @MessageMapping("/message")
  public void chatToProduct(Principal principal, @Payload ChatMessageRequest request) {
    ProductMessageDto productMessageDto = productMessageService.create(
        ProductMessageRequest.of(request, principal.getName()));

    messagingTemplate.convertAndSend(DestinationPrefix.PRODUCT + request.productId(),
        productMessageDto);
  }

}
