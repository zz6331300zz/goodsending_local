package com.goodsending.order.entity;

import com.goodsending.bid.entity.Bid;
import com.goodsending.global.entity.BaseEntity;
import com.goodsending.order.dto.request.ReceiverInfoRequest;
import com.goodsending.order.type.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Date : 2024. 07. 30.
 * @Team : GoodsEnding
 * @author : jieun(je-pa)
 * @Project : goodsending-be :: goodsending
 */
@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

  @Id
  private Long id;

  @Column(name = "receiver_cell_number", nullable = true)
  private String receiverCellNumber;

  @Column(name = "receiver_name", nullable = true)
  private String receiverName;

  @Column(name = "receiver_address", nullable = true)
  private String receiverAddress;

  @Column(name = "delivery_date_time", nullable = true)
  private LocalDateTime deliveryDateTime;

  @Column(name = "confirmed_date_time", nullable = true)
  private LocalDateTime confirmedDateTime;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = true)
  private OrderStatus status;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  private Bid bid;

  private Order(Bid bid) {
    this.bid = bid;
  }

  public static Order from(Bid bid) {
    return new Order(bid);
  }

  public boolean isReceiverId(Long memberId){
    return this.bid.getMember().getMemberId().equals(memberId);
  }

  public Order updateReceiverInfo(ReceiverInfoRequest request){
    this.receiverAddress = request.receiverAddress();
    this.receiverName = request.receiverName();
    this.receiverCellNumber = request.receiverCellNumber();
    this.status = OrderStatus.PENDING;
    return this;
  }
}
