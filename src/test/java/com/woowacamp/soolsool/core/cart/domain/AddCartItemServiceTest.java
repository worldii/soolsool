package com.woowacamp.soolsool.core.cart.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacamp.soolsool.core.liquor.domain.liquor.Liquor;
import com.woowacamp.soolsool.core.liquor.domain.liquor.LiquorBrew;
import com.woowacamp.soolsool.core.liquor.domain.liquor.LiquorRegion;
import com.woowacamp.soolsool.core.liquor.domain.liquor.LiquorStatus;
import com.woowacamp.soolsool.core.liquor.domain.liquor.vo.LiquorBrewType;
import com.woowacamp.soolsool.core.liquor.domain.liquor.vo.LiquorRegionType;
import com.woowacamp.soolsool.core.liquor.domain.liquor.vo.LiquorStatusType;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("단위 테스트: Cart")
class AddCartItemServiceTest {

    private Liquor soju;
    private Liquor beer;

    @BeforeEach
    void setUpLiquor() {
        LiquorBrew sojuBrew = new LiquorBrew(LiquorBrewType.SOJU);
        LiquorBrew etcBrew = new LiquorBrew(LiquorBrewType.ETC);
        LiquorRegion gyeongSangNamDoRegion = new LiquorRegion(LiquorRegionType.GYEONGSANGNAM_DO);
        LiquorStatus onSaleStatus = new LiquorStatus(LiquorStatusType.ON_SALE);
        LiquorStatus stoppedStatus = new LiquorStatus(LiquorStatusType.STOPPED);

        soju = new Liquor(
            1L, sojuBrew, gyeongSangNamDoRegion, onSaleStatus,
            "안동 소주", "12000", "안동", "/soju.jpg",
            21.7, 400
        );
        beer = new Liquor(
            2L, etcBrew, gyeongSangNamDoRegion, stoppedStatus,
            "맥주", "5000", "OB", "/beer.jpg",
            5.7, 500
        );
    }

    @Test
    @DisplayName("장바구니의 memberId와 CartItem의 memberId가 다르면 예외를 던진다.")
    void sameMember() {
        // given
        List<CartItem> cartItems = List.of(
            new CartItem(1L, soju, 1),
            new CartItem(1L, beer, 1)
        );
        CartItem newCartItem = new CartItem(2L, soju, 1);

        // when & then
        assertThatThrownBy(() -> new AddCartItemService().addCartItem(cartItems, newCartItem))
            .isExactlyInstanceOf(SoolSoolException.class)
            .hasMessage("다른 사용자의 장바구니 상품을 가지고 있습니다.");
    }

    @Test
    @DisplayName("새로운 장바구니 상품을 추가할 때 100개를 초과하면 예외를 던진다.")
    void exceedMaxSize() {
        // given
        List<CartItem> cartItems = new ArrayList<>();
        for (long id = 1; id <= 100; id++) {
            // 생성 시 중복 검사를 하지 않으므로 편의상 같은 상품 반복 삽입
            cartItems.add(new CartItem(1L, soju, 1));
        }

        AddCartItemService addCartItemService = new AddCartItemService();

        CartItem newCartItem = new CartItem(1L, beer, 1);

        // when & then
        assertThatThrownBy(() -> addCartItemService.addCartItem(cartItems, newCartItem))
            .isExactlyInstanceOf(SoolSoolException.class)
            .hasMessage("장바구니가 가득 찼습니다.");
    }

    @Test
    @DisplayName("새로운 장바구니 상품을 추가할 때 기존에 존재하는 상품이라면 예외를 던진다.")
    void duplicate() {
        // given
        CartItem cartItem = new CartItem(1L, soju, 1);
        CartItem sameCartItem = new CartItem(1L, soju, 1);

        List<CartItem> cartItems = new ArrayList<>(List.of(cartItem));

        AddCartItemService addCartItemService = new AddCartItemService();

        // when & then
        assertThatThrownBy(() -> addCartItemService.addCartItem(cartItems, sameCartItem))
            .isExactlyInstanceOf(SoolSoolException.class)
            .hasMessage("장바구니에 이미 존재하는 상품입니다.");
    }

    @Test
    @DisplayName("새로운 장바구니 상품을 추가할 때 판매중지된 상품이라면 예외를 던진다.")
    void stoppedLiquor() {
        // given
        AddCartItemService addCartItemService = new AddCartItemService();

        Liquor stoppedLiquor = Liquor.builder()
            .brew(new LiquorBrew(LiquorBrewType.SOJU))
            .region(new LiquorRegion(LiquorRegionType.GYEONGSANGNAM_DO))
            .status(new LiquorStatus(LiquorStatusType.STOPPED))
            .name("안동 소주")
            .price("12000")
            .brand("안동")
            .imageUrl("/soju.jpg")
            .alcohol(21.7)
            .volume(400)
            .build();

        CartItem cartItem = new CartItem(1L, stoppedLiquor, 1);

        // when & then
        assertThatThrownBy(() -> addCartItemService.addCartItem(List.of(), cartItem))
            .isExactlyInstanceOf(SoolSoolException.class)
            .hasMessage("판매가 중지된 상품은 추가할 수 없습니다.");
    }
}
