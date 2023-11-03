package com.woowacamp.soolsool.core.liquor.application;

import static com.woowacamp.soolsool.core.liquor.code.LiquorErrorCode.NOT_LIQUOR_FOUND;

import com.woowacamp.soolsool.core.liquor.domain.liquor.Liquor;
import com.woowacamp.soolsool.core.liquor.domain.liquor.LiquorBrew;
import com.woowacamp.soolsool.core.liquor.domain.liquor.LiquorCategoryCache;
import com.woowacamp.soolsool.core.liquor.domain.liquor.LiquorQueryDslRepository;
import com.woowacamp.soolsool.core.liquor.domain.liquor.LiquorRegion;
import com.woowacamp.soolsool.core.liquor.domain.liquor.LiquorRepository;
import com.woowacamp.soolsool.core.liquor.domain.liquor.LiquorStatus;
import com.woowacamp.soolsool.core.liquor.dto.request.LiquorListRequest;
import com.woowacamp.soolsool.core.liquor.dto.request.LiquorSearchCondition;
import com.woowacamp.soolsool.core.liquor.dto.response.LiquorClickElementDto;
import com.woowacamp.soolsool.core.liquor.dto.response.LiquorClickElementResponse;
import com.woowacamp.soolsool.core.liquor.dto.response.LiquorDetailResponse;
import com.woowacamp.soolsool.core.liquor.dto.response.LiquorElementResponse;
import com.woowacamp.soolsool.core.liquor.dto.response.PageLiquorResponse;
import com.woowacamp.soolsool.core.liquor.dto.response.PageLiquorWithClickResponse;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LiquorQueryService {

    private static final PageRequest TOP_RANK_PAGEABLE = PageRequest.of(0, 5);
    private final LiquorRepository liquorRepository;
    private final LiquorCategoryCache liquorCategoryCache;
    private final LiquorQueryDslRepository liquorQueryDslRepository;

    public LiquorDetailResponse liquorDetail(final Long liquorId) {
        return LiquorDetailResponse.of(findLiquor(liquorId));
    }

    public List<LiquorElementResponse> liquorPurchasedTogether(final Long liquorId) {
        final List<Liquor> relatedLiquors = liquorRepository
            .findLiquorsPurchasedTogether(liquorId, TOP_RANK_PAGEABLE);

        return relatedLiquors.stream()
            .map(LiquorElementResponse::from)
            .collect(Collectors.toList());
    }

    public PageLiquorWithClickResponse liquorListByClick(
        final LiquorListRequest liquorListRequest,
        final Pageable pageable
    ) {
        final LiquorSearchCondition liquorSearchCondition = new LiquorSearchCondition(
            getLiquorRegion(liquorListRequest), getLiquorBrew(liquorListRequest),
            getLiquorStatus(liquorListRequest), liquorListRequest.getBrand()
        );

        final List<LiquorClickElementDto> liquors = liquorQueryDslRepository.getListByClick(
            liquorSearchCondition, pageable, liquorListRequest.getLiquorId(),
            liquorListRequest.getClickCount(), LocalDateTime.now()
        );

        return PageLiquorWithClickResponse.of(pageable, getLiquorElementResponseFromClick(liquors));
    }

    private List<LiquorClickElementResponse> getLiquorElementResponseFromClick(
        final List<LiquorClickElementDto> liquors
    ) {
        return liquors.stream().map(LiquorClickElementResponse::from).collect(Collectors.toList());
    }

    public PageLiquorResponse liquorListByLatest(
        final LiquorListRequest liquorListRequest, final Pageable pageable
    ) {
        final LiquorSearchCondition liquorSearchCondition = new LiquorSearchCondition(
            getLiquorRegion(liquorListRequest), getLiquorBrew(liquorListRequest),
            getLiquorStatus(liquorListRequest), liquorListRequest.getBrand()
        );

        final List<Liquor> liquors = liquorQueryDslRepository.getList(
            liquorSearchCondition, pageable, liquorListRequest.getLiquorId());

        return PageLiquorResponse.of(pageable, getLiquorElementsFromLiquor(liquors));
    }

    @Cacheable(value = "liquorsFirstPage")
    public PageLiquorResponse getFirstPage(final Pageable pageable) {
        log.info("LiquorQueryDslRepository getCachedList");
        final List<Liquor> liquors = liquorQueryDslRepository.getCachedList(pageable);

        return PageLiquorResponse.of(pageable, getLiquorElementsFromLiquor(liquors));
    }

    private Liquor findLiquor(final Long liquorId) {
        return liquorRepository.findById(liquorId)
            .orElseThrow(() -> new SoolSoolException(NOT_LIQUOR_FOUND));
    }

    private LiquorBrew getLiquorBrew(final LiquorListRequest liquorListRequest) {
        return liquorCategoryCache.findByType(liquorListRequest.getBrew()).orElse(null);
    }

    private LiquorStatus getLiquorStatus(final LiquorListRequest liquorListRequest) {
        return liquorCategoryCache.findByType(liquorListRequest.getStatus()).orElse(null);
    }

    private LiquorRegion getLiquorRegion(final LiquorListRequest liquorListRequest) {
        return liquorCategoryCache.findByType(liquorListRequest.getRegion()).orElse(null);
    }

    private List<LiquorElementResponse> getLiquorElementsFromLiquor(final List<Liquor> liquors) {
        return liquors.stream()
            .map(LiquorElementResponse::from)
            .collect(Collectors.toList());
    }
}
