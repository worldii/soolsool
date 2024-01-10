package com.woowacamp.soolsool.core.order.application;

import com.woowacamp.soolsool.core.order.domain.*;
import com.woowacamp.soolsool.core.order.domain.vo.OrderStatusType;
import com.woowacamp.soolsool.core.receipt.domain.Receipt;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.woowacamp.soolsool.core.order.domain.vo.OrderStatusType.COMPLETED;
import static com.woowacamp.soolsool.core.order.exception.OrderErrorCode.NOT_EXISTS_ORDER_STATUS;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderCommandService {

    private final OrderRepository orderRepository;
    private final OrderStatusCache orderStatusCache;

    private final OrderCancelService orderCancelService;

    @Transactional
    public Order addOrder(final Long memberId, final Receipt receipt) {
        final OrderStatus orderStatus = getOrderStatusByType(COMPLETED);

        final Order order = Order.builder()
                .memberId(memberId)
                .orderStatus(orderStatus)
                .receipt(receipt)
                .build();

        return orderRepository.save(order);
    }

    private OrderStatus getOrderStatusByType(final OrderStatusType type) {
        return orderStatusCache.findByType(type)
                .orElseThrow(() -> new SoolSoolException(NOT_EXISTS_ORDER_STATUS));
    }

    @Transactional
    public Long cancelOrder(final Long memberId, final Long orderId) {
        return orderCancelService.cancelOrder(memberId, orderId);
    }
}
