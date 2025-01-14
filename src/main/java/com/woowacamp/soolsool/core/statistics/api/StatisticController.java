package com.woowacamp.soolsool.core.statistics.api;

import com.woowacamp.soolsool.core.member.dto.NoAuth;
import com.woowacamp.soolsool.core.statistics.application.StatisticService;
import com.woowacamp.soolsool.core.statistics.dto.response.LiquorSalePriceResponse;
import com.woowacamp.soolsool.core.statistics.dto.response.LiquorSaleQuantityResponse;
import com.woowacamp.soolsool.global.aop.RequestLogging;
import com.woowacamp.soolsool.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.woowacamp.soolsool.core.statistics.exception.StatisticsResultCode.STATISTIC_TOP5_SALE_PRICE;
import static com.woowacamp.soolsool.core.statistics.exception.StatisticsResultCode.STATISTIC_TOP5_SALE_QUANTITY;
import static org.springframework.http.HttpStatus.OK;

@RestController
@Slf4j
@RequestMapping("/statistic")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    @NoAuth
    @RequestLogging
    @GetMapping("/price")
    public ResponseEntity<ApiResponse<List<LiquorSalePriceResponse>>> findTop5LiquorsBySalePrice() {
        final List<LiquorSalePriceResponse> liquorSalePriceResponses
                = statisticService.findTop5LiquorsBySalePrice();

        return ResponseEntity.status(OK)
                .body(ApiResponse.of(STATISTIC_TOP5_SALE_PRICE, liquorSalePriceResponses));
    }

    @NoAuth
    @RequestLogging
    @GetMapping("/quantity")
    public ResponseEntity<ApiResponse<List<LiquorSaleQuantityResponse>>> findTop5LiquorsBySaleQuantity() {
        final List<LiquorSaleQuantityResponse> liquorSaleQuantityResponses
                = statisticService.findTop5LiquorsBySaleQuantity();

        return ResponseEntity.status(OK)
                .body(ApiResponse.of(STATISTIC_TOP5_SALE_QUANTITY, liquorSaleQuantityResponses));
    }
}
