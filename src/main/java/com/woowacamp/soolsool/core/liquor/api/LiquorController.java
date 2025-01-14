package com.woowacamp.soolsool.core.liquor.api;

import com.woowacamp.soolsool.core.liquor.application.LiquorCommandService;
import com.woowacamp.soolsool.core.liquor.application.LiquorQueryService;
import com.woowacamp.soolsool.core.liquor.dto.request.LiquorListRequest;
import com.woowacamp.soolsool.core.liquor.dto.request.LiquorModifyRequest;
import com.woowacamp.soolsool.core.liquor.dto.request.LiquorSaveRequest;
import com.woowacamp.soolsool.core.liquor.dto.response.LiquorDetailResponse;
import com.woowacamp.soolsool.core.liquor.dto.response.LiquorElementResponse;
import com.woowacamp.soolsool.core.liquor.dto.response.PageLiquorResponse;
import com.woowacamp.soolsool.core.liquor.dto.response.PageLiquorWithClickResponse;
import com.woowacamp.soolsool.core.liquor.exception.LiquorResultCode;
import com.woowacamp.soolsool.core.member.dto.NoAuth;
import com.woowacamp.soolsool.core.member.dto.Vendor;
import com.woowacamp.soolsool.global.aop.RequestLogging;
import com.woowacamp.soolsool.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static com.woowacamp.soolsool.core.liquor.exception.LiquorResultCode.*;

@RestController
@Slf4j
@RequestMapping("/liquors")
@RequiredArgsConstructor
public class LiquorController {

    private final LiquorCommandService liquorCommandService;
    private final LiquorQueryService liquorQueryService;

    @Vendor
    @RequestLogging
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> saveLiquor(
            @RequestBody final LiquorSaveRequest liquorSaveRequest
    ) {
        final Long saveLiquorId = liquorCommandService.saveLiquor(liquorSaveRequest);

        return ResponseEntity.created(URI.create("/liquors/" + saveLiquorId))
                .body(ApiResponse.from(LIQUOR_CREATED));
    }

    @NoAuth
    @RequestLogging
    @GetMapping("/{liquorId}")
    public ResponseEntity<ApiResponse<LiquorDetailResponse>> liquorDetail(
            @PathVariable final Long liquorId
    ) {
        final LiquorDetailResponse response = liquorQueryService.liquorDetail(liquorId);

        return ResponseEntity.ok(ApiResponse.of(LiquorResultCode.LIQUOR_DETAIL_FOUND, response));
    }

    @NoAuth
    @RequestLogging
    @GetMapping("/{liquorId}/related")
    public ResponseEntity<ApiResponse<List<LiquorElementResponse>>> liquorPurchasedTogether(
            @PathVariable final Long liquorId
    ) {
        final List<LiquorElementResponse> response = liquorQueryService.liquorPurchasedTogether(
                liquorId);

        return ResponseEntity.ok(
                ApiResponse.of(LiquorResultCode.LIQUOR_PURCHASED_TOGETHER_FOUND, response));
    }

    @NoAuth
    @RequestLogging
    @GetMapping("/first")
    public ResponseEntity<ApiResponse<PageLiquorResponse>> getLiquorFirstList(
            @PageableDefault final Pageable pageable
    ) {
        final PageRequest sortPageable = getSortedPageable(pageable);

        final PageLiquorResponse response = liquorQueryService.getFirstPage(sortPageable);

        return ResponseEntity.ok(ApiResponse.of(LIQUOR_LIST_FOUND, response));
    }

    @NoAuth
    @RequestLogging
    @GetMapping
    public ResponseEntity<ApiResponse<PageLiquorResponse>> liquorList(
            @ModelAttribute final LiquorListRequest liquorListRequest,
            @PageableDefault final Pageable pageable
    ) {
        final PageRequest sortPageable = getSortedPageable(pageable);

        final PageLiquorResponse response = liquorQueryService
                .liquorListByLatest(liquorListRequest, sortPageable);

        return ResponseEntity.ok(ApiResponse.of(LIQUOR_LIST_FOUND, response));
    }

    @NoAuth
    @RequestLogging
    @GetMapping("/click")
    public ResponseEntity<ApiResponse<PageLiquorWithClickResponse>> liquorListByClick(
            @ModelAttribute final LiquorListRequest liquorListRequest,
            @PageableDefault final Pageable pageable
    ) {
        final PageRequest sortPageable = getSortedPageable(pageable);

        final PageLiquorWithClickResponse response = liquorQueryService
                .liquorListByClick(liquorListRequest, sortPageable);

        return ResponseEntity.ok(ApiResponse.of(LIQUOR_LIST_FOUND, response));
    }

    private PageRequest getSortedPageable(Pageable pageable) {
        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdAt").descending()
        );
    }

    @Vendor
    @RequestLogging
    @PutMapping("/{liquorId}")
    public ResponseEntity<ApiResponse<Void>> modifyLiquor(
            @PathVariable final Long liquorId,
            @RequestBody final LiquorModifyRequest liquorModifyRequest
    ) {
        liquorCommandService.modifyLiquor(liquorId, liquorModifyRequest);

        return ResponseEntity.ok(ApiResponse.from(LIQUOR_UPDATED));
    }

    @Vendor
    @RequestLogging
    @DeleteMapping("/{liquorId}")
    public ResponseEntity<ApiResponse<Void>> deleteLiquor(
            @PathVariable final Long liquorId
    ) {
        liquorCommandService.deleteLiquor(liquorId);

        return ResponseEntity.ok().body(ApiResponse.from(LIQUOR_DELETED));
    }
}
