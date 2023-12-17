package com.woowacamp.soolsool.core.order.domain;

import java.math.BigInteger;

public interface OrderMemberService {

    void refundMileage(final Long memberId, final BigInteger mileage);
}

