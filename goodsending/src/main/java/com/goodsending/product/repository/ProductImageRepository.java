package com.goodsending.product.repository;

import com.goodsending.product.entity.Product;
import com.goodsending.product.entity.ProductImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
  List<ProductImage> findAllByProduct(Product product);
}
