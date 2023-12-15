package com.woowacamp.soolsool.core.liquor.domain.liquor.vo;

import com.woowacamp.soolsool.core.liquor.exception.LiquorErrorCode;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class LiquorAlcohol {

    private final double alcohol;

    public LiquorAlcohol(final double alcohol) {
        validateIsGreaterThanZero(alcohol);

        this.alcohol = alcohol;
    }

    private void validateIsGreaterThanZero(final double alcohol) {
        if (alcohol < 0) {
            throw new SoolSoolException(LiquorErrorCode.INVALID_SIZE_ALCOHOL);
        }
    }
}
