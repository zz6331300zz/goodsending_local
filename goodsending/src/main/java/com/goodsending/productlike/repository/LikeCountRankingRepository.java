package com.goodsending.productlike.repository;
import com.goodsending.global.redis.RedisRankingRepository;
import com.goodsending.productlike.entity.ProductLikeDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LikeCountRankingRepository extends RedisRankingRepository<String, ProductLikeDto> {
  private static final String PREFIX = "product:likeCount:";

  public LikeCountRankingRepository(RedisTemplate<String, ProductLikeDto> redisTemplate) {
    super(PREFIX, redisTemplate);
  }

}

