package com.woowacamp.soolsool.core.order.application;

import com.woowacamp.soolsool.core.member.application.MemberService;
import com.woowacamp.soolsool.core.member.domain.MemberRoleCache;
import com.woowacamp.soolsool.core.order.domain.*;
import com.woowacamp.soolsool.core.order.domain.vo.OrderStatusType;
import com.woowacamp.soolsool.core.order.exception.OrderErrorCode;
import com.woowacamp.soolsool.global.config.CacheManagerConfig;
import com.woowacamp.soolsool.global.config.QuerydslConfig;
import com.woowacamp.soolsool.global.config.RedissonConfig;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(
        {
                OrderCommandService.class, OrderStatusCache.class, QuerydslConfig.class, RedissonConfig.class,
                CacheManagerConfig.class, OrderCancelService.class, OrderStatusService.class,
                MemberService.class, MemberRoleCache.class, OrderRatioService.class, OrderQueryService.class,
                OrderQueryDslRepository.class
        }
)
@DisplayName("통합 테스트: OrderService")
class OrderCommandServiceIntegrationTest {

    @Autowired
    private OrderCommandService orderCommandService;
    @Autowired
    private OrderQueryService orderQueryService;
    @Autowired
    private OrderRepository orderRepository;

    @Test
    @Sql({
            "/member-type.sql", "/member.sql",
            "/liquor-type.sql", "/liquor.sql",
            "/cart-item.sql",
            "/receipt-type.sql", "/receipt.sql",
            "/order-type.sql", "/order.sql"
    })
    @DisplayName("주문 상세 내역 조회 시 주문이 존재하지 않을 경우 SoolSoolException을 던진다.")
    void failOrderDetailWhenNotExistsOrder() {
        // given
        Long 김배달 = 1L;

        // when & then

        assertThatThrownBy(() -> orderQueryService.orderDetail(김배달, 99999L))
                .isExactlyInstanceOf(SoolSoolException.class)
                .hasMessage(OrderErrorCode.NOT_EXISTS_ORDER.getMessage());
    }

    @Test
    @Sql({
            "/member-type.sql", "/member.sql",
            "/liquor-type.sql", "/liquor.sql",
            "/cart-item.sql",
            "/receipt-type.sql", "/receipt.sql",
            "/order-type.sql", "/order.sql"
    })
    @DisplayName("다른 사용자의 주문 상세내역을 조회할 경우 SoolSoolException을 던진다.")
    void failOrderDetailWhenAccessToOthers() {
        // given
        Long 최민족 = 2L;
        Long 김배달_주문 = 1L;

        // when & then
        assertThatThrownBy(() -> orderQueryService.orderDetail(최민족, 김배달_주문))
                .isExactlyInstanceOf(SoolSoolException.class)
                .hasMessage(OrderErrorCode.NOT_EXISTS_ORDER.getMessage());
    }

    @Test
    @Sql({
            "/member-type.sql", "/member.sql",
            "/liquor-type.sql", "/liquor.sql",
            "/cart-item.sql",
            "/receipt-type.sql", "/receipt.sql",
            "/order-type.sql", "/order.sql"
    })
    @DisplayName("주문 상태 변경 시 Order가 존재하지 않을 경우 SoolSoolException을 던진다.")
    void failModifyOrderWhenNotExistsOrder() {
        // given
        Long 김배달 = 1L;

        // when & then
        assertThatThrownBy(() -> orderCommandService.cancelOrder(김배달, 99999L))
                .isExactlyInstanceOf(SoolSoolException.class)
                .hasMessage("주문 내역이 존재하지 않습니다.");
    }

    @Test
    @Sql({
            "/member-type.sql", "/member.sql",
            "/liquor-type.sql", "/liquor.sql",
            "/cart-item.sql",
            "/receipt-type.sql", "/receipt.sql",
            "/order-type.sql", "/order.sql"
    })
    @DisplayName("다른 사용자의 주문 상세내역을 변경할 경우 SoolSoolException을 던진다.")
    void failModifyOrderWhenAccessToOthers() {
        // given
        Long 최민족 = 2L;
        Long 김배달_주문 = 1L;

        // when & then
        assertThatThrownBy(() -> orderCommandService.cancelOrder(최민족, 김배달_주문))
                .isExactlyInstanceOf(SoolSoolException.class)
                .hasMessage("본인의 주문 내역만 조회할 수 있습니다.");
    }

    @Test
    @Sql({
            "/member-type.sql", "/member.sql",
            "/liquor-type.sql", "/liquor.sql",
            "/cart-item.sql",
            "/receipt-type.sql", "/receipt.sql",
            "/order-type.sql", "/order.sql"
    })
    @DisplayName("주문을 취소한다.")
    void cancelOrder() throws Exception {
        /* given */
        Long 김배달 = 1L;
        Long 김배달_주문 = 1L;

        /* when */
        Long orderId = orderCommandService.cancelOrder(김배달, 김배달_주문);

        /* then */
        final Order order = orderRepository.findOrderById(orderId)
                .orElseThrow(RuntimeException::new);
        assertThat(order.getStatus().getType()).isEqualTo(OrderStatusType.CANCELED);
    }
}
