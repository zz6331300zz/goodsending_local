package com.goodsending.productmessage.repository;

import com.goodsending.productmessage.dto.request.ProductMessageListRequest;
import com.goodsending.productmessage.dto.response.ProductMessageResponse;
import org.springframework.data.domain.Slice;

/**
 * @Date : 2024. 08. 08.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
public interface ProductMessageHistoryQueryDslRepository {

  Slice<ProductMessageResponse> findByProductId(ProductMessageListRequest request);
}
