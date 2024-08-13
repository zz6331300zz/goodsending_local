package com.goodsending.product.dto.request;

import com.goodsending.product.type.ProductStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProductSearchCondition {

  // 상품 등록 회원 아이디
  private Long memberId;

  // 구매 가능한 매물 선택 여부
  private boolean openProduct;

  // 마감된 매물 선택 여부
  private boolean closedProduct;

  // 검색어
  private String keyword;

  // 사용자에게 응답해준 마지막 데이터의 상태
  private ProductStatus cursorStatus;

  // 사용자에게 응답해준 마지막 데이터의 경매 시작 시각
  private LocalDateTime cursorStartDateTime;

  // 사용자에게 응답해준 마지막 데이터의 식별자값
  private Long cursorId;

  public static ProductSearchCondition of(Long memberId, boolean openProduct, boolean closedProduct,
      String keyword, ProductStatus cursorStatus, LocalDateTime cursorStartDateTime,
      Long cursorId) {
    return ProductSearchCondition.builder()
        .memberId(memberId)
        .openProduct(openProduct)
        .closedProduct(closedProduct)
        .keyword(keyword)
        .cursorStatus(cursorStatus)
        .cursorStartDateTime(cursorStartDateTime)
        .cursorId(cursorId)
        .build();
  }

}
