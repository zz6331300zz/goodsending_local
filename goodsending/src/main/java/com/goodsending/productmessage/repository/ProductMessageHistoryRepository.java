package com.goodsending.productmessage.repository;

import com.goodsending.productmessage.entity.ProductMessageHistory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Date : 2024. 08. 07.
 * @Team : GoodsEnding
 * @author : jieun(je-pa)
 * @Project : goodsending-be :: goodsending
 */
public interface ProductMessageHistoryRepository extends
    JpaRepository<ProductMessageHistory, Long>, ProductMessageHistoryQueryDslRepository {

}
