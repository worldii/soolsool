package com.woowacamp.soolsool.core.payment.application;

import com.woowacamp.soolsool.core.cart.application.CartService;
import com.woowacamp.soolsool.core.liquor.application.LiquorCommandService;
import com.woowacamp.soolsool.core.liquor.application.LiquorStockService;
import com.woowacamp.soolsool.core.member.application.MemberService;
import com.woowacamp.soolsool.core.order.application.OrderService;
import com.woowacamp.soolsool.core.order.domain.Order;
import com.woowacamp.soolsool.core.order.domain.OrderPaymentInfo;
import com.woowacamp.soolsool.core.payment.domain.PayClient;
import com.woowacamp.soolsool.core.payment.dto.request.PayOrderRequest;
import com.woowacamp.soolsool.core.payment.dto.response.PayReadyResponse;
import com.woowacamp.soolsool.core.receipt.application.ReceiptService;
import com.woowacamp.soolsool.core.receipt.domain.Receipt;
import com.woowacamp.soolsool.core.receipt.domain.ReceiptItem;
import com.woowacamp.soolsool.core.receipt.domain.event.ReceiptRemoveEvent;
import com.woowacamp.soolsool.core.receipt.domain.vo.ReceiptStatusType;
import com.woowacamp.soolsool.global.aop.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayService {

    private final ReceiptService receiptService;
    private final MemberService memberService;
    private final OrderService orderService;
    private final CartService cartService;
    private final LiquorStockService liquorStockService;
    private final LiquorCommandService liquorCommandService;

    private final PayClient payClient;

    private final ApplicationEventPublisher publisher;

    // TODO : 결제 로직 분리
    // TODO : ready 일때, 재고 차감, 마일리지 차감, 장바구니 비우는 것을 진행
    @Transactional
    public PayReadyResponse ready(final Long memberId, final PayOrderRequest payOrderRequest) {
        final Receipt receipt = receiptService
            .getMemberReceipt(memberId, payOrderRequest.getReceiptId());

        return payClient.ready(receipt);
    }

    // TODO : 결제 로직 분리
    //  성공하면, 주문서 상태를 success 로 바꾸기, receiptPublisher remove 하기
    //  실패하면 재고 복귀, 마일리지 복귀, 장바구니 복귀 진행
    @Transactional
    @DistributedLock(lockName = "Receipt", entityId = "#receiptId")
    public Order approve(final Long memberId, final Long receiptId, final String pgToken) {
        final Receipt receipt = receiptService.getMemberReceipt(memberId, receiptId);

        // 재고 차감.
        decreaseStocks(receipt);

        // 주문서를 바탕으로 주문을 넣는다.
        final Order order = orderService.addOrder(memberId, receipt);

        // 마일리지를 차감한다.
        memberService.subtractMemberMileage(memberId, order, receipt.getMileageUsage());

        // 장바구니를 비운다.
        cartService.removeCartItems(memberId);

        // 주문서 상태를 Completed 로 바꾼다. (주문서 락)
        receiptService.modifyReceiptStatus(memberId, receiptId, ReceiptStatusType.COMPLETED);

        // 결제 정보를 받아 저장한다.(주문 서비스락)
        final OrderPaymentInfo payInfo = payClient.payApprove(receipt, pgToken)
            .toEntity(order.getId());
        orderService.addPaymentInfo(payInfo);

        // TODO : 단일 책임원칙이 지켜지지 않음. receipt 리무브 하는 것을 여기서 진행함..
        // publisher 를 가지고 이벤트를 발행한다.
        publisher.publishEvent(new ReceiptRemoveEvent(receiptId));

        return order;
    }

    private void decreaseStocks(final Receipt receipt) {
        for (final ReceiptItem receiptItem : receipt.getReceiptItems()) {
            liquorStockService.decreaseLiquorStock(receiptItem.getLiquorId(),
                receiptItem.getQuantity());
            liquorCommandService.decreaseTotalStock(receiptItem.getLiquorId(),
                receiptItem.getQuantity());
        }
    }

    @Transactional
    public void cancelReceipt(final Long memberId, final Long receiptId) {
        receiptService.modifyReceiptStatus(memberId, receiptId, ReceiptStatusType.CANCELED);
    }
}
