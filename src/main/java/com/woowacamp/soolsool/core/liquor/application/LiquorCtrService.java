package com.woowacamp.soolsool.core.liquor.application;

import com.woowacamp.soolsool.core.liquor.domain.liquorCtr.IncreaseLiquorCtrService;
import com.woowacamp.soolsool.core.liquor.domain.liquorCtr.LiquorCtr;
import com.woowacamp.soolsool.core.liquor.domain.liquorCtr.LiquorCtrRepository;
import com.woowacamp.soolsool.core.liquor.dto.liquorCtr.LiquorClickAddRequest;
import com.woowacamp.soolsool.core.liquor.dto.liquorCtr.LiquorImpressionAddRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LiquorCtrService {

    private final LiquorCtrRepository liquorCtrRepository;
    private final IncreaseLiquorCtrService increaseLiquorCtrService;

    public double getLiquorCtrByLiquorId(final Long liquorId) {
        return increaseLiquorCtrService.getCtr(liquorId);
    }

    public void increaseImpression(final LiquorImpressionAddRequest request) {
        request.getLiquorIds().forEach(increaseLiquorCtrService::increaseImpression);
    }

    public void increaseClick(final LiquorClickAddRequest request) {
        increaseLiquorCtrService.increaseClick(request.getLiquorId());
    }

    @Transactional
    public void writeBackCtr(final LiquorCtr latestLiquorCtr) {
        liquorCtrRepository.updateLiquorCtr(
            latestLiquorCtr.getImpression(),
            latestLiquorCtr.getClick(),
            latestLiquorCtr.getLiquorId()
        );
    }
}
