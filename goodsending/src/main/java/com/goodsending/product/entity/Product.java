package com.goodsending.product.entity;

import com.goodsending.global.entity.BaseEntity;
import com.goodsending.member.entity.Member;
import com.goodsending.product.dto.request.ProductCreateRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "product_id")
  private Long id;

  @Column(name = "name", nullable = false, length = 20)
  private String name;

  @Column(name = "price", nullable = false)
  private int price;

  @Column(name = "introduction", nullable = false)
  private String introduction;

  @Column(name = "auction_end_date", nullable = true)
  private LocalDateTime auctionEndDate;

  @Column(name = "bidding_count", nullable = false)
  private int biddingCount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @Builder
  public Product(String name, int price, String introduction, LocalDateTime auctionEndDate,
      Member member) {
    this.name = name;
    this.price = price;
    this.introduction = introduction;
    this.auctionEndDate = auctionEndDate;
    this.member = member;
  }

  public static Product of(ProductCreateRequestDto requestDto, LocalDateTime currentTime, Member member) {

    int auctionPeriodDays = requestDto.getAuctionPeriodDays();
    LocalDateTime auctionEndDate = currentTime.plusDays(auctionPeriodDays)
        .withHour(23)
        .withMinute(59)
        .withSecond(59)
        .withNano(0);

    return Product.builder()
        .name(requestDto.getName())
        .price(requestDto.getPrice())
        .introduction(requestDto.getIntroduction())
        .auctionEndDate(auctionEndDate)
        .member(member)
        .build();
  }
}
