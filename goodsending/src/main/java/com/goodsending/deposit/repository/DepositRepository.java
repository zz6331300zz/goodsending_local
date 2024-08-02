package com.goodsending.deposit.repository;

import com.goodsending.deposit.entity.Deposit;
import com.goodsending.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositRepository extends JpaRepository<Deposit, Long> {

  Deposit findByProduct(Product product);
}
