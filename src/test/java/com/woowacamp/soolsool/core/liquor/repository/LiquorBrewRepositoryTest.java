package com.woowacamp.soolsool.core.liquor.repository;

import static com.woowacamp.soolsool.core.liquor.domain.liquor.vo.LiquorBrewType.BERRY;
import static com.woowacamp.soolsool.core.liquor.exception.LiquorErrorCode.NOT_LIQUOR_BREW_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacamp.soolsool.core.liquor.domain.liquor.LiquorBrew;
import com.woowacamp.soolsool.core.liquor.domain.liquor.LiquorBrewRepository;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql("/liquor-type.sql")
@DisplayName("통합 테스트: LiquorBrewRepository")
class LiquorBrewRepositoryTest {

    @Autowired
    private LiquorBrewRepository liquorBrewRepository;

    @Test
    @DisplayName("LiquorBrew의 name를 가지고 LiquorBrew을 조회한다.")
    void findByLiquorBrew_type() {
        // given
        LiquorBrew 과실주 = liquorBrewRepository
            .findByType(BERRY)
            .orElseThrow(() -> new SoolSoolException(NOT_LIQUOR_BREW_FOUND));

        // when & then
        assertThat(과실주.getType().getName()).isEqualTo(BERRY.getName());
    }
}
