package com.goodsending.product.dto.request;

import com.goodsending.product.type.AuctionTime;
import com.goodsending.product.validator.ValidDateRange;
import com.goodsending.product.validator.ValidEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class ProductCreateRequestDto {

  @NotNull(message = "상품명은 필수 입력 항목입니다.")
  @Size(min = 1, max = 20, message = "상품명은 1글자 이상 20자 이하로 입력할 수 있습니다.")
  private String name;

  @Min(value = 0, message = "판매가를 0원 이상 입력해야합니다.")
  private int price;

  @NotNull(message = "상품 소개는 필수 입력 항목입니다.")
  @Size(min = 1, max = 255, message = "상품 소개는 1글자 이상 255자 이하로 입력할 수 있습니다.")
  private String introduction;

  @NotNull(message = "경매시작일은 필수 입력 항목입니다.")
  @ValidDateRange
  private LocalDate startDate;

  @ValidEnum(enumClass = AuctionTime.class)
  private AuctionTime auctionTime;

}
