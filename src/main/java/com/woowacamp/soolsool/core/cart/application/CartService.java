package com.woowacamp.soolsool.core.cart.application;

import static com.woowacamp.soolsool.core.cart.exception.CartErrorCode.NOT_EQUALS_MEMBER;
import static com.woowacamp.soolsool.core.cart.exception.CartErrorCode.NOT_FOUND_CART_ITEM;
import static com.woowacamp.soolsool.core.cart.exception.CartErrorCode.NOT_FOUND_LIQUOR;

import com.woowacamp.soolsool.core.cart.domain.AddCartItemService;
import com.woowacamp.soolsool.core.cart.domain.CartItem;
import com.woowacamp.soolsool.core.cart.domain.CartItemRepository;
import com.woowacamp.soolsool.core.cart.dto.request.CartItemModifyRequest;
import com.woowacamp.soolsool.core.cart.dto.request.CartItemSaveRequest;
import com.woowacamp.soolsool.core.cart.dto.response.CartItemResponse;
import com.woowacamp.soolsool.core.liquor.domain.liquor.Liquor;
import com.woowacamp.soolsool.core.liquor.domain.liquor.LiquorRepository;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final LiquorRepository liquorRepository;
    private final AddCartItemService addCartItemService;

    @Transactional
    public Long addCartItem(final Long memberId, final CartItemSaveRequest request) {
        final Liquor liquor = findLiquor(request.getLiquorId());
        final CartItem newCartItem = CartItem.builder()
            .memberId(memberId)
            .liquor(liquor)
            .quantity(request.getQuantity())
            .build();
        final List<CartItem> cartItems = cartItemRepository
            .findAllByMemberIdOrderByCreatedAtDesc(memberId);

        newCartItem.addCartItem(addCartItemService, cartItems);

        return cartItemRepository.save(newCartItem).getId();
    }

    private Liquor findLiquor(final Long liquorId) {
        return liquorRepository.findById(liquorId)
            .orElseThrow(() -> new SoolSoolException(NOT_FOUND_LIQUOR));
    }

    @Transactional(readOnly = true)
    public List<CartItemResponse> cartItemList(final Long memberId) {
        final List<CartItem> cartItems = cartItemRepository
            .findAllByMemberIdOrderByCreatedAtDescWithLiquor(memberId);

        return cartItems.stream()
            .map(CartItemResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional
    public void modifyCartItemQuantity(
        final Long memberId,
        final Long cartItemId,
        final CartItemModifyRequest cartItemModifyRequest
    ) {
        final CartItem cartItem = findCartItem(cartItemId);

        validateMemberId(memberId, cartItem.getMemberId());

        cartItem.updateQuantity(cartItemModifyRequest.getLiquorQuantity());
    }

    private CartItem findCartItem(final Long cartItemId) {
        return cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new SoolSoolException(NOT_FOUND_CART_ITEM));
    }

    @Transactional
    public void removeCartItem(final Long memberId, final Long cartItemId) {
        final CartItem cartItem = findCartItem(cartItemId);

        validateMemberId(memberId, cartItem.getMemberId());

        cartItemRepository.delete(cartItem);
    }

    private void validateMemberId(final Long memberId, final Long cartItemMemberId) {
        if (!Objects.equals(cartItemMemberId, memberId)) {
            throw new SoolSoolException(NOT_EQUALS_MEMBER);
        }
    }

    @Transactional
    public void removeCartItems(final Long memberId) {
        cartItemRepository.deleteAllByMemberId(memberId);
    }
}
