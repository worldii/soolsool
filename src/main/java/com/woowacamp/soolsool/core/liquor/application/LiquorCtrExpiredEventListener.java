package com.woowacamp.soolsool.core.liquor.application;

import com.woowacamp.soolsool.core.liquor.domain.liquorCtr.LiquorCtrExpiredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LiquorCtrExpiredEventListener {

    private final LiquorCtrService liquorCtrService;

    @Async
    @EventListener
    public void expiredListener(final LiquorCtrExpiredEvent event) {
        liquorCtrService.writeBackCtr(event.getLiquorCtr());
    }
}
