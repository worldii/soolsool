package com.woowacamp.soolsool.core.order.domain;

import com.woowacamp.soolsool.core.member.application.MemberService;
import com.woowacamp.soolsool.core.member.dto.request.MemberMileageChargeRequest;
import com.woowacamp.soolsool.core.order.exception.OrderErrorCode;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.woowacamp.soolsool.core.order.domain.vo.OrderStatusType.CANCELED;
import static com.woowacamp.soolsool.core.order.exception.OrderErrorCode.ACCESS_DENIED_ORDER;

@Service
@RequiredArgsConstructor
public class OrderCancelService {

    private final OrderStatusService orderStatusService;
    private final MemberService memberService;
    private final OrderRepository orderRepository;

    @Transactional
    public Long cancelOrder(final Long memberId, final Long orderId) {
        final Order order = orderRepository
                .findOrderById(orderId).orElseThrow(() -> new SoolSoolException(OrderErrorCode.NOT_EXISTS_ORDER));

        validateAccessible(memberId, order);

        memberService.addMemberMileage(memberId, new MemberMileageChargeRequest(order.getMileageUsage()));

        return orderStatusService.modifyOrderStatusType(memberId, order.getId(), CANCELED);
    }

    private void validateAccessible(final Long memberId, final Order order) {
        if (!Objects.equals(memberId, order.getMemberId())) {
            throw new SoolSoolException(ACCESS_DENIED_ORDER);
        }
    }
}
