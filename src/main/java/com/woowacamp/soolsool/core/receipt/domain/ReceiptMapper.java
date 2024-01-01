package com.woowacamp.soolsool.core.receipt.domain;

import static com.woowacamp.soolsool.core.cart.exception.CartErrorCode.NOT_FOUND_CART_ITEM;
import static com.woowacamp.soolsool.core.receipt.domain.vo.ReceiptStatusType.INPROGRESS;
import static com.woowacamp.soolsool.core.receipt.exception.ReceiptErrorCode.NOT_RECEIPT_TYPE_FOUND;

import com.woowacamp.soolsool.core.cart.domain.CartItem;
import com.woowacamp.soolsool.core.receipt.domain.repository.ReceiptStatusCache;
import com.woowacamp.soolsool.core.receipt.domain.vo.ReceiptItemPrice;
import com.woowacamp.soolsool.core.receipt.domain.vo.ReceiptItemQuantity;
import com.woowacamp.soolsool.core.receipt.domain.vo.ReceiptStatusType;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReceiptMapper {

    private static final BigInteger MILEAGE_USAGE_PERCENT = BigInteger.valueOf(10L);

    private final ReceiptStatusCache receiptStatusRepository;

    public Receipt mapFrom(final Long memberId, final List<CartItem> cartItems,
        final BigInteger mileage) {
        if (cartItems.isEmpty()) {
            throw new SoolSoolException(NOT_FOUND_CART_ITEM);
        }

        final ReceiptItemPrice totalPrice = computeTotalPrice(cartItems);
        final ReceiptItemPrice mileageUsage = computeMileageUsage(mileage);

        return Receipt.builder()
            .memberId(memberId)
            .receiptStatus(getReceiptStatusByType(INPROGRESS))
            .originalTotalPrice(totalPrice)
            .mileageUsage(mileageUsage)
            .purchasedTotalPrice(totalPrice.subtract(mileageUsage))
            .totalQuantity(new ReceiptItemQuantity(cartItems.size()))
            .receiptItems(mapToReceiptItems(cartItems))
            .build();
    }

    private ReceiptItemPrice computeMileageUsage(final BigInteger mileage) {
        return new ReceiptItemPrice(mileage.divide(MILEAGE_USAGE_PERCENT));
    }

    private List<ReceiptItem> mapToReceiptItems(final List<CartItem> cartItems) {
        return cartItems.stream()
            .map(cartItem -> ReceiptItem.of(cartItem.getLiquor(), cartItem.getQuantity()))
            .collect(Collectors.toList());
    }

    private ReceiptItemPrice computeTotalPrice(final List<CartItem> cartItems) {
        BigInteger totalPrice = BigInteger.ZERO;

        for (CartItem cartItem : cartItems) {
            totalPrice = totalPrice.add(cartItem.getLiquorPrice());
        }

        return new ReceiptItemPrice(totalPrice);
    }

    private ReceiptStatus getReceiptStatusByType(final ReceiptStatusType type) {
        return receiptStatusRepository.findByType(type)
            .orElseThrow(() -> new SoolSoolException(NOT_RECEIPT_TYPE_FOUND));
    }
}
