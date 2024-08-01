package com.goodsending.global.redis.listener;

import com.goodsending.bid.repository.ProductBidPriceMaxRepository;
import com.goodsending.global.redis.handler.RedisMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * @author : jieun
 * @Date : 2024. 07. 31.
 * @Team : GoodsEnding
 * @Project : goodsending-be :: goodsending
 */
@Component
@Slf4j
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {
  private final ApplicationContext applicationContext;

  public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer,
      ApplicationContext applicationContext) {
    super(listenerContainer);
    this.applicationContext = applicationContext;
  }

  /**
   * 만료된 키에 대한 처리를 수행합니다.
   *
   * @param message redis key
   * @param pattern __keyevent@*__:expired
   */
  @Override
  public void onMessage(Message message, byte[] pattern) {
    String key = message.toString();
    log.info("Expired key: {}", key);

    RedisMessageHandler handler = null;
    if(key.startsWith(ProductBidPriceMaxRepository.KEY_PREFIX)){
      handler = this.applicationContext.getBean(
          "bidPriceMaxKeyExpirationHandler", RedisMessageHandler.class);
    }

    if(handler != null){
      handler.handle(key);
    }
  }


}

