package com.woowacamp.soolsool.core.order.domain;

import com.woowacamp.soolsool.global.common.DomainService;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class OrderRatioService {
    private static final int PERCENTAGE_BIAS = 100;

    private final OrderRepository orderRepository;

    public Double getOrderRatioByLiquorId(final Long liquorId) {
        return orderRepository.findOrderRatioByLiquorId(liquorId)
                .orElse(0.0) * PERCENTAGE_BIAS;
    }
}
