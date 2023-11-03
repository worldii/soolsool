package com.woowacamp.soolsool.core.liquor.application;

import static com.woowacamp.soolsool.core.liquor.code.LiquorErrorCode.NOT_LIQUOR_FOUND;

import com.woowacamp.soolsool.core.liquor.domain.liquor.Liquor;
import com.woowacamp.soolsool.core.liquor.domain.liquor.LiquorRepository;
import com.woowacamp.soolsool.core.liquor.domain.stock.DecreaseStocksService;
import com.woowacamp.soolsool.core.liquor.domain.stock.LiquorStock;
import com.woowacamp.soolsool.core.liquor.domain.stock.LiquorStockRepository;
import com.woowacamp.soolsool.core.liquor.dto.request.LiquorStockSaveRequest;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LiquorStockService {

    private final LiquorRepository liquorRepository;
    private final LiquorStockRepository liquorStockRepository;
    private final DecreaseStocksService decreaseStocksService;

    @Transactional
    public Long saveLiquorStock(final LiquorStockSaveRequest request) {
        final Liquor liquor = findLiquor(request.getLiquorId());

        liquor.increaseTotalStock(request.getStock());

        return liquorStockRepository.save(request.toEntity()).getId();
    }

    @Transactional
    public void decreaseLiquorStock(final Long liquorId, final int quantity) {
        final List<LiquorStock> stocks = liquorStockRepository
            .findAllByLiquorIdNotExpired(liquorId);

        decreaseStocksService.decreaseStock(stocks, quantity);

        liquorStockRepository.deleteAllInBatch(getOutOfStocks(stocks));
    }

    private Liquor findLiquor(final Long liquorId) {
        return liquorRepository.findById(liquorId)
            .orElseThrow(() -> new SoolSoolException(NOT_LIQUOR_FOUND));
    }

    private List<LiquorStock> getOutOfStocks(final List<LiquorStock> stocks) {
        return stocks.stream()
            .filter(LiquorStock::isOutOfStock)
            .collect(Collectors.toUnmodifiableList());
    }
}
