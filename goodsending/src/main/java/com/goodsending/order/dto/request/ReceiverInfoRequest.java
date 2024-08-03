package com.goodsending.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author : jieun(je-pa)
 * @Date : 2024. 08. 02.
 * @Team : GoodsEnding
 * @Project : goodsending-be :: goodsending
 */
public record ReceiverInfoRequest(

    @NotNull
    Long orderId,

    @NotBlank
    String receiverName,

    @NotBlank
    @ValidPhoneNumber
    String receiverCellNumber,

    @NotBlank
    String receiverAddress

) {

}
