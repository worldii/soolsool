package com.woowacamp.soolsool.core.liquor.dto.request;

import com.woowacamp.soolsool.core.liquor.domain.stock.LiquorStock;
import com.woowacamp.soolsool.core.liquor.domain.stock.LiquorStockCount;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class LiquorStockSaveRequest {

    private final Long liquorId;
    private final Integer stock;
    private final LocalDateTime expiredAt;

    public LiquorStock toEntity() {
        return LiquorStock.builder()
            .liquorId(liquorId)
            .stock(new LiquorStockCount(stock))
            .expiredAt(expiredAt)
            .build();
    }
}
