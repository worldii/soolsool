package com.woowacamp.soolsool.core.payment.application;

import com.woowacamp.soolsool.core.order.domain.Order;
import com.woowacamp.soolsool.core.payment.domain.PayClient;
import com.woowacamp.soolsool.core.payment.dto.response.PayApproveResponse;
import com.woowacamp.soolsool.core.receipt.application.ReceiptService;
import com.woowacamp.soolsool.core.receipt.domain.Receipt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayApproveService {

    private final PayClient payClient;
    private final PaySuccessHandler paySuccessHandler;
    private final PayFailHandler payFailHandler;

    private final ReceiptService receiptService;

    public Order approve(final Long memberId, final Long receiptId, final String pgToken) {
        try {
            final Receipt receipt = receiptService.getMemberReceipt(memberId, receiptId);
            final PayApproveResponse payApproveResponse = payClient.payApprove(receipt, pgToken);

            return paySuccessHandler.success(memberId, receipt, payApproveResponse, receipt.getMileageUsage());
        } catch (final Exception e) {

            payFailHandler.recover(memberId, receiptId);

            return null;
        }
    }
}
