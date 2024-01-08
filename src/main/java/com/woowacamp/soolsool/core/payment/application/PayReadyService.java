package com.woowacamp.soolsool.core.payment.application;

import com.woowacamp.soolsool.core.liquor.application.LiquorCommandService;
import com.woowacamp.soolsool.core.payment.domain.PayClient;
import com.woowacamp.soolsool.core.payment.dto.request.PayOrderRequest;
import com.woowacamp.soolsool.core.payment.dto.response.PayReadyResponse;
import com.woowacamp.soolsool.core.receipt.application.ReceiptService;
import com.woowacamp.soolsool.core.receipt.domain.Receipt;
import com.woowacamp.soolsool.core.receipt.domain.ReceiptItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PayReadyService {

    private final ReceiptService receiptService;
    private final LiquorCommandService liquorCommandService;
    private final PayClient payClient;

    @Transactional
    public PayReadyResponse ready(final Long memberId, final PayOrderRequest payOrderRequest) {
        final Receipt receipt = receiptService
                .getMemberReceipt(memberId, payOrderRequest.getReceiptId());

        decreaseStocks(receipt);
        
        return payClient.ready(receipt);
    }

    private void decreaseStocks(final Receipt receipt) {
        for (final ReceiptItem receiptItem : receipt.getReceiptItems()) {
            liquorCommandService.decreaseTotalStock(receiptItem.getLiquorId(), receiptItem.getQuantity());
        }
    }
}
