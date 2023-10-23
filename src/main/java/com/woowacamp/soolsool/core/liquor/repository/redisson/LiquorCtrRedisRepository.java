package com.woowacamp.soolsool.core.liquor.repository.redisson;

import com.woowacamp.soolsool.core.liquor.code.LiquorCtrErrorCode;
import com.woowacamp.soolsool.core.liquor.domain.LiquorCtr;
import com.woowacamp.soolsool.core.liquor.event.LiquorCtrExpiredEvent;
import com.woowacamp.soolsool.core.liquor.infra.RedisLiquorCtr;
import com.woowacamp.soolsool.core.liquor.repository.LiquorCtrRepository;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import com.woowacamp.soolsool.global.aop.DistributedLock;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.api.map.event.EntryExpiredListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LiquorCtrRedisRepository {

    // TODO: ~Repository vs ~Service
    private static final String LIQUOR_CTR_KEY = "LIQUOR_CTR";
    private static final long LIQUOR_CTR_TTL = 5L;

    private final LiquorCtrRepository liquorCtrRepository;

    private final RMapCache<Long, RedisLiquorCtr> liquorCtrs;

    public LiquorCtrRedisRepository(
        final LiquorCtrRepository liquorCtrRepository,
        final RedissonClient redissonClient,
        final ApplicationEventPublisher publisher
    ) {
        redissonClient.getMapCache(LIQUOR_CTR_KEY)
            .addListener((EntryExpiredListener<Long, RedisLiquorCtr>) event ->
                publisher.publishEvent(
                    new LiquorCtrExpiredEvent(
                        event.getKey(),
                        event.getValue()
                    )
                )
            );

        this.liquorCtrRepository = liquorCtrRepository;
        liquorCtrs = redissonClient.getMapCache(LIQUOR_CTR_KEY);
    }

    public double getCtr(final Long liquorId) {
        return lookUpLiquorCtr(liquorId).toEntity(liquorId).getCtr();
    }

    @DistributedLock(lockName = "LIQUOR_CTR:", entityId ="#liquorId",waitTime = 1L,leaseTime = 1L)
    public void increaseImpression(final Long liquorId) {
        liquorCtrs.replace(liquorId, lookUpLiquorCtr(liquorId).increaseImpression());
    }

    @DistributedLock(lockName = "LIQUOR_CTR:", entityId ="#liquorId",waitTime = 1L,leaseTime = 1L)
    public void increaseClick(final Long liquorId) {
        liquorCtrs.replace(liquorId, lookUpLiquorCtr(liquorId).increaseClick());
    }

    // TODO: 만료 테스트는 어떻게 해야할까?
    private RedisLiquorCtr lookUpLiquorCtr(final Long liquorId) {
        if (!liquorCtrs.containsKey(liquorId)) {
            final LiquorCtr liquorCtr = liquorCtrRepository.findByLiquorId(liquorId)
                .orElseThrow(() -> new SoolSoolException(LiquorCtrErrorCode.NOT_LIQUOR_CTR_FOUND));

            liquorCtrs.put(
                liquorId,
                new RedisLiquorCtr(liquorCtr.getImpression(), liquorCtr.getClick()),
                LIQUOR_CTR_TTL,
                TimeUnit.MINUTES
            );
        }

        return liquorCtrs.get(liquorId);
    }
}
