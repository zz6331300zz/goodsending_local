package com.goodsending.productlike.repository;
import com.goodsending.global.redis.RedisRankingRepository;
import com.goodsending.productlike.dto.ProductRankingDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LikeCountRankingRepository extends RedisRankingRepository<String, ProductRankingDto> {
  private static final String PREFIX = "product:likeCount:";

  public LikeCountRankingRepository(RedisTemplate<String, ProductRankingDto> redisTemplate) {
    super(PREFIX, redisTemplate);
  }

}

