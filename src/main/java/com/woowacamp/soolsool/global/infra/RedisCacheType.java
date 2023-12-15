package com.woowacamp.soolsool.global.infra;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisCacheType {

    LIQUOR_FIRST_PAGE("liquorsFirstPage", 60L),
    LIQUOR_STATUS("liquorStatus", 60 * 60 * 24L),
    LIQUOR_BREW("liquorBrew", 60 * 60 * 24L),
    LIQUOR_REGION("liquorRegion", 60 * 60 * 24L),
    MEMBER_ROLE("memberRole", 60 * 60 * 24L),
    ORDER_STATUS("orderStatus", 60 * 60 * 24L),
    RECEIPT_STATUS("receiptStatus", 60 * 60 * 24L),
    LIQUOR_FIRST("liquorsFirstPage", 5L),
    ;

    private final String cacheName;
    private final Long expireSeconds;
}
