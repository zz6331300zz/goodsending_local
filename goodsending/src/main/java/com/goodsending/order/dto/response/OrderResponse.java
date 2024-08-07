package com.goodsending.order.dto.response;

import com.goodsending.order.entity.Order;
import com.goodsending.order.type.OrderStatus;
import java.time.LocalDateTime;
import lombok.Builder;

/**
 * @author : jieun(je-pa)
 * @Date : 2024. 08. 04.
 * @Team : GoodsEnding
 * @Project : goodsending-be :: goodsending
 */
@Builder
public record OrderResponse(
    Long orderId,

    Long sellerId,

    String receiverName,

    String receiverCellNumber,

    String receiverAddress,

    LocalDateTime deliveryDateTime,

    LocalDateTime confirmedDateTime,

    OrderStatus status

) {
    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
            .orderId(order.getId())
            .sellerId(order.getBid().getProduct().getMember().getMemberId())
            .receiverName(order.getReceiverName())
            .receiverCellNumber(order.getReceiverCellNumber())
            .receiverAddress(order.getReceiverAddress())
            .deliveryDateTime(order.getDeliveryDateTime())
            .confirmedDateTime(order.getConfirmedDateTime())
            .status(order.getStatus())
            .build();
    }
}
