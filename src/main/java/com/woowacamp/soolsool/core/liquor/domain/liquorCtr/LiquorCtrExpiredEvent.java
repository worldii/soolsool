package com.woowacamp.soolsool.core.liquor.domain.liquorCtr;

import com.woowacamp.soolsool.core.liquor.infra.RedisLiquorCtr;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LiquorCtrExpiredEvent {

    private final Long liquorId;
    private final RedisLiquorCtr redisLiquorCtr;

    public LiquorCtr getLiquorCtr() {
        return redisLiquorCtr.toEntity(liquorId);
    }
}
