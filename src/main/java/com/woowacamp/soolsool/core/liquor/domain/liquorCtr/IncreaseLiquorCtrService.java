package com.woowacamp.soolsool.core.liquor.domain.liquorCtr;

public interface IncreaseLiquorCtrService {

    void increaseImpression(final Long liquorId);

    void increaseClick(final Long liquorId);

    Double getCtr(final Long liquorId);
}
