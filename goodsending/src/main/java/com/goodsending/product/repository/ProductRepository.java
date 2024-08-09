package com.goodsending.product.repository;

import com.goodsending.member.entity.Member;
import com.goodsending.product.entity.Product;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductCustomRepository {

  @Query("SELECT p "
      + "FROM Product p "
      + "JOIN Like l ON l.product = p "
      + "WHERE l.member = :member")
  Page<Product> findLikeProductByMember(Member member, Pageable pageable);

  @Lock(value = LockModeType.OPTIMISTIC)
  @Query("select p from Product p where  p.id = :id")
  Optional<Product> findByIdWithOptimisticLock(Long id);

  List<Product> findTop5ByStartDateTimeAfterOrderByLikeCountDesc(
      @Param("currentDateTime") LocalDateTime currentDateTime);
}
