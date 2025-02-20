package com.woowacamp.soolsool.core.liquor.domain.liquor;

import com.woowacamp.soolsool.core.liquor.domain.liquor.vo.LiquorStatusType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LiquorStatusRepository extends JpaRepository<LiquorStatus, Long> {

    Optional<LiquorStatus> findByType(final LiquorStatusType type);
}
