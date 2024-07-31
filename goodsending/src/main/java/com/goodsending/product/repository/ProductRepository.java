package com.goodsending.product.repository;

import com.goodsending.member.entity.Member;
import com.goodsending.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductCustomRepository {

  @Query("SELECT p "
      + "FROM Like l "
      + "INNER JOIN Product p ON l.product.id = p.id "
      + "WHERE l.member = :member")
  Page<Product> findLikeProductByMember(Member member, Pageable pageable);

  @Lock(value = LockModeType.OPTIMISTIC)
  @Query("select p from Product p where  p.id = :id")
  Optional<Product> findByIdWithOptimisticLock(Long id);
}
