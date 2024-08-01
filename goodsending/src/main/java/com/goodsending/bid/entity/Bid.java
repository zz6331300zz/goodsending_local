package com.goodsending.bid.entity;

import com.goodsending.bid.dto.request.BidRequest;
import com.goodsending.bid.type.BidStatus;
import com.goodsending.global.entity.BaseEntity;
import com.goodsending.member.entity.Member;
import com.goodsending.product.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Date : 2024. 07. 25.
 * @Team : GoodsEnding
 * @author : jieun
 * @Project : goodsending-be :: goodsending
 */
@Entity
@Table(name = "bids",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"price", "member_id", "product_id"})}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bid extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "bid_id")
  private Long id;

  @Column(name = "price", nullable = false)
  private Integer price;

  @Column(name = "use_point", nullable = true)
  private Integer usePoint;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = true)
  private BidStatus status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Version
  private Long version;

  @Builder
  private Bid(Member member, Integer price, Product product, Integer usePoint) {
    this.member = member;
    this.price = price;
    this.product = product;
    this.usePoint = usePoint;
  }

  public static Bid of(Member member, Product product, BidRequest request) {
    return Bid.builder()
        .member(member)
        .product(product)
        .price(request.bidPrice())
        .usePoint(request.usePoint())
        .build();
  }

}