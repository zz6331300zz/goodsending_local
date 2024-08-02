package com.goodsending.product.entity;

import com.goodsending.global.entity.BaseEntity;
import com.goodsending.member.entity.Member;
import com.goodsending.product.dto.request.ProductCreateRequestDto;
import com.goodsending.product.dto.request.ProductUpdateRequestDto;
import com.goodsending.product.type.AuctionTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE products SET deleted_date_time = NOW() WHERE product_id = ? and version = ?")
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

  @Column(name = "start_date_time", nullable = false)
  private LocalDateTime startDateTime;

  @Column(name = "max_end_date_time", nullable = true)
  private LocalDateTime maxEndDateTime;

  // dynamicEndDateTime은 낙찰자가 정해졌을 때 입력되는 값이다.
  @Setter
  @Column(name = "dynamic_end_date_time")
  private LocalDateTime dynamicEndDateTime;

  @Column(name = "bidding_count", nullable = false)
  private int biddingCount;

  @Column(name = "like_count", nullable = true)
  private Long likeCount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @Version
  private Long version;

  @Column(name = "deleted_date_time", nullable = true)
  private LocalDateTime deletedDateTime;

  @Builder
  public Product(Long id, String name, int price, String introduction, LocalDateTime startDateTime,
      LocalDateTime maxEndDateTime, LocalDateTime dynamicEndDateTime, int biddingCount,
      Member member) {
    this.name = name;
    this.price = price;
    this.introduction = introduction;
    this.startDateTime = startDateTime;
    this.maxEndDateTime = maxEndDateTime;
    this.member = member;
  }

  public static Product of(ProductCreateRequestDto requestDto, Member member) {

    LocalDate startDate = requestDto.getStartDate();
    AuctionTime auctionTime = requestDto.getAuctionTime();

    LocalDateTime startDateTime = startDate.atTime(auctionTime.getStartTime());
    LocalDateTime maxEndDateTime = startDate.atTime(auctionTime.getEndTime());

    return Product.builder()
        .name(requestDto.getName())
        .price(requestDto.getPrice())
        .introduction(requestDto.getIntroduction())
        .startDateTime(startDateTime)
        .maxEndDateTime(maxEndDateTime)
        .member(member)
        .build();
  }

  public void update(ProductUpdateRequestDto requestDto) {
    LocalDate startDate = requestDto.getStartDate();
    AuctionTime auctionTime = requestDto.getAuctionTime();

    LocalDateTime startDateTime = startDate.atTime(auctionTime.getStartTime());
    LocalDateTime maxEndDateTime = startDate.atTime(auctionTime.getEndTime());

    this.name = requestDto.getName();
    this.introduction = requestDto.getIntroduction();
    this.startDateTime = startDateTime;
    this.maxEndDateTime = maxEndDateTime;
  }

  public void setLikeCount(Long likeCount) {
    this.likeCount = likeCount;
  }


  public boolean isPriceGreaterOrEqualsThan(Integer amount) {
    if (amount == null) {
      return false;
    }
    return this.price >= amount;
  }

  public void setBiddingCount(Long biddingCount) {
    if(biddingCount == null) {
      this.biddingCount = 0;
      return;
    }
    this.biddingCount = (int)biddingCount.longValue();
  }
}
