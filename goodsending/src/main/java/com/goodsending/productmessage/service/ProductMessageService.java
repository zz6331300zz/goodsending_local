package com.goodsending.productmessage.service;

import com.goodsending.global.websocket.dto.ProductMessageDto;
import com.goodsending.productmessage.dto.ProductMessageListRequest;
import com.goodsending.productmessage.dto.ProductMessageResponse;
import com.goodsending.productmessage.event.CreateProductMessageEvent;
import org.springframework.data.domain.Slice;

public interface ProductMessageService {
  ProductMessageDto create(CreateProductMessageEvent event);

  Slice<ProductMessageResponse> read(ProductMessageListRequest request);
}
