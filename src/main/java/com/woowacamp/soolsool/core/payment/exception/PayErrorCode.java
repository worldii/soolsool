package com.woowacamp.soolsool.core.payment.exception;

import com.woowacamp.soolsool.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PayErrorCode implements ErrorCode {

    ACCESS_DENIED_RECEIPT(403, "P001", "본인의 주문서 내역만 조회할 수 있습니다."),
    NOT_FOUND_RECEIPT(404, "P002", "회원의 주문 내역을 찾을 수 없습니다."),
    NOT_FOUND_KAKAO_PAY_RECEIPT(404, "P003", "카카오 페이 주문 내역을 찾을 수 없습니다."),
    NOT_FOUND_LIQUOR(404, "P005", "주문한 술을 찾을 수 없습니다."),
    NOT_FOUND_PAY_APPROVE_RESPONSE(404, "P002", "페이 결제 승인 내역을 찾을 수 없습니다."),
    NOT_FOUND_PAY_READY_RESPONSE(404, "P006", "페이 결제 준비 내역을 찾을 수 없습니다."),
    NOT_MATCHED_LIQUOR_PRICE(400, "P007", "주문한 술의 가격이 일치하지 않습니다."),

    INTERRUPTED_THREAD(500, "P008", "예상치 못한 예외가 발생했습니다."),
    ;

    private final int status;
    private final String code;
    private final String message;
}
