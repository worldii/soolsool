package com.woowacamp.soolsool.core.payment.application;

import com.woowacamp.soolsool.core.cart.application.CartService;
import com.woowacamp.soolsool.core.liquor.application.LiquorStockService;
import com.woowacamp.soolsool.core.member.application.MemberService;
import com.woowacamp.soolsool.core.order.application.OrderService;
import com.woowacamp.soolsool.core.order.domain.Order;
import com.woowacamp.soolsool.core.payment.dto.response.PayApproveResponse;
import com.woowacamp.soolsool.core.receipt.application.ReceiptService;
import com.woowacamp.soolsool.core.receipt.domain.Receipt;
import com.woowacamp.soolsool.core.receipt.domain.ReceiptItem;
import com.woowacamp.soolsool.core.receipt.domain.event.ReceiptRemoveEvent;
import com.woowacamp.soolsool.core.receipt.domain.vo.ReceiptStatusType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Component
@RequiredArgsConstructor
public class PaySuccessHandler {

    private final ApplicationEventPublisher publisher;
    private final OrderService orderService;
    private final MemberService memberService;
    private final ReceiptService receiptService;
    private final CartService cartService;
    private final LiquorStockService liquorStockService;


    @Transactional
    public Order success(
            final Long memberId,
            final Receipt receipt,
            final PayApproveResponse payApproveResponse,
            final BigInteger mileage
    ) {

        // 주문서를 바탕으로 주문을 넣는다. // 성공 후.
        final Order order = orderService.addOrder(memberId, receipt);
        // 결제 정보를 받아 저장한다.(주문 서비스락) // 성공 후
        orderService.addPaymentInfo(payApproveResponse, order.getId());
        // 성공 후 receipt completed 로 바꿔주기
        receiptService.modifyReceiptStatus(memberId, receipt.getId(), ReceiptStatusType.COMPLETED);
        // 성공 후 멤버 마일리지 차감 // 성공 후
        memberService.subtractMemberMileage(memberId, order, mileage);
        // 성공 후 장바구니 비워 주기
        cartService.removeCartItems(memberId);
        // 성공 후 재고 수량 차감
        decreaseStocksCount(receipt);
        // publisher 를 가지고 이벤트를 발행한다. // 성공 후
        publisher.publishEvent(new ReceiptRemoveEvent(receipt.getId()));
        
        return order;
    }

    private void decreaseStocksCount(final Receipt receipt) {
        for (final ReceiptItem receiptItem : receipt.getReceiptItems()) {
            liquorStockService.decreaseLiquorStock(receiptItem.getLiquorId(), receiptItem.getQuantity());
        }
    }
}
