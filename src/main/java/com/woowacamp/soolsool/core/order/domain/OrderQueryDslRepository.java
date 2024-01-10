package com.woowacamp.soolsool.core.order.domain;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.woowacamp.soolsool.core.order.dto.response.OrderDetailResponse;
import com.woowacamp.soolsool.core.order.dto.response.OrderListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.woowacamp.soolsool.core.order.domain.QOrder.order;
import static com.woowacamp.soolsool.core.order.domain.QOrderPaymentInfo.orderPaymentInfo;

@Repository
@RequiredArgsConstructor
public class OrderQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public OrderDetailResponse getOrderDetailWithPaymentInfo(final Long memberId, final Long orderId) {
        return queryFactory.select(
                        Projections.constructor(OrderDetailResponse.class, order, orderPaymentInfo)
                )
                .from(order)
                .join(orderPaymentInfo)
                .on(orderPaymentInfo.orderId.eq(orderId))
                .where(order.memberId.eq(memberId))
                .fetchOne();
    }

    public List<OrderListResponse> findAllByMemberId(
            final Long memberId,
            final Pageable pageable,
            final Long cursorId
    ) {
        return queryFactory.select(
                        Projections.constructor(
                                OrderListResponse.class,
                                order
                        )
                )
                .from(order)
                .join(order.status).fetchJoin()
                .join(order.receipt).fetchJoin()
                .where(
                        order.memberId.eq(memberId),
                        cursorId(cursorId)
                )
                .orderBy(order.id.desc())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private BooleanExpression cursorId(final Long cursorId) {
        if (cursorId == null) {
            return null;
        }
        return order.id.lt(cursorId);
    }
}
