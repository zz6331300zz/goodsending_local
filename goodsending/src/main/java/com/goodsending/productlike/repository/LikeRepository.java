package com.goodsending.productlike.repository;

import com.goodsending.member.entity.Member;
import com.goodsending.product.entity.Product;
import com.goodsending.productlike.entity.Like;
import com.goodsending.productlike.entity.ProductLikeDto;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

  Optional<Like> findLikeByMemberAndProduct(Member member, Product product);
  Long countByProduct(Product product);
  boolean existsByMemberAndProduct(Member member, Product product);
}
