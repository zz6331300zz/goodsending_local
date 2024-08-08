package com.goodsending.productmessage.entity;

import com.goodsending.member.entity.Member;
import com.goodsending.product.entity.Product;
import com.goodsending.productmessage.type.MessageType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * @Date : 2024. 08. 07.
 * @Team : GoodsEnding
 * @author : jieun(je-pa)
 * @Project : goodsending-be :: goodsending
 */
@Entity
@Table(name = "product_message_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ProductMessageHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;

  @Column(name = "message", nullable = false)
  private String message;

  @Column(name = "status", nullable = false)
  @Enumerated(value = EnumType.STRING)
  private MessageType type;

  @Column(nullable = false, updatable = false)
  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime createdDateTime;

  @Builder
  private ProductMessageHistory(Member member, Product product, MessageType type, String message) {
    this.member = member;
    this.product = product;
    this.type = type;
    this.message = message;
  }

  public static ProductMessageHistory of(
      Member member, Product product, MessageType type,String message){
    return ProductMessageHistory.builder()
        .member(member)
        .product(product)
        .type(type)
        .message(message)
        .build();
  }

}
