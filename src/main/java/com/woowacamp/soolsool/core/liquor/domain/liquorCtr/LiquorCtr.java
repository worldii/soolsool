package com.woowacamp.soolsool.core.liquor.domain.liquorCtr;

import static javax.persistence.GenerationType.IDENTITY;

import com.woowacamp.soolsool.core.liquor.exception.LiquorCtrErrorCode;
import com.woowacamp.soolsool.global.common.BaseEntity;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "liquor_ctrs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LiquorCtr extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "liquor_id", nullable = false)
    @Getter
    private Long liquorId;

    @Column(name = "impression", nullable = false)
    @Convert(converter = LiquorCtrImpressionConverter.class)
    private LiquorCtrImpression impression;

    @Embedded
    private LiquorCtrClick click;

    public LiquorCtr(@NonNull final Long liquorId) {
        this(liquorId, 0L, 0L);
    }

    @Builder
    public LiquorCtr(
        @NonNull final Long liquorId,
        final Long impression,
        final Long click
    ) {
        this.liquorId = liquorId;
        this.impression = new LiquorCtrImpression(impression);
        this.click = new LiquorCtrClick(click);
    }

    public Double getCtr() {
        if (impression.getImpression() == 0) {
            throw new SoolSoolException(LiquorCtrErrorCode.DIVIDE_BY_ZERO_IMPRESSION);
        }

        final double ratio = (double) click.getCount() / impression.getImpression();

        return Math.round(ratio * 100) / 100.0;
    }

    public Long getImpression() {
        return impression.getImpression();
    }

    public Long getClick() {
        return click.getCount();
    }
}
