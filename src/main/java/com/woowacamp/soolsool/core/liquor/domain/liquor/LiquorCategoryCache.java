package com.woowacamp.soolsool.core.liquor.domain.liquor;

import com.woowacamp.soolsool.core.liquor.domain.liquor.vo.LiquorBrewType;
import com.woowacamp.soolsool.core.liquor.domain.liquor.vo.LiquorRegionType;
import com.woowacamp.soolsool.core.liquor.domain.liquor.vo.LiquorStatusType;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LiquorCategoryCache {

    private final LiquorRegionRepository liquorRegionRepository;
    private final LiquorStatusRepository liquorStatusRepository;
    private final LiquorBrewRepository liquorBrewRepository;

    @Cacheable(value = "liquorRegion", key = "#type", condition = "#type!=null",
        unless = "#result==null", cacheManager = "caffeineCacheManager")
    public Optional<LiquorRegion> findByType(final LiquorRegionType type) {
        log.info("LiquorRegionCache {}", type);
        return liquorRegionRepository.findByType(type);
    }

    @Cacheable(value = "liquorStatus", key = "#type", condition = "#type!=null",
        unless = "#result==null", cacheManager = "caffeineCacheManager")
    public Optional<LiquorStatus> findByType(final LiquorStatusType type) {
        log.info("LiquorStatusCache {}", type);
        return liquorStatusRepository.findByType(type);
    }

    @Cacheable(value = "liquorBrew", key = "#type", condition = "#type!=null",
        unless = "#result==null", cacheManager = "caffeineCacheManager")
    public Optional<LiquorBrew> findByType(final LiquorBrewType type) {
        log.info("LiquorBrewCache {}", type);
        return liquorBrewRepository.findByType(type);
    }
}
