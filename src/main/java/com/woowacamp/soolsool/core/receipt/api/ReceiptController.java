package com.woowacamp.soolsool.core.receipt.api;

import com.woowacamp.soolsool.core.member.dto.LoginUser;
import com.woowacamp.soolsool.core.receipt.application.ReceiptService;
import com.woowacamp.soolsool.core.receipt.dto.response.ReceiptDetailResponse;
import com.woowacamp.soolsool.global.aop.RequestLogging;
import com.woowacamp.soolsool.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static com.woowacamp.soolsool.core.receipt.exception.ReceiptResultCode.RECEIPT_ADD_SUCCESS;
import static com.woowacamp.soolsool.core.receipt.exception.ReceiptResultCode.RECEIPT_FOUND;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/receipts")
public class ReceiptController {

    private final ReceiptService receiptService;

    @RequestLogging
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> addReceipt(
            @LoginUser final Long memberId
    ) {
        final Long receiptId = receiptService.addReceipt(memberId);

        return ResponseEntity.created(URI.create("/receipts/" + receiptId))
                .body(ApiResponse.of(RECEIPT_ADD_SUCCESS, receiptId));
    }

    @RequestLogging
    @GetMapping("/{receiptId}")
    public ResponseEntity<ApiResponse<ReceiptDetailResponse>> receiptDetails(
            @LoginUser final Long memberId,
            @PathVariable final Long receiptId
    ) {
        final ReceiptDetailResponse receipt = receiptService.findReceipt(memberId, receiptId);

        return ResponseEntity.ok(ApiResponse.of(RECEIPT_FOUND, receipt));
    }
}
