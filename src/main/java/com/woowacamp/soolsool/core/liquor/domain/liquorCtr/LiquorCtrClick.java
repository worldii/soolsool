package com.woowacamp.soolsool.core.liquor.domain.liquorCtr;

import com.woowacamp.soolsool.core.liquor.exception.LiquorCtrErrorCode;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class LiquorCtrClick {

    @Column(name = "click", nullable = false)
    private Long count;

    public LiquorCtrClick(final Long Click) {
        validateIsNotNull(Click);
        validateIsNotLessThanZero(Click);

        this.count = Click;
    }

    private void validateIsNotNull(final Long Click) {
        if (Objects.isNull(Click)) {
            throw new SoolSoolException(LiquorCtrErrorCode.NO_CONTENT_CLICK);
        }
    }

    private void validateIsNotLessThanZero(final Long Click) {
        if (Click < 0) {
            throw new SoolSoolException(LiquorCtrErrorCode.INVALID_SIZE_CLICK);
        }
    }
}
