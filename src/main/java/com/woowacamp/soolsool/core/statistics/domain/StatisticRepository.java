package com.woowacamp.soolsool.core.statistics.domain;

import org.springframework.stereotype.Repository;

@Repository
public interface StatisticRepository {

    StatisticLiquors findTop5LiquorsBySalePrice();

    StatisticLiquors findTop5LiquorsBySaleQuantity();

    void updateStatistic();
}
