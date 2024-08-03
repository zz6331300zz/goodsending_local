package com.goodsending.member.repository;

import com.goodsending.global.redis.RedisRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SaveMailAndCodeRepository extends RedisRepository<String, String> {
  private static final String PREFIX = "email:code:";

  public SaveMailAndCodeRepository(RedisTemplate<String, String> redisTemplate) {
    super(PREFIX, redisTemplate);
  }
}
