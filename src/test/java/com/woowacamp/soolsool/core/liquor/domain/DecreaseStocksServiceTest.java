package com.woowacamp.soolsool.core.liquor.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.woowacamp.soolsool.core.liquor.domain.stock.DecreaseStocksService;
import com.woowacamp.soolsool.core.liquor.domain.stock.LiquorStock;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("단위 테스트: LiquorStocks")
class DecreaseStocksServiceTest {
    
    @Test
    @DisplayName("상품 재고가 없을 경우 SoolSoolException을 던진다.")
    void createFailWithEmpty() {
        /* given */
        int quantity = 3;

        /* when & then */
        assertThatThrownBy(
            () -> new DecreaseStocksService().decreaseStock(Collections.emptyList(), quantity))
            .isExactlyInstanceOf(SoolSoolException.class)
            .hasMessage("주류 재고가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("재고보다 더 많이 주문할 경우 SoolSoolException을 던진다.")
    void decrease() {
        /* given */
        LiquorStock 재고1 = mock(LiquorStock.class);
        when(재고1.getStock()).thenReturn(0);

        /* when & then */
        assertThatThrownBy(() -> new DecreaseStocksService().decreaseStock(List.of(재고1), 12345))
            .isExactlyInstanceOf(SoolSoolException.class)
            .hasMessage("주류 재고가 부족합니다.");
    }
}
