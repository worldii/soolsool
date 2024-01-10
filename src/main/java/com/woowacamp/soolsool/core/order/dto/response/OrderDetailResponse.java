package com.woowacamp.soolsool.core.order.dto.response;

import com.woowacamp.soolsool.core.order.domain.Order;
import com.woowacamp.soolsool.core.order.domain.OrderPaymentInfo;
import com.woowacamp.soolsool.core.receipt.domain.Receipt;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailResponse {

    private Long orderId;
    private String orderStatus;
    private String originalTotalPrice;
    private String mileageUsage;
    private String purchasedTotalPrice;
    private Integer totalQuantity;
    private LocalDateTime createdAt;
    private List<OrderItemDetailResponse> orderItems;
    private OrderPaymentInfoResponse paymentInfo;

    public OrderDetailResponse(final Order order, final OrderPaymentInfo orderPaymentInfo) {
        final Receipt receipt = order.getReceipt();

        final List<OrderItemDetailResponse> receiptItems = receipt.getReceiptItems().stream()
                .map(OrderItemDetailResponse::from)
                .collect(Collectors.toUnmodifiableList());

        this.orderId = order.getId();
        this.orderStatus = order.getStatus().getType().getStatus();
        this.originalTotalPrice = receipt.getOriginalTotalPrice().toString();
        this.mileageUsage = receipt.getMileageUsage().toString();
        this.purchasedTotalPrice = receipt.getPurchasedTotalPrice().toString();
        this.totalQuantity = receipt.getTotalQuantity();
        this.createdAt = order.getCreatedAt();
        this.orderItems = receiptItems;
        this.paymentInfo = OrderPaymentInfoResponse.from(orderPaymentInfo);
    }

    public static OrderDetailResponse of(final Order order, final OrderPaymentInfo orderPaymentInfo) {
        return new OrderDetailResponse(order, orderPaymentInfo);
    }
}
