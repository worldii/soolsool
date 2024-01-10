package com.woowacamp.soolsool.core.payment.api;

import com.woowacamp.soolsool.core.member.dto.LoginUser;
import com.woowacamp.soolsool.core.member.dto.NoAuth;
import com.woowacamp.soolsool.core.order.domain.Order;
import com.woowacamp.soolsool.core.payment.application.PayApproveService;
import com.woowacamp.soolsool.core.payment.application.PayCancelService;
import com.woowacamp.soolsool.core.payment.application.PayReadyService;
import com.woowacamp.soolsool.core.payment.dto.request.PayOrderRequest;
import com.woowacamp.soolsool.core.payment.dto.response.PayReadyResponse;
import com.woowacamp.soolsool.core.payment.dto.response.PaySuccessResponse;
import com.woowacamp.soolsool.global.aop.RequestLogging;
import com.woowacamp.soolsool.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.woowacamp.soolsool.core.payment.exception.PayResultCode.*;

@RestController
@Slf4j
@RequestMapping("/pay")
@RequiredArgsConstructor
public class PayController {

    private final PayApproveService payApproveService;
    private final PayReadyService payReadyService;
    private final PayCancelService payCancelService;

    @RequestLogging
    @PostMapping("/ready")
    public ResponseEntity<ApiResponse<PayReadyResponse>> payReady(
            @LoginUser final Long memberId,
            @RequestBody final PayOrderRequest payOrderRequest
    ) {
        return ResponseEntity.ok(
                ApiResponse.of(PAY_READY_SUCCESS, payReadyService.ready(memberId, payOrderRequest)));
    }

    @NoAuth
    @RequestLogging
    @GetMapping("/success/{receiptId}")
    public ResponseEntity<ApiResponse<PaySuccessResponse>> kakaoPaySuccess(
            @LoginUser final Long memberId,
            @PathVariable("receiptId") final Long receiptId,
            @RequestParam("pg_token") final String pgToken
    ) {
        final Order order = payApproveService.approve(memberId, receiptId, pgToken);

        return ResponseEntity.ok(
                ApiResponse.of(PAY_READY_SUCCESS, new PaySuccessResponse(order.getId())));
    }

    @NoAuth
    @RequestLogging
    @GetMapping("/cancel/{receiptId}")
    public ResponseEntity<ApiResponse<Long>> kakaoPayCancel(
            @LoginUser final Long memberId,
            @PathVariable final Long receiptId
    ) {
        payCancelService.cancelPay(memberId, receiptId);

        return ResponseEntity.ok(ApiResponse.from(PAY_READY_CANCEL));
    }

    @NoAuth
    @RequestLogging
    @GetMapping("/fail/{receiptId}")
    public ResponseEntity<ApiResponse<Long>> kakaoPayFail(
            @LoginUser final Long memberId,
            @PathVariable final Long receiptId
    ) {
        payCancelService.cancelPay(memberId, receiptId);

        return ResponseEntity.ok(ApiResponse.from(PAY_READY_FAIL));
    }
}
