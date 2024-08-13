package com.goodsending.productlike.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.goodsending.product.dto.response.ProductlikeCountDto;
import com.goodsending.productlike.dto.LikeRequestDto;
import com.goodsending.productlike.dto.LikeResponseDto;
import com.goodsending.productlike.dto.ProductRankingDto;
import com.goodsending.productlike.entity.ProductLikeWithScore;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface LikeService{
  ResponseEntity<LikeResponseDto> toggleLike(Long memberId, LikeRequestDto likeRequestDto);

  Page<ProductlikeCountDto> getLikeProductsPage(Long memberId, int page, int size,
      String sortBy, boolean isAsc);

  List<ProductlikeCountDto> getTop5LikeProduct(LocalDateTime dateTime);

  ResponseEntity<LikeResponseDto> toggleLikeRedis(Long memberId, LikeRequestDto requestDto);

  List<ProductLikeWithScore> readTop5LikeProduct();

  ProductRankingDto convertMapToDto(Map<String, Object> map);

  void deleteTop5Likes();

  void deleteLikeFromZSet(ProductRankingDto rankingDto);
}
