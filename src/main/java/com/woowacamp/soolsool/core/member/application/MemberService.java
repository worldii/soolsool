package com.woowacamp.soolsool.core.member.application;

import com.woowacamp.soolsool.core.member.domain.*;
import com.woowacamp.soolsool.core.member.domain.vo.MemberEmail;
import com.woowacamp.soolsool.core.member.domain.vo.MemberRoleType;
import com.woowacamp.soolsool.core.member.dto.request.MemberAddRequest;
import com.woowacamp.soolsool.core.member.dto.request.MemberMileageChargeRequest;
import com.woowacamp.soolsool.core.member.dto.request.MemberModifyRequest;
import com.woowacamp.soolsool.core.member.dto.response.MemberDetailResponse;
import com.woowacamp.soolsool.core.member.exception.MemberErrorCode;
import com.woowacamp.soolsool.global.aop.DistributedLock;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static com.woowacamp.soolsool.core.member.exception.MemberErrorCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberRoleCache memberRoleRepository;
    private final MemberMileageChargeRepository memberMileageChargeRepository;

    @Transactional
    public void addMember(final MemberAddRequest memberAddRequest) {
        checkDuplicatedEmail(memberAddRequest.getEmail());

        final MemberRole memberRole = getMemberRole(memberAddRequest.getMemberRoleType());
        final Member member = memberAddRequest.toMember(memberRole);

        memberRepository.save(member);
    }

    private void checkDuplicatedEmail(final String email) {
        final Optional<Member> duplicatedEmil = memberRepository.findByEmail(
                new MemberEmail(email));

        if (duplicatedEmil.isPresent()) {
            throw new SoolSoolException(MEMBER_DUPLICATED_EMAIL);
        }
    }

    private MemberRole getMemberRole(final String memberRequestRoleType) {
        final MemberRoleType memberRoleType = Arrays.stream(MemberRoleType.values())
                .filter(type -> Objects.equals(type.getType(), memberRequestRoleType))
                .findFirst()
                .orElse(MemberRoleType.CUSTOMER);

        return memberRoleRepository.findByName(memberRoleType)
                .orElseThrow(() -> new SoolSoolException(MEMBER_NO_ROLE_TYPE));
    }

    @Transactional(readOnly = true)
    public MemberDetailResponse findMember(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new SoolSoolException(MEMBER_NO_INFORMATION));

        return MemberDetailResponse.from(member);
    }

    @Transactional
    public void modifyMember(final Long memberId, final MemberModifyRequest memberModifyRequest) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new SoolSoolException(MEMBER_NO_INFORMATION));

        member.update(memberModifyRequest);
    }

    @Transactional
    public void removeMember(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new SoolSoolException(MemberErrorCode.MEMBER_NO_INFORMATION));

        memberRepository.delete(member);
    }

    @Transactional
    @DistributedLock(lockName = "Member", entityId = "#memberId", waitTime = 3L, leaseTime = 3L)
    public void addMemberMileage(
            final Long memberId,
            final MemberMileageChargeRequest memberMileageChargeRequest
    ) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new SoolSoolException(MEMBER_NO_INFORMATION));

        member.updateMileage(memberMileageChargeRequest.getAmount());

        final MemberMileageCharge memberMileageCharge = memberMileageChargeRequest.toMemberMileageCharge(
                member);

        memberMileageChargeRepository.save(memberMileageCharge);
    }

    @Transactional
    @DistributedLock(lockName = "Member", entityId = "#memberId", waitTime = 3L, leaseTime = 3L)
    public void subtractMemberMileage(
            final Long memberId,
            final BigInteger mileageUsage
    ) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new SoolSoolException(NOT_FOUND_RECEIPT));

        member.decreaseMileage(mileageUsage);
    }
}
