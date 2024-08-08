package com.goodsending.member.repository;

import com.goodsending.global.redis.RedisRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BlackListAccessTokenRepository extends RedisRepository<String, String> {
  private static final String PREFIX = "blacklist:";
  public BlackListAccessTokenRepository(RedisTemplate<String, String> redisTemplate) {
    super(PREFIX, redisTemplate);
  }
}
