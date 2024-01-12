package com.woowacamp.soolsool.core.payment.application;

import com.woowacamp.soolsool.core.liquor.application.LiquorCommandService;
import com.woowacamp.soolsool.core.receipt.application.ReceiptService;
import com.woowacamp.soolsool.core.receipt.domain.Receipt;
import com.woowacamp.soolsool.core.receipt.domain.ReceiptItem;
import com.woowacamp.soolsool.global.aop.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PayFailHandler {

    private final ReceiptService receiptService;
    private final LiquorCommandService liquorCommandService;

    @Transactional
    @DistributedLock(lockName = "Receipt", entityId = "#receiptId", waitTime = 10L, leaseTime = 3L)
    public void recover(final Long memberId, final Long receiptId) {
        final Receipt receipt = receiptService.getMemberReceipt(memberId, receiptId);

        increaseStocks(receipt);
    }

    private void increaseStocks(final Receipt receipt) {
        for (final ReceiptItem receiptItem : receipt.getReceiptItems()) {
            liquorCommandService.increaseTotalStock(receiptItem.getLiquorId(), receiptItem.getQuantity());
        }
    }
}
