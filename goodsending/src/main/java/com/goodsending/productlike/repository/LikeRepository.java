package com.goodsending.productlike.repository;

import com.goodsending.member.entity.Member;
import com.goodsending.product.entity.Product;
import com.goodsending.productlike.entity.Like;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

  Optional<Like> findLikeByMemberAndProduct(Member member, Product product);
  Long countByProduct(Product product);
  boolean existsByMemberAndProduct(Member member, Product product);
}
