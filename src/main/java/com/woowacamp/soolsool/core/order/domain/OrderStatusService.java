package com.woowacamp.soolsool.core.order.domain;

import com.woowacamp.soolsool.core.order.domain.vo.OrderStatusType;
import com.woowacamp.soolsool.global.aop.DistributedLock;
import com.woowacamp.soolsool.global.common.DomainService;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

import static com.woowacamp.soolsool.core.order.exception.OrderErrorCode.*;

@DomainService
@RequiredArgsConstructor
public class OrderStatusService {

    private final OrderRepository orderRepository;
    private final OrderStatusCache orderStatusCache;

    @DistributedLock(lockName = "Order", entityId = "#orderId", waitTime = 3L, leaseTime = 3L)
    public Long modifyOrderStatusType(
            final Long memberId,
            final Long orderId,
            final OrderStatusType orderStatusType
    ) {
        final Order order = orderRepository.findOrderById(orderId)
                .orElseThrow(() -> new SoolSoolException(NOT_EXISTS_ORDER));

        validateAccessible(memberId, order);
        order.updateStatus(getOrderStatusByType(orderStatusType));

        return order.getId();
    }

    private void validateAccessible(final Long memberId, final Order order) {
        if (!Objects.equals(memberId, order.getMemberId())) {
            throw new SoolSoolException(ACCESS_DENIED_ORDER);
        }
    }

    private OrderStatus getOrderStatusByType(final OrderStatusType type) {
        return orderStatusCache.findByType(type)
                .orElseThrow(() -> new SoolSoolException(NOT_EXISTS_ORDER_STATUS));
    }
}
