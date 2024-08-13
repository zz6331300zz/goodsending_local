package com.goodsending.global.redis;

import com.goodsending.productlike.dto.ProductRankingDto;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

@RequiredArgsConstructor
public abstract class RedisRankingRepository<K, V> {

  private final String PREFIX;
  private final RedisTemplate<String, V> redisTemplate;

  public void setZSetValue(K key, V value, double score) {
    redisTemplate.opsForZSet()
        .add(PREFIX + key, value, score);
  }

  public Set<TypedTuple<V>> getZSetTupleByKey(K key, long start, long end) {
    return redisTemplate.opsForZSet().rangeWithScores(PREFIX + key, start, end);
  }

  public Set<TypedTuple<V>> getReverseZSetTupleByKey(K key, long start, long end) {
    return redisTemplate.opsForZSet().reverseRangeWithScores(PREFIX + key, start, end);
  }

  public boolean isExistInRedis(String key, V value) {
    Double score = redisTemplate.opsForZSet().score(PREFIX + key, value);
    return score != null;
  }

  public void increaseScore(K key, V value, int delta) {
    redisTemplate.opsForZSet().incrementScore(PREFIX + key, value, delta);
  }

  public void deleteZSetKey(K key) {
    redisTemplate.delete(PREFIX + key);
  }

  public void deleteZSetValue(K key) {
    Long size = redisTemplate.opsForZSet().size(PREFIX + key);
    if (size != null && size > 0) {
      redisTemplate.opsForZSet().removeRange(PREFIX + key, 0, size - 1);
    }
  }
  public void deleteLikeFromZSet(K key, ProductRankingDto rankingDto) {
    redisTemplate.opsForZSet().remove(PREFIX + key, rankingDto);
  }
}
