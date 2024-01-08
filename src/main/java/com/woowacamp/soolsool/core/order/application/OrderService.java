package com.woowacamp.soolsool.core.order.application;

import com.woowacamp.soolsool.core.order.domain.*;
import com.woowacamp.soolsool.core.order.domain.vo.OrderStatusType;
import com.woowacamp.soolsool.core.order.dto.response.OrderDetailResponse;
import com.woowacamp.soolsool.core.order.dto.response.OrderListResponse;
import com.woowacamp.soolsool.core.order.dto.response.PageOrderListResponse;
import com.woowacamp.soolsool.core.receipt.domain.Receipt;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.woowacamp.soolsool.core.order.domain.vo.OrderStatusType.COMPLETED;
import static com.woowacamp.soolsool.core.order.exception.OrderErrorCode.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final int PERCENTAGE_BIAS = 100;
    private final OrderRepository orderRepository;
    private final OrderPaymentInfoRepository orderPaymentInfoRepository;
    private final OrderStatusCache orderStatusCache;
    private final OrderQueryRepository orderQueryRepository;
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

    @Transactional(readOnly = true)
    public OrderDetailResponse orderDetail(final Long memberId, final Long orderId) {
        final Order order = orderRepository.findOrderById(orderId)
                .orElseThrow(() -> new SoolSoolException(NOT_EXISTS_ORDER));

        validateAccessible(memberId, order);

        final OrderPaymentInfo orderPaymentInfo = orderPaymentInfoRepository
                .findPaymentInfoByOrderId(orderId)
                .orElseThrow(() -> new SoolSoolException(NOT_EXISTS_PAYMENT_INFO));

        return OrderDetailResponse.of(order, orderPaymentInfo);
    }

    @Transactional(readOnly = true)
    public PageOrderListResponse orderList(
            final Long memberId,
            final Pageable pageable,
            final Long cursorId
    ) {
        final List<OrderListResponse> orders = orderQueryRepository
                .findAllByMemberId(memberId, pageable, cursorId);

        if (orders.size() < pageable.getPageSize()) {
            return PageOrderListResponse.of(false, orders);
        }

        final Long lastReadOrderId = orders.get(orders.size() - 1).getOrderId();

        return PageOrderListResponse.of(true, lastReadOrderId, orders);
    }

    @Transactional
    public Long cancelOrder(final Long memberId, final Long orderId) {
        return orderCancelService.cancelOrder(memberId, orderId);
    }

    @Transactional(readOnly = true)
    public Double getOrderRatioByLiquorId(final Long liquorId) {
        return orderRepository.findOrderRatioByLiquorId(liquorId)
                .orElse(0.0) * PERCENTAGE_BIAS;
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

    @Transactional
    public Long addPaymentInfo(final OrderPaymentInfo payInfo) {
        return orderPaymentInfoRepository.save(payInfo).getId();
    }
}
