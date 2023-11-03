package com.woowacamp.soolsool.core.liquor.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
public class PageLiquorWithClickResponse {

    private final boolean hasNext;
    private final Long nextCursorId;
    private final List<LiquorClickElementResponse> liquors;

    @JsonCreator
    public PageLiquorWithClickResponse(
        final boolean hasNext,
        final Long nextCursorId,
        final List<LiquorClickElementResponse> liquors
    ) {
        this.hasNext = hasNext;
        this.nextCursorId = nextCursorId;
        this.liquors = liquors;
    }

    public static PageLiquorWithClickResponse of(
        final Pageable pageable,
        final List<LiquorClickElementResponse> liquors
    ) {
        if (liquors.size() < pageable.getPageSize()) {
            return new PageLiquorWithClickResponse(false, liquors);
        }

        final Long lastReadLiquorId = liquors.get(liquors.size() - 1).getId();

        return new PageLiquorWithClickResponse(true, lastReadLiquorId, liquors);
    }

    private PageLiquorWithClickResponse(final boolean hasNext,
        final List<LiquorClickElementResponse> liquors) {
        this(hasNext, null, liquors);
    }

}
