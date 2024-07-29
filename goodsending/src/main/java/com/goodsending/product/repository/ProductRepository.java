package com.goodsending.product.repository;

import com.goodsending.member.entity.Member;
import com.goodsending.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

  @Query("SELECT p "
      + "FROM Like l "
      + "INNER JOIN Product p ON l.product.id = p.id "
      + "WHERE l.member = :member")
  Page<Product> findLikeProductByMember(Member member, Pageable pageable);

}
