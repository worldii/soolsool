package com.woowacamp.soolsool.core.liquor.domain.liquor;

import static com.woowacamp.soolsool.core.liquor.domain.liquor.QLiquor.liquor;
import static com.woowacamp.soolsool.core.statistics.domain.QStatistic.statistic;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.woowacamp.soolsool.core.liquor.domain.liquor.vo.LiquorBrand;
import com.woowacamp.soolsool.core.liquor.dto.request.LiquorSearchCondition;
import com.woowacamp.soolsool.core.liquor.dto.response.LiquorClickElementDto;
import com.woowacamp.soolsool.core.statistics.domain.vo.Click;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LiquorQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public List<Liquor> getList(
        final LiquorSearchCondition condition,
        final Pageable pageable,
        final Long liquorId
    ) {
        return queryFactory.select(liquor)
            .from(liquor)
            .where(
                eqRegion(condition.getLiquorRegion()),
                eqBrew(condition.getLiquorBrew()),
                eqStatus(condition.getLiquorStatus()),
                eqBrand(condition.getBrand()),
                cursorId(liquorId, null)
            )
            .orderBy(liquor.id.desc())
            .limit(pageable.getPageSize())
            .fetch();
    }

    @Cacheable(value = "liquorsFirstPage")
    public List<Liquor> getCachedList(
        final Pageable pageable
    ) {
        log.info("LiquorQueryDslRepository getCachedList");
        return queryFactory.select(liquor)
            .from(liquor)
            .orderBy(liquor.id.desc())
            .limit(pageable.getPageSize())
            .fetch();
    }

    public List<LiquorClickElementDto> getListByClick(
        final LiquorSearchCondition condition,
        final Pageable pageable,
        final Long liquorId,
        final Long clickCount,
        final LocalDateTime dataTime
    ) {
        return queryFactory.select(
                Projections.constructor(
                    LiquorClickElementDto.class,
                    liquor,
                    statistic.click
                )
            )
            .from(liquor)
            .join(statistic)
            .on(liquor.id.eq(statistic.statisticId.liquorId))
            .where(
                statistic.statisticId.year.eq(dataTime.getYear()),
                statistic.statisticId.month.eq(dataTime.getMonthValue()),
                statistic.statisticId.week.eq(dataTime.getDayOfWeek().getValue()),
                statistic.statisticId.day.eq(dataTime.getDayOfMonth()),
                eqRegion(condition.getLiquorRegion()),
                eqBrew(condition.getLiquorBrew()),
                eqStatus(condition.getLiquorStatus()),
                eqBrand(condition.getBrand()),
                cursorId(liquorId, clickCount)
            )
            .orderBy(statistic.click.count.desc(), liquor.id.desc())
            .limit(pageable.getPageSize())
            .fetch();
    }

    private BooleanExpression eqRegion(final LiquorRegion liquorRegion) {
        if (Objects.isNull(liquorRegion)) {
            return null;
        }
        return liquor.region.eq(liquorRegion);
    }

    private BooleanExpression eqBrew(final LiquorBrew liquorBrew) {
        if (Objects.isNull(liquorBrew)) {
            return null;
        }
        return liquor.brew.eq(liquorBrew);
    }

    private BooleanExpression eqStatus(final LiquorStatus liquorStatus) {
        if (Objects.isNull(liquorStatus)) {
            return null;
        }
        return liquor.status.eq(liquorStatus);
    }

    private BooleanExpression eqBrand(final String brand) {
        if (brand == null) {
            return null;
        }
        return liquor.brand.eq(new LiquorBrand(brand));
    }

    private BooleanExpression cursorId(final Long liquorId, final Long click) {
        if (liquorId == null) {
            return null;
        }
        if (click == null) {
            return liquor.id.lt(liquorId);
        }

        return statistic.click.count.lt(click)
            .or(statistic.click.eq(new Click(new BigInteger(click.toString())))
                .and(liquor.id.lt(liquorId)));
    }
}
