package com.woowacamp.soolsool.core.order.application;

import com.woowacamp.soolsool.core.order.domain.OrderPaymentInfo;
import com.woowacamp.soolsool.core.order.domain.OrderPaymentInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderPaymentService {
    private final OrderPaymentInfoRepository orderPaymentInfoRepository;

    @Transactional
    public Long addPaymentInfo(final OrderPaymentInfo payInfo) {
        return orderPaymentInfoRepository.save(payInfo).getId();
    }
}
