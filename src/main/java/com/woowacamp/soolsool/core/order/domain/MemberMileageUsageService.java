package com.woowacamp.soolsool.core.order.domain;

import com.woowacamp.soolsool.core.member.domain.Member;
import com.woowacamp.soolsool.core.member.domain.MemberRepository;
import com.woowacamp.soolsool.core.order.exception.OrderErrorCode;
import com.woowacamp.soolsool.global.common.DomainService;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;

@DomainService
@RequiredArgsConstructor
public class MemberMileageUsageService {
    private final MemberMileageUsageRepository memberMileageUsageRepository;
    private final MemberRepository memberRepository;

    public void addMemberMileageUsage(final Order order, final Long memberId, final BigInteger amount) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new SoolSoolException(OrderErrorCode.NOT_EXISTS_MEMBER));

        memberMileageUsageRepository.save(new MemberMileageUsage(member, order, amount));
    }
}
