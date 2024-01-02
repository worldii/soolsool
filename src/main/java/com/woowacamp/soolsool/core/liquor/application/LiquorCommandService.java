package com.woowacamp.soolsool.core.liquor.application;

import static com.woowacamp.soolsool.core.liquor.exception.LiquorErrorCode.NOT_LIQUOR_BREW_FOUND;
import static com.woowacamp.soolsool.core.liquor.exception.LiquorErrorCode.NOT_LIQUOR_FOUND;
import static com.woowacamp.soolsool.core.liquor.exception.LiquorErrorCode.NOT_LIQUOR_REGION_FOUND;
import static com.woowacamp.soolsool.core.liquor.exception.LiquorErrorCode.NOT_LIQUOR_STATUS_FOUND;

import com.woowacamp.soolsool.core.liquor.domain.liquor.Liquor;
import com.woowacamp.soolsool.core.liquor.domain.liquor.LiquorBrew;
import com.woowacamp.soolsool.core.liquor.domain.liquor.LiquorCategoryCache;
import com.woowacamp.soolsool.core.liquor.domain.liquor.LiquorRegion;
import com.woowacamp.soolsool.core.liquor.domain.liquor.LiquorRepository;
import com.woowacamp.soolsool.core.liquor.domain.liquor.LiquorStatus;
import com.woowacamp.soolsool.core.liquor.domain.liquor.vo.LiquorBrewType;
import com.woowacamp.soolsool.core.liquor.domain.liquor.vo.LiquorRegionType;
import com.woowacamp.soolsool.core.liquor.domain.liquor.vo.LiquorStatusType;
import com.woowacamp.soolsool.core.liquor.domain.liquorCtr.LiquorCtr;
import com.woowacamp.soolsool.core.liquor.domain.liquorCtr.LiquorCtrRepository;
import com.woowacamp.soolsool.core.liquor.dto.request.LiquorModifyRequest;
import com.woowacamp.soolsool.core.liquor.dto.request.LiquorSaveRequest;
import com.woowacamp.soolsool.global.aop.DistributedLock;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LiquorCommandService {

    private final LiquorRepository liquorRepository;
    private final LiquorCategoryCache liquorCategoryCache;
    private final LiquorCtrRepository liquorCtrRepository;

    @CacheEvict(value = "liquorsFirstPage")
    @Transactional
    public Long saveLiquor(final LiquorSaveRequest request) {
        final LiquorBrew liquorBrew = getLiquorBrew(request.getBrew());
        final LiquorRegion liquorRegion = getLiquorRegion(request.getRegion());
        final LiquorStatus liquorStatus = getLiquorStatus(request.getStatus());

        final Liquor liquor = liquorRepository.save(
            request.toEntity(liquorBrew, liquorRegion, liquorStatus));

        return liquorCtrRepository.save(new LiquorCtr(liquor.getId())).getLiquorId();
    }

    @CacheEvict(value = "liquorsFirstPage")
    @Transactional
    public void modifyLiquor(final Long liquorId, final LiquorModifyRequest request) {
        final Liquor liquor = findLiquor(liquorId);
        final LiquorBrew modifyLiquorBrew = getLiquorBrew(request.getTypeName());
        final LiquorRegion modifyLiquorRegion = getLiquorRegion(request.getRegionName());
        final LiquorStatus modifyLiquorStatus = getLiquorStatus(request.getStatusName());

        liquor.update(modifyLiquorBrew, modifyLiquorRegion, modifyLiquorStatus, request);
    }

    @CacheEvict(value = "liquorsFirstPage")
    @Transactional
    public void deleteLiquor(final Long liquorId) {
        final Liquor liquor = findLiquor(liquorId);

        liquorRepository.delete(liquor);
    }

    @CacheEvict(value = "liquorsFirstPage")
    @Transactional
    @DistributedLock(entityId = "#liquorId", lockName = "Liquor")
    public void decreaseTotalStock(final Long liquorId, final int quantity) {
        findLiquor(liquorId).decreaseTotalStock(quantity);
    }

    private Liquor findLiquor(Long liquorId) {
        return liquorRepository.findById(liquorId)
            .orElseThrow(() -> new SoolSoolException(NOT_LIQUOR_FOUND));
    }

    private LiquorStatus getLiquorStatus(final String name) {
        return liquorCategoryCache.findByType(LiquorStatusType.valueOf(name))
            .orElseThrow(() -> new SoolSoolException(NOT_LIQUOR_STATUS_FOUND));
    }

    private LiquorRegion getLiquorRegion(final String name) {
        return liquorCategoryCache.findByType(LiquorRegionType.valueOf(name))
            .orElseThrow(() -> new SoolSoolException(NOT_LIQUOR_REGION_FOUND));
    }

    private LiquorBrew getLiquorBrew(final String name) {
        return liquorCategoryCache.findByType(LiquorBrewType.valueOf(name))
            .orElseThrow(() -> new SoolSoolException(NOT_LIQUOR_BREW_FOUND));
    }
}
