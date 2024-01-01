package com.woowacamp.soolsool.core.payment.domain;

import com.woowacamp.soolsool.core.payment.dto.response.PayApproveResponse;
import com.woowacamp.soolsool.core.payment.dto.response.PayReadyResponse;
import com.woowacamp.soolsool.core.receipt.domain.Receipt;

public interface PayClient {

    PayReadyResponse ready(final Receipt receipt);

    PayApproveResponse payApprove(final Object... args);
}
