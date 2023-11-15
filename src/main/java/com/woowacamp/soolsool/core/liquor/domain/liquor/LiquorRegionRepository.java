package com.woowacamp.soolsool.core.liquor.domain.liquor;

import com.woowacamp.soolsool.core.liquor.domain.liquor.vo.LiquorRegionType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LiquorRegionRepository extends JpaRepository<LiquorRegion, Long> {

    Optional<LiquorRegion> findByType(final LiquorRegionType type);
}
