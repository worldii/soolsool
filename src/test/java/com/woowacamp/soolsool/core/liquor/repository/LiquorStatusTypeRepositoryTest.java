package com.woowacamp.soolsool.core.liquor.repository;

import static com.woowacamp.soolsool.core.liquor.domain.liquor.vo.LiquorStatusType.ON_SALE;
import static com.woowacamp.soolsool.core.liquor.exception.LiquorErrorCode.NOT_LIQUOR_STATUS_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacamp.soolsool.core.liquor.domain.liquor.LiquorStatus;
import com.woowacamp.soolsool.core.liquor.domain.liquor.LiquorStatusRepository;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql("/liquor-type.sql")
@DisplayName("통합 테스트: LiquorStatusRepository")
class LiquorStatusTypeRepositoryTest {

    @Autowired
    private LiquorStatusRepository liquorStatusRepository;

    @Test
    @DisplayName("LiquorStatus의 name를 가지고 LiquorStatus을 조회한다.")
    void findByLiquorRegionType_type() {
        // given
        LiquorStatus 판매중 = liquorStatusRepository.findByType(ON_SALE)
            .orElseThrow(() -> new SoolSoolException(NOT_LIQUOR_STATUS_FOUND));

        // when & then
        assertThat(판매중.getType().getStatus()).isEqualTo(ON_SALE.getStatus());
    }
}
