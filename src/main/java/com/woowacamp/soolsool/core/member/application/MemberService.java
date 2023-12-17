package com.woowacamp.soolsool.core.member.application;

import static com.woowacamp.soolsool.core.member.exception.MemberErrorCode.MEMBER_DUPLICATED_EMAIL;
import static com.woowacamp.soolsool.core.member.exception.MemberErrorCode.MEMBER_NO_INFORMATION;
import static com.woowacamp.soolsool.core.member.exception.MemberErrorCode.MEMBER_NO_ROLE_TYPE;
import static com.woowacamp.soolsool.core.member.exception.MemberErrorCode.NOT_FOUND_RECEIPT;

import com.woowacamp.soolsool.core.member.domain.Member;
import com.woowacamp.soolsool.core.member.domain.MemberMileageCharge;
import com.woowacamp.soolsool.core.member.domain.MemberMileageUsage;
import com.woowacamp.soolsool.core.member.domain.MemberRole;
import com.woowacamp.soolsool.core.member.domain.vo.MemberEmail;
import com.woowacamp.soolsool.core.member.domain.vo.MemberRoleType;
import com.woowacamp.soolsool.core.member.dto.request.MemberAddRequest;
import com.woowacamp.soolsool.core.member.dto.request.MemberMileageChargeRequest;
import com.woowacamp.soolsool.core.member.dto.request.MemberModifyRequest;
import com.woowacamp.soolsool.core.member.dto.response.MemberDetailResponse;
import com.woowacamp.soolsool.core.member.exception.MemberErrorCode;
import com.woowacamp.soolsool.core.member.repository.MemberMileageChargeRepository;
import com.woowacamp.soolsool.core.member.repository.MemberMileageUsageRepository;
import com.woowacamp.soolsool.core.member.repository.MemberRepository;
import com.woowacamp.soolsool.core.member.repository.MemberRoleCache;
import com.woowacamp.soolsool.core.order.domain.Order;
import com.woowacamp.soolsool.global.aop.DistributedLock;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberRoleCache memberRoleRepository;
    private final MemberMileageChargeRepository memberMileageChargeRepository;
    private final MemberMileageUsageRepository memberMileageUsageRepository;

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
    @DistributedLock(lockName = "memberMileageCharge", entityId = "#memberId", waitTime = 3L, leaseTime = 3L)
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
    public void subtractMemberMileage(
        final Long memberId,
        final Order order,
        final BigInteger mileageUsage
    ) {
        final Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new SoolSoolException(NOT_FOUND_RECEIPT));

        member.decreaseMileage(mileageUsage);

        memberMileageUsageRepository.save(new MemberMileageUsage(member, order, mileageUsage));
    }
}
