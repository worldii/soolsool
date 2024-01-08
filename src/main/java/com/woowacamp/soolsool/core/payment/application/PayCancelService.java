package com.woowacamp.soolsool.core.payment.application;

import com.woowacamp.soolsool.core.receipt.application.ReceiptService;
import com.woowacamp.soolsool.core.receipt.domain.vo.ReceiptStatusType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PayCancelService {
    private final ReceiptService receiptService;

    @Transactional
    public void cancelPay(final Long memberId, final Long receiptId) {
        receiptService.modifyReceiptStatus(memberId, receiptId, ReceiptStatusType.CANCELED);
    }
}
