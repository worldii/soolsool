package com.woowacamp.soolsool.core.liquor.domain.stock;

import com.woowacamp.soolsool.core.liquor.code.LiquorStockErrorCode;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import java.util.List;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
public final class DecreaseStocksService {

    public void decreaseStock(final List<LiquorStock> stocks, final int quantity) {
        validateEmptyLiquor(stocks);
        validateEnoughStocks(stocks, quantity);

        int total = quantity;
        for (LiquorStock liquorStock : stocks) {
            if (total == 0) {
                break;
            }
            int target = Math.min(liquorStock.getStock(), quantity);
            total -= target;
            liquorStock.decreaseStock(target);
        }
    }

    private void validateEmptyLiquor(final List<LiquorStock> liquorStocks) {
        if (liquorStocks.isEmpty()) {
            throw new SoolSoolException(LiquorStockErrorCode.EMPTY_LIQUOR_STOCKS);
        }
    }

    private void validateEnoughStocks(final List<LiquorStock> liquorStocks, final int quantity) {
        final int totalStock = liquorStocks.stream()
            .mapToInt(LiquorStock::getStock)
            .sum();

        if (totalStock < quantity) {
            throw new SoolSoolException(LiquorStockErrorCode.NOT_ENOUGH_LIQUOR_STOCKS);
        }
    }
}
