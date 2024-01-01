package com.woowacamp.soolsool.core.order.domain;

import static com.woowacamp.soolsool.core.order.domain.vo.OrderStatusType.CANCELED;
import static com.woowacamp.soolsool.core.order.exception.OrderErrorCode.ACCESS_DENIED_ORDER;
import static com.woowacamp.soolsool.core.order.exception.OrderErrorCode.INTERRUPTED_THREAD;
import static com.woowacamp.soolsool.core.order.exception.OrderErrorCode.NOT_EXISTS_ORDER;
import static com.woowacamp.soolsool.core.order.exception.OrderErrorCode.NOT_EXISTS_ORDER_STATUS;

import com.woowacamp.soolsool.global.exception.SoolSoolException;
import com.woowacamp.soolsool.global.infra.LockType;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderCancelService {

    private final RedissonClient redissonClient;
    private static final long LOCK_WAIT_TIME = 3L;
    private static final long LOCK_LEASE_TIME = 3L;

    private final OrderRepository orderRepository;
    private final OrderStatusCache orderStatusCache;
    private final OrderMemberService orderMemberService;

    // TODO : 이거 추후에 이벤트 핸들러로 개선하기
    @Transactional
    public Order cancelOrder(final Long memberId, final Long orderId) {
        final RLock multiLock = redissonClient.getMultiLock(
            getMemberLock(memberId),
            getOrderLock(orderId)
        );

        try {
            multiLock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS);

            final Order order = orderRepository.findOrderById(orderId)
                .orElseThrow(() -> new SoolSoolException(NOT_EXISTS_ORDER));

            validateAccessible(memberId, order);

            final OrderStatus cancelOrderStatus = orderStatusCache.findByType(CANCELED)
                .orElseThrow(() -> new SoolSoolException(NOT_EXISTS_ORDER_STATUS));

            order.updateStatus(cancelOrderStatus);
            orderMemberService.refundMileage(memberId, order.getMileageUsage());

            return order;
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new SoolSoolException(INTERRUPTED_THREAD);

        } finally {
            multiLock.unlock();
        }
    }

    private RLock getOrderLock(final Long orderId) {
        return redissonClient.getLock(LockType.ORDER.getPrefix() + orderId);
    }

    private RLock getMemberLock(final Long memberId) {
        return redissonClient.getLock(LockType.MEMBER.getPrefix() + memberId);
    }

    private void validateAccessible(final Long memberId, final Order order) {
        if (!Objects.equals(memberId, order.getMemberId())) {
            throw new SoolSoolException(ACCESS_DENIED_ORDER);
        }
    }
}
