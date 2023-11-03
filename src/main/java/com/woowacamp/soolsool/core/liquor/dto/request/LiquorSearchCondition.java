package com.woowacamp.soolsool.core.liquor.dto.request;

import com.woowacamp.soolsool.core.liquor.domain.liquor.LiquorBrew;
import com.woowacamp.soolsool.core.liquor.domain.liquor.LiquorRegion;
import com.woowacamp.soolsool.core.liquor.domain.liquor.LiquorStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LiquorSearchCondition {

    private final LiquorRegion liquorRegion;
    private final LiquorBrew liquorBrew;
    private final LiquorStatus liquorStatus;
    private final String brand;
}
