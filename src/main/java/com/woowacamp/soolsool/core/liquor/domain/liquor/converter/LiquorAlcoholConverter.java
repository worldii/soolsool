package com.woowacamp.soolsool.core.liquor.domain.liquor.converter;

import com.woowacamp.soolsool.core.liquor.domain.liquor.vo.LiquorAlcohol;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class LiquorAlcoholConverter implements AttributeConverter<LiquorAlcohol, Double> {

    @Override
    public Double convertToDatabaseColumn(final LiquorAlcohol alcohol) {
        return alcohol.getAlcohol();
    }

    @Override
    public LiquorAlcohol convertToEntityAttribute(final Double dbData) {
        return new LiquorAlcohol(dbData);
    }
}
