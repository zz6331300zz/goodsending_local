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
import java.util.List;
import java.util.Optional;
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
    boolean isClicked = likeRequestDto.isClicked();
    Optional<Like> like = likeRepository.findLikeByMemberAndProduct(member,
        product);
    boolean existLike = like.isPresent();

    if (existLike && isClicked) {
        deleteLike(product, like);
        countLike(product);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
      }

    if (!existLike && !isClicked){

      addLike(product, member);
      countLike(product);
      return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    if(!existLike){
      throw CustomException.from(ExceptionCode.USER_NOT_FOUND);
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
  }

  private void addLike(Product product, Member member) {
    likeRepository.save(new Like(product, member));
  }

  @Transactional
  public void countLike(Product product) {
    Long likeCount = likeRepository.countByProduct(product);
    product.setLikeCount(likeCount);
  }

  private void deleteLike(Product product, Optional<Like> like) {
    if (product.getLikeCount() == null || product.getLikeCount() <= 0) {
      throw CustomException.from(ExceptionCode.CANNOT_DECREASE_LIKE_COUNT);
    }
    likeRepository.delete(like.get());
  }

  private Product findProductById(Long productId) {
    return productRepository.findById(productId)
        .orElseThrow(() -> CustomException.from(ExceptionCode.PRODUCT_NOT_FOUND));

  }

  private Member findMemberById(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> CustomException.from(ExceptionCode.MEMBER_NOT_FOUND));

  }

}
