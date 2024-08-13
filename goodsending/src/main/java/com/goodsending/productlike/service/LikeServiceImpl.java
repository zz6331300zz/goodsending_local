package com.goodsending.productlike.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import com.goodsending.productlike.dto.ProductRankingDto;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

  private final LikeRepository likeRepository;
  private final MemberRepository memberRepository;
  private final ProductRepository productRepository;
  private final ProductImageRepository productImageRepository;
  private final LikeCountRankingRepository likeCountRankingRepository;
  private final ObjectMapper objectMapper;
  private final RedisTemplate<String, ProductRankingDto> redisTemplate;

  /**
   * 찜하기 토글 기능
   * <p>
   * 회원Id, 상품정보, 클릭여부를 받아 찜하기 또는 찜하기 취소한다.
   *
   * @param memberId       회원Id
   * @param likeRequestDto 상품정보, 클릭여부
   * @return 상태코드
   * @author zz6331300zz
   */

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

  /**
   * 찜한 상품 목록 조회 기능
   * <p>
   * 찜한 상품의 목록을 페이지 정보와 함께 조회합니다
   *
   * @param memberId 회원 Id
   * @param page     조회할 페이지 번호
   * @param size     한 페이지에 보여줄 상품 개수
   * @param sortBy   정렬 항목(id, title, price...)
   * @param isAsc    오름차순(true:오름차순 false:내림차순)
   * @return 찜한 상품 목록 정보 : number:조회된 페이지 번호(0부터 시작), content:조회된 상품정보, size: 한 페이지에 보여줄 상품개수,
   * numberOfElements:전체 상품 개수(회원이 등록한 모든 상품의 개수), totalPages: 전체 페이지 수
   * @author : zz6331300zz
   */


  public Page<ProductlikeCountDto> getLikeProductsPage(Long memberId, int page, int size,
      String sortBy, boolean isAsc) {
    Member member = findMemberById(memberId);
    Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
    Sort sort = Sort.by(direction, sortBy);
    Pageable pageable = PageRequest.of(page, size, sort);

    Page<Product> productList = productRepository.findLikeProductByMember(member, pageable);
    return productList.map(ProductlikeCountDto::from);
  }


  /**
   * 찜하기 수 Top 5 상품 조회 기능(queryDSL 사용)
   *
   * 미래에 진행되는 찜하기 Top 5 상품을 queryDSL을 사용해서 조회합니다
   *
   * @param dateTime 현재 시간을 시작시간과 비교하기 위해 LocalDateTime으로 선언
   * @return 정렬된 Top5 찜하기 상품 목록
   * @author : zz6331300zz
   */

  public List<ProductlikeCountDto> getTop5LikeProduct(LocalDateTime dateTime) {
    return productRepository.findTop5ByStartDateTimeAfterOrderByLikeCountDesc(dateTime).stream()
        .map(ProductlikeCountDto::from).toList();
  }

  /**
   * 찜하기 상품 등록 기능(Redis 사용)
   *
   * 상품 찜을 Redis을 사용해서 등록합니다
   *
   * @param memberId   회원 Id
   * @param requestDto 상품Id, 클릭여부
   * @return 찜한 상품의 등록정보(상태코드)
   * @author : zz6331300zz
   */

  @Transactional
  public ResponseEntity<LikeResponseDto> toggleLikeRedis(Long memberId, LikeRequestDto requestDto) {
    Member member = findMemberById(memberId);
    Product product = findProductById(requestDto.getProductId());
    ProductImage productImage = productImageRepository.findFirstByProduct(product);
    ProductRankingDto productRankingDto = new ProductRankingDto(product.getId(), product.getName(),
        product.getPrice(), product.getStartDateTime(), product.getMaxEndDateTime(),
        product.getStatus(),
        productImage.getUrl());
    boolean likeButton = requestDto.isPress();
    Like like = null;
    boolean existingLike = likeRepository.existsByMemberAndProduct(member, product);

    if (likeButton) {
      if (!existingLike) {
        like = new Like(product, member);
        likeRepository.save(like);
        countLike(product);
        likeCountRankingRepository.setZSetValue("ranking", productRankingDto,
            product.getLikeCount());
      } else {
        likeCountRankingRepository.setZSetValue("ranking", productRankingDto,
            product.getLikeCount());
      }
      return ResponseEntity.status(HttpStatus.CREATED).build();
    } else {
      like = likeRepository.findLikeByMemberAndProduct(member,
          product).orElseThrow(() -> CustomException.from(ExceptionCode.MEMBER_NOT_FOUND));
      likeRepository.delete(like);
      countLike(product);

      likeCountRankingRepository.setZSetValue("ranking", productRankingDto,
          product.getLikeCount());

      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
  }

  /**
   * 찜하기 수 Top 5 상품 조회 기능(Redis 사용)
   *
   * 미래에 진행되는 찜하기 Top 5 상품을 Redis을 사용해서 조회합니다
   *
   * @return 정렬된 Top5 찜하기 상품 목록
   * @author : zz6331300zz
   */

  public List<ProductLikeWithScore> readTop5LikeProduct() {
    Set<TypedTuple<ProductRankingDto>> allProducts = likeCountRankingRepository.getZSetTupleByKey(
        "ranking", 0, -1);

    if (allProducts != null) {
      return allProducts.stream()
          .sorted(
              (p1, p2) -> Double.compare(p2.getScore(), p1.getScore())) // Sort in descending order
          .limit(5)
          .map(tuple -> {
            Object value = tuple.getValue();
            ProductRankingDto dto = convertMapToDto(
                (Map<String, Object>) value); // Convert Map to DTO
            if ("UPCOMING".equals(dto.getStatus().toString())) {
              return new ProductLikeWithScore(dto, tuple.getScore());
            } else {
              return null;
            }
          })
          .filter(dtoWithScore -> dtoWithScore != null)
          .collect(Collectors.toList());
    }

    return Collections.emptyList();

  }

  public ProductRankingDto convertMapToDto(Map<String, Object> map) {
    try {
      return objectMapper.convertValue(map, ProductRankingDto.class);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Error converting map to ProductLikeDto", e);
    }
  }

  @Override
  public void deleteTop5Likes() {
    likeCountRankingRepository.deleteZSetValue("ranking");
  }

  @Override
  public void deleteLikeFromZSet(ProductRankingDto rankingDto) {
    {
      ZSetOperations<String, ProductRankingDto> zSetOperations = redisTemplate.opsForZSet();
      // DTO를 직렬화하여 zset의 멤버로 사용
      String serializedMember = null;
      try {
        serializedMember = objectMapper.writeValueAsString(rankingDto);
      } catch (JsonProcessingException e) {
        throw CustomException.from(ExceptionCode.LIKE_NOT_FOUND);
      }
      zSetOperations.remove("ranking", serializedMember);
    }
  }
}
