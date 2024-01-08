package com.woowacamp.soolsool.core.order.domain;

import static com.woowacamp.soolsool.core.order.domain.vo.OrderStatusType.CANCELED;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderCancelService {

    private final OrderStatusService orderStatusService;
    
    @Transactional
    public Long cancelOrder(final Long memberId, final Long orderId) {
        return orderStatusService.modifyOrderStatusType(memberId, orderId, CANCELED);
    }
}
