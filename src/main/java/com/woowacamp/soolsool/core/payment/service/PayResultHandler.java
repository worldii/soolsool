package com.woowacamp.soolsool.core.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayResultHandler {

    private final ApplicationEventPublisher publisher;
}
