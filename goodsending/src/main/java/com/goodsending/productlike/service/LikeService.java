package com.goodsending.productlike.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.member.entity.Member;
import com.goodsending.member.repository.MemberRepository;
import com.goodsending.product.dto.response.ProductlikeCountDto;
import com.goodsending.product.entity.Product;
import com.goodsending.product.entity.ProductImage;
import com.goodsending.product.repository.ProductImageRepository;
import com.goodsending.product.repository.ProductRepository;
import com.goodsending.productlike.dto.LikeRequestDto;
import com.goodsending.productlike.dto.LikeResponseDto;
import com.goodsending.productlike.entity.Like;
import com.goodsending.productlike.entity.ProductLikeDto;
import com.goodsending.productlike.entity.ProductLikeWithScore;
import com.goodsending.productlike.repository.LikeCountRankingRepository;
import com.goodsending.productlike.repository.LikeRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class LikeService {

  private final LikeRepository likeRepository;
  private final MemberRepository memberRepository;
  private final ProductRepository productRepository;
  private final ProductImageRepository productImageRepository;
  private final LikeCountRankingRepository likeCountRankingRepository;
  private final ObjectMapper objectMapper;

  @Transactional
  public ResponseEntity<LikeResponseDto> toggleLike(Long memberId, LikeRequestDto likeRequestDto) {

    Member member = findMemberById(memberId);
    Product product = findProductById(likeRequestDto.getProductId());
    boolean likeButton = likeRequestDto.isPress();
    Like like = null;
    boolean existingLike = likeRepository.existsByMemberAndProduct(member, product);

    if (likeButton) {
      if (!existingLike) {
        like = new Like(product, member);
        likeRepository.save(like);
        countLike(product);
        return ResponseEntity.status(HttpStatus.CREATED).build();
      }
    } else {
      like = likeRepository.findLikeByMemberAndProduct(member,
          product).orElseThrow(() -> CustomException.from(ExceptionCode.MEMBER_NOT_FOUND));
      likeRepository.delete(like);
      countLike(product);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
  }

  private void countLike(Product product) {
    Long likeCount = likeRepository.countByProduct(product);
    product.setLikeCount(likeCount);

  }

  private Product findProductById(Long productId) {
    return productRepository.findById(productId)
        .orElseThrow(() -> CustomException.from(ExceptionCode.PRODUCT_NOT_FOUND));

  }

  private Member findMemberById(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> CustomException.from(ExceptionCode.MEMBER_NOT_FOUND));

  }

  public Page<ProductlikeCountDto> getLikeProductsPage(Long memberId, int page, int size,
      String sortBy, boolean isAsc) {
    Member member = findMemberById(memberId);
    Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
    Sort sort = Sort.by(direction, sortBy);
    Pageable pageable = PageRequest.of(page, size, sort);

    Page<Product> productList = productRepository.findLikeProductByMember(member, pageable);
    return productList.map(ProductlikeCountDto::from);
  }

  public List<ProductlikeCountDto> getTop5LikeProduct(LocalDateTime dateTime) {
    return productRepository.findTop5ByStartDateTimeAfterOrderByLikeCountDesc(dateTime).stream()
        .map(ProductlikeCountDto::from).toList();
  }

  @Transactional
  public ResponseEntity<LikeResponseDto> toggleLikeRedis(Long memberId, LikeRequestDto requestDto) {
    Member member = findMemberById(memberId);
    Product product = findProductById(requestDto.getProductId());
    ProductImage productImage = productImageRepository.findFirstByProduct(product);
    ProductLikeDto productLikeDto = new ProductLikeDto(product.getName(), product.getStartDateTime(), product.getMaxEndDateTime(), product.getPrice(),
        productImage.getUrl());
    boolean likeButton = requestDto.isPress();
    Like like = null;
    boolean existingLike = likeRepository.existsByMemberAndProduct(member, product);

    if (likeButton) {
      if (!existingLike) {
        like = new Like(product, member);
        likeRepository.save(like);
        countLike(product);
        likeCountRankingRepository.setZSetValue("ranking", productLikeDto,
            product.getLikeCount());


        return ResponseEntity.status(HttpStatus.CREATED).build();
      }
    } else {
      like = likeRepository.findLikeByMemberAndProduct(member,
          product).orElseThrow(() -> CustomException.from(ExceptionCode.MEMBER_NOT_FOUND));
      likeRepository.delete(like);
      countLike(product);

      likeCountRankingRepository.setZSetValue("ranking", productLikeDto,
          product.getLikeCount());

      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
  }

  public List<ProductLikeWithScore> read() {
    Set<TypedTuple<ProductLikeDto>> allProducts = likeCountRankingRepository.getZSetTupleByKey(
        "ranking", 0, -1);

    if (allProducts != null) {
      return allProducts.stream()
          .sorted(
              (p1, p2) -> Double.compare(p2.getScore(), p1.getScore())) // Sort in descending order
          .limit(5)
          .map(tuple -> {
            Object value = tuple.getValue();
            ProductLikeDto dto = convertMapToDto((Map<String, Object>) value); // Convert Map to DTO
            return new ProductLikeWithScore(dto, tuple.getScore());
          })
          .collect(Collectors.toList());
    }

    return Collections.emptyList();

  }

  private ProductLikeDto convertMapToDto(Map<String, Object> map) {
    try {
      return objectMapper.convertValue(map, ProductLikeDto.class);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Error converting map to ProductLikeDto", e);
    }
  }


  public void delete() {
    likeCountRankingRepository.deleteZSetValue("ranking");
    likeCountRankingRepository.deleteZSetValue("product_start_date_time");
  }
}
