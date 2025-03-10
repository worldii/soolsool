package com.woowacamp.soolsool.core.statistics.application;

import com.woowacamp.soolsool.core.statistics.domain.StatisticLiquors;
import com.woowacamp.soolsool.core.statistics.domain.StatisticRepository;
import com.woowacamp.soolsool.core.statistics.dto.response.LiquorSalePriceResponse;
import com.woowacamp.soolsool.core.statistics.dto.response.LiquorSaleQuantityResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StatisticService {

    private final StatisticRepository statisticRepository;

    @Transactional(readOnly = true)
    public List<LiquorSalePriceResponse> findTop5LiquorsBySalePrice() {
        final StatisticLiquors statisticLiquors = statisticRepository.findTop5LiquorsBySalePrice();

        return statisticLiquors.getValues().stream()
            .map(LiquorSalePriceResponse::from)
            .collect(Collectors.toUnmodifiableList());
    }

    @Transactional(readOnly = true)
    public List<LiquorSaleQuantityResponse> findTop5LiquorsBySaleQuantity() {
        final StatisticLiquors statisticLiquors = statisticRepository.findTop5LiquorsBySaleQuantity();

        return statisticLiquors.getValues().stream()
            .map(LiquorSaleQuantityResponse::from)
            .collect(Collectors.toUnmodifiableList());
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateStatistic() {
        statisticRepository.updateStatistic();
    }
}
