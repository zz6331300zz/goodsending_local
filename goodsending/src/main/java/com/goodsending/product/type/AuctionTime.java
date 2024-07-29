package com.goodsending.product.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.LocalTime;

public enum AuctionTime {
  AFTERNOON(LocalTime.of(12, 0, 0), LocalTime.of(14, 59, 59)),
  EVENING(LocalTime.of(18, 0, 0), LocalTime.of(20, 59, 59));

  private final LocalTime startTime;
  private final LocalTime endTime;

  AuctionTime(LocalTime startTime, LocalTime endTime) {
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public LocalTime getStartTime() {
    return startTime;
  }

  public LocalTime getEndTime() {
    return endTime;
  }

  @JsonCreator
  public static AuctionTime from(String value) {
    for (AuctionTime auctionTime : AuctionTime.values()) {
      if (auctionTime.name().equalsIgnoreCase(value)) {
        return auctionTime;
      }
    }
    return null;
  }
}
