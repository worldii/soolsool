package com.woowacamp.soolsool.core.payment.application;

import com.woowacamp.soolsool.core.payment.domain.PayClient;
import com.woowacamp.soolsool.core.payment.dto.response.PayApproveResponse;
import com.woowacamp.soolsool.core.receipt.application.ReceiptService;
import com.woowacamp.soolsool.core.receipt.domain.Receipt;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import com.woowacamp.soolsool.helper.ReceiptHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("결제 승인 테스트")
class PayApproveServiceTest {

    @InjectMocks
    private PayApproveService payApproveService;
    @Mock
    private PaySuccessHandler paySuccessHandler;
    @Mock
    private PayFailHandler payFailHandler;
    @Mock
    private PayClient payClient;
    @Mock
    private ReceiptService receiptService;


    @Test
    @DisplayName("결제가 성공되면, PaySuccessHandler를 호출한다.")
    void executeSuccessIfPaymentSuccess() {
        // given
        final Long memberId = 1L;
        final Long receiptId = 1L;
        final String pgToken = "abc";
        final PayApproveResponse payApproveResponse = mock(PayApproveResponse.class);
        final Receipt receipt = ReceiptHelper.helper().build();
        final BigInteger number = new BigInteger("100");

        when(receiptService.getMemberReceipt(memberId, receiptId)).thenReturn(receipt);
        when(payClient.payApprove(receipt, pgToken)).thenReturn(payApproveResponse);
        when(paySuccessHandler.success(memberId, receipt, payApproveResponse, number)).thenReturn(null);

        // when
        payApproveService.approve(memberId, receiptId, pgToken);

        // then
        verify(paySuccessHandler, times(1)).success(memberId, receipt, payApproveResponse, number);
    }

    @Test
    @DisplayName("결제가 실패되면, PayFailHandler를 호출한다.")
    void executeFailHandleIfPaymentFail() {
        // given
        final Long memberId = 1L;
        final Long receiptId = 1L;
        final String pgToken = "abc";
        final PayApproveResponse payApproveResponse = mock(PayApproveResponse.class);
        final Receipt receipt = ReceiptHelper.helper().build();
        final BigInteger number = new BigInteger("100");
        when(receiptService.getMemberReceipt(memberId, receiptId)).thenReturn(receipt);
        when(payClient.payApprove(receipt, pgToken)).thenThrow(SoolSoolException.class);

        // when
        payApproveService.approve(memberId, receiptId, pgToken);

        // then
        verify(payFailHandler, times(1)).recover(memberId, receiptId);
    }
}
