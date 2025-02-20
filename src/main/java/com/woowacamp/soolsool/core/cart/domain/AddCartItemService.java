package com.woowacamp.soolsool.core.cart.domain;

import com.woowacamp.soolsool.global.common.DomainService;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

import static com.woowacamp.soolsool.core.cart.exception.CartErrorCode.*;

@DomainService
@Getter
public class AddCartItemService {

    private static final int MAX_CART_SIZE = 100;

    public void addCartItem(final List<CartItem> cartItems, final CartItem newCartItem) {

        validateMember(newCartItem.getMemberId(), cartItems);
        validateNull(newCartItem);
        validateExceedMaxSize(cartItems);
        validateDuplicated(cartItems, newCartItem);
        validateLiquorStatus(newCartItem);

        cartItems.add(newCartItem);
    }

    private void validateMember(final Long memberId, final List<CartItem> cartItems) {
        if (cartItems.stream().anyMatch(cartItem -> cartItem.hasDifferentMemberIdWith(memberId))) {
            throw new SoolSoolException(NOT_EQUALS_MEMBER);
        }
    }

    private void validateNull(final CartItem newCartItem) {
        if (Objects.isNull(newCartItem)) {
            throw new SoolSoolException(NULL_LIQUOR);
        }
    }

    private void validateExceedMaxSize(final List<CartItem> cartItems) {
        if (cartItems.size() == MAX_CART_SIZE) {
            throw new SoolSoolException(EXCEED_MAX_CART_SIZE);
        }
    }

    private void validateDuplicated(final List<CartItem> cartItems, final CartItem newCartItem) {
        if (cartItems.stream().anyMatch(newCartItem::hasSameLiquorWith)) {
            throw new SoolSoolException(EXISTS_CART_ITEM);
        }
    }

    private void validateLiquorStatus(final CartItem newCartItem) {
        if (newCartItem.hasStoppedLiquor()) {
            throw new SoolSoolException(STOPPED_LIQUOR);
        }
    }
}
