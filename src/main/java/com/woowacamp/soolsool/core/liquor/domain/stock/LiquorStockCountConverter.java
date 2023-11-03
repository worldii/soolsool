package com.woowacamp.soolsool.core.liquor.domain.stock;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class LiquorStockCountConverter implements AttributeConverter<LiquorStockCount, Integer> {

    @Override
    public Integer convertToDatabaseColumn(final LiquorStockCount stock) {
        return stock.getStock();
    }

    @Override
    public LiquorStockCount convertToEntityAttribute(final Integer dbData) {
        return new LiquorStockCount(dbData);
    }
}
