package com.goodsending.bid.repository;

import com.goodsending.global.redis.RedisRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * @Date : 2024. 07. 26.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
@Repository
public class ProductBidPriceMaxRepository extends RedisRepository<Long, Integer> {
  private final static String KEY_PREFIX = "PRODUCT_BID_PRICE_MAX:";

  public ProductBidPriceMaxRepository(RedisTemplate<String, Integer> redisTemplate) {
    super(KEY_PREFIX, redisTemplate);
  }

  public boolean isBidPriceMaxGreaterOrEqualsThan(Long key, Integer amount){
    return super.getValueByKey(key) >= amount;
  }
}
