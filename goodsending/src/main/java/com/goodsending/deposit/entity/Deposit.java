package com.goodsending.deposit.entity;

import com.goodsending.deposit.type.DepositStatus;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "deposit_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Deposit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "deposit_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;

  @Column(name = "price", nullable = true)
  private int price;

  @Column(name = "total_price", nullable = true)
  private int totalPrice;

  @Setter
  @Column(name = "status", nullable = false)
  @Enumerated(value = EnumType.STRING)
  private DepositStatus status;

  @Builder
  public Deposit(Product product, Member member, int price, int totalPrice, DepositStatus status) {
    this.product = product;
    this.member = member;
    this.price = price;
    this.totalPrice = totalPrice;
    this.status = status;
  }

  public static Deposit of(Product savedProduct, Member member, Integer depositPrice) {
    return Deposit.builder()
        .member(member)
        .product(savedProduct)
        .price(depositPrice)
        .totalPrice(savedProduct.getPrice())
        .status(DepositStatus.UNRETURNED)
        .build();
  }
}