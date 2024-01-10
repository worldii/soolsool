package com.woowacamp.soolsool.core.liquor.infra;

import com.woowacamp.soolsool.core.liquor.domain.liquorCtr.IncreaseLiquorCtrService;
import com.woowacamp.soolsool.core.liquor.domain.liquorCtr.LiquorCtr;
import com.woowacamp.soolsool.core.liquor.domain.liquorCtr.LiquorCtrExpiredEvent;
import com.woowacamp.soolsool.core.liquor.domain.liquorCtr.LiquorCtrRepository;
import com.woowacamp.soolsool.core.liquor.exception.LiquorCtrErrorCode;
import com.woowacamp.soolsool.global.aop.DistributedLock;
import com.woowacamp.soolsool.global.common.DomainService;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.api.map.event.EntryExpiredListener;
import org.springframework.context.ApplicationEventPublisher;

import java.util.concurrent.TimeUnit;

@Slf4j
@DomainService
public class IncreaseRedisLiquorCtrService implements IncreaseLiquorCtrService {

    private static final String LIQUOR_CTR_KEY = "LIQUOR_CTR";
    private static final Long LIQUOR_CTR_TTL = 5L;

    private final LiquorCtrRepository liquorCtrRepository;
    private final RMapCache<Long, RedisLiquorCtr> liquorCtrs;

    public IncreaseRedisLiquorCtrService(
            final LiquorCtrRepository liquorCtrRepository,
            final RedissonClient redissonClient,
            final ApplicationEventPublisher publisher
    ) {
        publicCtrExpiredEvent(redissonClient, publisher);
        this.liquorCtrRepository = liquorCtrRepository;
        liquorCtrs = redissonClient.getMapCache(LIQUOR_CTR_KEY);
    }

    private void publicCtrExpiredEvent(
            final RedissonClient redissonClient, final ApplicationEventPublisher publisher
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
    }

    public Double getCtr(final Long liquorId) {
        return lookUpLiquorCtr(liquorId).toEntity(liquorId).getCtr();
    }

    @DistributedLock(lockName = "LIQUOR_CTR:", entityId = "#liquorId", waitTime = 1L, leaseTime = 1L)
    public void increaseImpression(final Long liquorId) {
        liquorCtrs.replace(liquorId, lookUpLiquorCtr(liquorId).increaseImpression());
    }

    @DistributedLock(lockName = "LIQUOR_CTR:", entityId = "#liquorId", waitTime = 1L, leaseTime = 1L)
    public void increaseClick(final Long liquorId) {
        liquorCtrs.replace(liquorId, lookUpLiquorCtr(liquorId).increaseClick());
    }

    // TODO: 만료 테스트는 어떻게 해야할까?
    private RedisLiquorCtr lookUpLiquorCtr(final Long liquorId) {
        if (!liquorCtrs.containsKey(liquorId)) {
            final LiquorCtr liquorCtr = findLiquorCtr(liquorId);

            liquorCtrs.put(
                    liquorId, new RedisLiquorCtr(liquorCtr.getImpression(), liquorCtr.getClick()),
                    LIQUOR_CTR_TTL, TimeUnit.MINUTES
            );
        }
        return liquorCtrs.get(liquorId);
    }

    private LiquorCtr findLiquorCtr(final Long liquorId) {
        return liquorCtrRepository.findByLiquorId(liquorId)
                .orElseThrow(() -> new SoolSoolException(LiquorCtrErrorCode.NOT_LIQUOR_CTR_FOUND));
    }
}
