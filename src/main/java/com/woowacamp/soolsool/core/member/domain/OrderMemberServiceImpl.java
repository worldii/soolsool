package com.woowacamp.soolsool.core.member.domain;

import com.woowacamp.soolsool.core.member.exception.MemberErrorCode;
import com.woowacamp.soolsool.core.order.domain.OrderMemberService;
import com.woowacamp.soolsool.global.aop.DistributedLock;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Component
@RequiredArgsConstructor
public class OrderMemberServiceImpl implements OrderMemberService {

    private final MemberRepository memberRepository;

    @Transactional
    @DistributedLock(lockName = "Member", entityId = "#memberId", waitTime = 3L, leaseTime = 3L)
    public void refundMileage(final Long memberId, final BigInteger mileage) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new SoolSoolException(MemberErrorCode.MEMBER_NO_INFORMATION));

        member.updateMileage(mileage);
    }
}
