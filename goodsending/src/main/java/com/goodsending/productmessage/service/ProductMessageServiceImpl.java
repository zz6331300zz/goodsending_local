package com.goodsending.productmessage.service;

import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.global.websocket.dto.ProductMessageDto;
import com.goodsending.member.entity.Member;
import com.goodsending.member.repository.MemberRepository;
import com.goodsending.product.entity.Product;
import com.goodsending.product.repository.ProductRepository;
import com.goodsending.productmessage.dto.request.ProductMessageListRequest;
import com.goodsending.productmessage.dto.request.ProductMessageRequest;
import com.goodsending.productmessage.dto.response.ProductMessageResponse;
import com.goodsending.productmessage.entity.ProductMessageHistory;
import com.goodsending.productmessage.event.CreateProductMessageEvent;
import com.goodsending.productmessage.repository.ProductMessageHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

/**
 * @Date : 2024. 08. 07.
 * @Team : GoodsEnding
 * @author : jieun(je-pa)
 * @Project : goodsending-be :: goodsending
 */
@Service
@RequiredArgsConstructor
public class ProductMessageServiceImpl implements ProductMessageService{
  private final ProductMessageHistoryRepository productMessageHistoryRepository;
  private final ProductRepository productRepository;
  private final MemberRepository memberRepository;

  /**
   * 입찰, 낙찰 시 발행된 이벤트로 메시지 내역이 저장됩니다.
   * @param event
   * @return
   */
  @Override
  public ProductMessageDto create(CreateProductMessageEvent event) {
    Member member = findMemberById(event.memberId());
    Product product = findProduct(event.productId());

    ProductMessageHistory history = productMessageHistoryRepository.save(
        ProductMessageHistory.of(
            member, product, event.type(), event.getMessageBy(member.getEmail())));

    return ProductMessageDto.of(history, event.price());
  }


  /**
   * 채팅으로 메시지 내역이 저장됩니다.
   * @param request 채팅 내용, memberId, userId
   * @return
   */
  @Override
  public ProductMessageDto create(ProductMessageRequest request) {
    Member member = findMemberByEmail(request.memberEmail());
    Product product = findProduct(request.productId());

    ProductMessageHistory history = productMessageHistoryRepository.save(
        ProductMessageHistory.of(
            member, product, request.type(), request.message()));

    return ProductMessageDto.of(history);
  }

  /**
   * 커서 기반 페이징으로 상품별 메시지 내역 리스트를 조회합니다.
   * @param request 커서기반 페이징을 위한 request
   * @return 커서 기반으로 페이징한 리스트
   * @author jieun(je-pa)
   */
  @Override
  public Slice<ProductMessageResponse> read(ProductMessageListRequest request) {
    return productMessageHistoryRepository.findByProductId(request);
  }

  private Member findMemberByEmail(String email) {
    return memberRepository.findByEmail(email)
        .orElseThrow(() -> CustomException.from(ExceptionCode.USER_NOT_FOUND));
  }

  private Member findMemberById(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> CustomException.from(ExceptionCode.USER_NOT_FOUND));
  }

  private Product findProduct(Long productId) {
    return productRepository.findById(productId)
        .orElseThrow(() -> CustomException.from(ExceptionCode.PRODUCT_NOT_FOUND));
  }
}
