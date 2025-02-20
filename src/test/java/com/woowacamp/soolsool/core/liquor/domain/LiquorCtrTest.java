package com.woowacamp.soolsool.core.liquor.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.woowacamp.soolsool.core.liquor.domain.liquorCtr.LiquorCtr;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("단위 테스트: LiquorCtr")
class LiquorCtrTest {

    @Test
    @DisplayName("LiquorCtr을 정상 생성한다.")
    void create() {
        /* given */


        /* when & then */
        assertThatCode(() -> new LiquorCtr(1L))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("LiquorCtr에 liquorId가 null이면 예외가 발생한다.")
    void createFailWithOutNullLiquorId() {
        /* given */


        /* when & then */
        assertThatCode(() -> LiquorCtr.builder().build())
            .isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("클릭률을 구한다")
    void getCtr() {
        /* given */
        LiquorCtr liquorCtr = new LiquorCtr(1L, 2L, 1L);

        /* when & then */
        assertThat(liquorCtr.getCtr()).isEqualTo(0.5);
    }
}
