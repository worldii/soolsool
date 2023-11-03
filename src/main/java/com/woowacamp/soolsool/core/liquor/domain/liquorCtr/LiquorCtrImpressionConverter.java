package com.woowacamp.soolsool.core.liquor.domain.liquorCtr;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class LiquorCtrImpressionConverter implements AttributeConverter<LiquorCtrImpression, Long> {

    @Override
    public Long convertToDatabaseColumn(final LiquorCtrImpression impression) {
        return impression.getImpression();
    }

    @Override
    public LiquorCtrImpression convertToEntityAttribute(final Long dbData) {
        return new LiquorCtrImpression(dbData);
    }
}
