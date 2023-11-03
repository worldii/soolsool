package com.woowacamp.soolsool.core.payment.service;

import com.woowacamp.soolsool.core.cart.application.CartService;
import com.woowacamp.soolsool.core.liquor.service.LiquorService;
import com.woowacamp.soolsool.core.liquor.service.LiquorStockService;
import com.woowacamp.soolsool.core.member.service.MemberService;
import com.woowacamp.soolsool.core.order.domain.Order;
import com.woowacamp.soolsool.core.order.domain.OrderPaymentInfo;
import com.woowacamp.soolsool.core.order.service.OrderService;
import com.woowacamp.soolsool.core.payment.code.PayErrorCode;
import com.woowacamp.soolsool.core.payment.dto.request.PayOrderRequest;
import com.woowacamp.soolsool.core.payment.dto.response.PayReadyResponse;
import com.woowacamp.soolsool.core.payment.infra.PayClient;
import com.woowacamp.soolsool.core.receipt.domain.Receipt;
import com.woowacamp.soolsool.core.receipt.domain.ReceiptItem;
import com.woowacamp.soolsool.core.receipt.domain.vo.ReceiptStatusType;
import com.woowacamp.soolsool.core.receipt.event.ReceiptRemoveEvent;
import com.woowacamp.soolsool.core.receipt.service.ReceiptService;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import com.woowacamp.soolsool.global.infra.LockType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayService {

    private static final long LOCK_WAIT_TIME = 3L;
    private static final long LOCK_LEASE_TIME = 3L;

    private final ReceiptService receiptService;
    private final MemberService memberService;
    private final OrderService orderService;
    private final CartService cartService;
    private final LiquorStockService liquorStockService;
    private final LiquorService liquorService;

    private final PayClient payClient;

    private final ApplicationEventPublisher publisher;

    private final RedissonClient redissonClient;

    @Transactional
    public PayReadyResponse ready(final Long memberId, final PayOrderRequest payOrderRequest) {
        final Receipt receipt = receiptService
            .getMemberReceipt(memberId, payOrderRequest.getReceiptId());

        return payClient.ready(receipt);
    }

    @Transactional
    public Order approve(final Long memberId, final Long receiptId, final String pgToken) {
        final Receipt receipt = receiptService.getMemberReceipt(memberId, receiptId);

        final List<RLock> locks = new ArrayList<>();
        locks.add(getMemberLock(memberId));
        locks.add(getReceiptLock(receiptId));
        locks.addAll(getLiquorLocks(receipt.getReceiptItems()));

        final RLock multiLock = redissonClient.getMultiLock(
            locks.toArray(locks.toArray(new RLock[0]))
        );

        try {
            multiLock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS);

            // 재고 차감.
            decreaseStocks(receipt);

            // 주문서를 바탕으로 주문을 넣는다.
            final Order order = orderService.addOrder(memberId, receipt);

            // 마일리지를 차감한다. (멤버 락)
            memberService.subtractMemberMileage(memberId, order, receipt.getMileageUsage());

            // 장바구니를 비운다. (멤버 락)
            cartService.removeCartItems(memberId);

            // 주문서 상태를 Completed 로 바꾼다. (주문서 락 )
            receiptService.modifyReceiptStatus(memberId, receiptId, ReceiptStatusType.COMPLETED);

            // 결제 정보를 받아 저장한다.(주문 서비스락 )
            OrderPaymentInfo payInfo = payClient.payApprove(receipt, pgToken)
                .toEntity(order.getId());
            orderService.addPaymentInfo(payInfo);

            // publisher 를 가지고 이벤트를 발행한다.
            publisher.publishEvent(new ReceiptRemoveEvent(receiptId));

            return order;
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new SoolSoolException(PayErrorCode.INTERRUPTED_THREAD);
        } finally {
            multiLock.unlock();
        }
    }

    private RLock getMemberLock(final Long memberId) {
        return redissonClient.getLock(LockType.MEMBER.getPrefix() + memberId);
    }

    private RLock getReceiptLock(final Long receiptId) {
        return redissonClient.getLock(LockType.RECEIPT.getPrefix() + receiptId);
    }

    private List<RLock> getLiquorLocks(final List<ReceiptItem> receiptItems) {
        return receiptItems.stream()
            .map(ReceiptItem::getLiquorId)
            .sorted()
            .map(liquorId -> redissonClient.getLock(
                LockType.LIQUOR_STOCK.getPrefix() + liquorId))
            .collect(Collectors.toList());
    }

    private void decreaseStocks(final Receipt receipt) {
        for (final ReceiptItem receiptItem : receipt.getReceiptItems()) {
            liquorStockService.decreaseLiquorStock(receiptItem.getLiquorId(),
                receiptItem.getQuantity());
            liquorService.decreaseTotalStock(receiptItem.getLiquorId(),
                receiptItem.getQuantity());
        }
    }

    @Transactional
    public void cancelReceipt(final Long memberId, final Long receiptId) {
        receiptService.modifyReceiptStatus(memberId, receiptId, ReceiptStatusType.CANCELED);
    }
}
