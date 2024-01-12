package com.woowacamp.soolsool.helper;

import com.woowacamp.soolsool.core.receipt.domain.Receipt;
import com.woowacamp.soolsool.core.receipt.domain.ReceiptItem;
import com.woowacamp.soolsool.core.receipt.domain.ReceiptStatus;
import com.woowacamp.soolsool.core.receipt.domain.vo.ReceiptItemPrice;
import com.woowacamp.soolsool.core.receipt.domain.vo.ReceiptItemQuantity;
import com.woowacamp.soolsool.core.receipt.domain.vo.ReceiptStatusType;

import java.math.BigInteger;
import java.util.List;

public class ReceiptHelper {
    public static Receipt.ReceiptBuilder helper() {
        return Receipt.builder()
                .mileageUsage(new ReceiptItemPrice(new BigInteger("100")))
                .receiptItems(List.of(new ReceiptItem(null, 1L, "a", "a", "a"
                        , "100", "100", "a", "a"
                        , 10.0, 10, 10)))
                .receiptStatus(new ReceiptStatus(ReceiptStatusType.COMPLETED))
                .purchasedTotalPrice(new ReceiptItemPrice(new BigInteger("100")))
                .totalQuantity(new ReceiptItemQuantity(10))
                .originalTotalPrice(new ReceiptItemPrice(new BigInteger("100")))
                .memberId(1L);
    }
}
