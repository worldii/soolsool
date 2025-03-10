package com.woowacamp.soolsool.core.liquor.api;

import com.woowacamp.soolsool.core.liquor.application.LiquorCtrService;
import com.woowacamp.soolsool.core.liquor.dto.liquorCtr.LiquorClickAddRequest;
import com.woowacamp.soolsool.core.liquor.dto.liquorCtr.LiquorCtrDetailResponse;
import com.woowacamp.soolsool.core.liquor.dto.liquorCtr.LiquorImpressionAddRequest;
import com.woowacamp.soolsool.core.liquor.exception.LiquorCtrResultCode;
import com.woowacamp.soolsool.core.member.dto.NoAuth;
import com.woowacamp.soolsool.global.aop.RequestLogging;
import com.woowacamp.soolsool.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/liquor-ctr")
@RequiredArgsConstructor
public class LiquorCtrController {

    private final LiquorCtrService liquorCtrService;

    @NoAuth
    @RequestLogging
    @GetMapping
    public ResponseEntity<ApiResponse<LiquorCtrDetailResponse>> findLiquorCtr(
            @RequestParam final Long liquorId
    ) {
        return ResponseEntity.ok(
                ApiResponse.of(LiquorCtrResultCode.FIND_LIQUOR_CTR_SUCCESS,
                        new LiquorCtrDetailResponse(liquorCtrService.getLiquorCtrByLiquorId(liquorId)))
        );
    }

    @NoAuth
    @RequestLogging
    @PatchMapping("/impressions")
    public ResponseEntity<ApiResponse<Void>> increaseImpression(
            @RequestBody final LiquorImpressionAddRequest request
    ) {
        liquorCtrService.increaseImpression(request);

        return ResponseEntity.ok(ApiResponse.from(LiquorCtrResultCode.INCREASE_IMPRESSION_SUCCESS));
    }

    @NoAuth
    @RequestLogging
    @PatchMapping("/clicks")
    public ResponseEntity<ApiResponse<Void>> increaseClick(
            @RequestBody final LiquorClickAddRequest request
    ) {
        liquorCtrService.increaseClick(request);

        return ResponseEntity.ok(ApiResponse.from(LiquorCtrResultCode.INCREASE_CLICK_SUCCESS));
    }
}
