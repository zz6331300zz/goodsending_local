package com.goodsending.productlike.repository;
import com.goodsending.global.redis.RedisRankingRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository

public class LikeCountRankingRankingRepository extends RedisRankingRepository<String, String> {
  private static final String PREFIX = "product:likeCount:";

  public LikeCountRankingRankingRepository(RedisTemplate<String, String> redisTemplate) {
    super(PREFIX, redisTemplate);
  }

}

