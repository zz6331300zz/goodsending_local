package com.goodsending.charge.repository;

import com.goodsending.charge.entity.ChargeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChargeHistoryRepository extends JpaRepository<ChargeHistory, Long> {

}
