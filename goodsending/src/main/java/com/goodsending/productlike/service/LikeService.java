package com.goodsending.productlike.service;

import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.member.entity.Member;
import com.goodsending.member.repository.MemberRepository;
import com.goodsending.product.dto.response.ProductCreateResponseDto;
import com.goodsending.product.entity.Product;
import com.goodsending.product.repository.ProductRepository;
import com.goodsending.productlike.dto.LikeRequestDto;
import com.goodsending.productlike.dto.LikeResponseDto;
import com.goodsending.productlike.entity.Like;
import com.goodsending.productlike.repository.LikeRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

  public Page<ProductCreateResponseDto> getLikeProductsPage(Long memberId, int page, int size,
      String sortBy, boolean isAsc) {
    Member member = findMemberById(memberId);
    Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
    Sort sort = Sort.by(direction, sortBy);
    Pageable pageable = PageRequest.of(page, size, sort);

    Page<Product> productList = productRepository.findLikeProductByMember(member, pageable);
    return productList.map(ProductCreateResponseDto::from);
  }

  public List<ProductCreateResponseDto> getTop5LikeProduct(LocalDateTime dateTime) {
    return productRepository.findTop5ByStartDateTimeAfterOrderByLikeCountDesc(dateTime).stream()
        .map(ProductCreateResponseDto::from).toList();
  }
}
