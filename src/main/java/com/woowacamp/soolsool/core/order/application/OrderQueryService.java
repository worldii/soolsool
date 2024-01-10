package com.woowacamp.soolsool.core.order.application;

import com.woowacamp.soolsool.core.order.domain.OrderQueryDslRepository;
import com.woowacamp.soolsool.core.order.domain.OrderRatioService;
import com.woowacamp.soolsool.core.order.dto.response.OrderDetailResponse;
import com.woowacamp.soolsool.core.order.dto.response.OrderListResponse;
import com.woowacamp.soolsool.core.order.dto.response.PageOrderListResponse;
import com.woowacamp.soolsool.core.order.exception.OrderErrorCode;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderQueryService {
    private final OrderQueryDslRepository orderQueryDslRepository;
    private final OrderRatioService orderRatioService;

    @Transactional(readOnly = true)
    public OrderDetailResponse orderDetail(final Long memberId, final Long orderId) {
        final OrderDetailResponse orderDetail = orderQueryDslRepository.getOrderDetailWithPaymentInfo(memberId, orderId);

        if (orderDetail == null) {
            throw new SoolSoolException(OrderErrorCode.NOT_EXISTS_ORDER);
        }

        return orderDetail;
    }

    @Transactional(readOnly = true)
    public PageOrderListResponse orderList(final Long memberId, final Pageable pageable, final Long cursorId) {
        final List<OrderListResponse> orders = orderQueryDslRepository.findAllByMemberId(memberId, pageable, cursorId);

        if (orders.size() < pageable.getPageSize()) {
            return PageOrderListResponse.of(false, orders);
        }

        final Long lastReadOrderId = orders.get(orders.size() - 1).getOrderId();

        return PageOrderListResponse.of(true, lastReadOrderId, orders);
    }

    @Transactional(readOnly = true)
    public Double getOrderRatioByLiquorId(final Long liquorId) {
        return orderRatioService.getOrderRatioByLiquorId(liquorId);
    }
}
