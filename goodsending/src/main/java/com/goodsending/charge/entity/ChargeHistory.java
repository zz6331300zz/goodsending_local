package com.goodsending.charge.entity;

import com.goodsending.order.entity.Order;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Date : 2024. 08. 05.
 * @Team : GoodsEnding
 * @author : jieun(je-pa)
 * @Project : goodsending-be :: goodsending
 */
@Entity
@Getter
@Table(name = "charge_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChargeHistory {

  @Id
  private Long id;

  @Column(name = "price", nullable = false)
  private int price;

  @Column(name = "accumulated_price", nullable = false)
  private int accumulatedPrice;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "charge_id")
  private Order order;

  @Builder
  private ChargeHistory(Order order, int price, int accumulatedPrice) {
    this.order = order;
    this.price = price;
    this.accumulatedPrice = accumulatedPrice;
  }

  public static ChargeHistory of(Order order, int price, int accumulatedPrice) {
    return ChargeHistory.builder()
        .order(order)
        .price(price)
        .accumulatedPrice(accumulatedPrice)
        .build();
  }
}