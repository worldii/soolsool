package com.woowacamp.soolsool.core.liquor.domain.liquorCtr;

import com.woowacamp.soolsool.core.liquor.domain.liquor.vo.LiquorCtrClick;
import javax.persistence.AttributeConverter;

public class LiquorCtrClickConverter implements AttributeConverter<LiquorCtrClick, Long> {

    @Override
    public Long convertToDatabaseColumn(final LiquorCtrClick click) {
        return click.getCount();
    }

    @Override
    public LiquorCtrClick convertToEntityAttribute(final Long dbData) {
        return new LiquorCtrClick(dbData);
    }
}
