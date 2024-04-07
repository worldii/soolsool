package com.woowacamp.soolsool.core.receipt.application;

import static com.woowacamp.soolsool.core.receipt.exception.ReceiptErrorCode.ACCESS_DENIED_RECEIPT;
import static com.woowacamp.soolsool.core.receipt.exception.ReceiptErrorCode.NOT_EQUALS_MEMBER;
import static com.woowacamp.soolsool.core.receipt.exception.ReceiptErrorCode.NOT_FOUND_RECEIPT;
import static com.woowacamp.soolsool.core.receipt.exception.ReceiptErrorCode.NOT_RECEIPT_FOUND;

import com.woowacamp.soolsool.core.cart.domain.CartItem;
import com.woowacamp.soolsool.core.cart.domain.CartItemRepository;
import com.woowacamp.soolsool.core.member.domain.Member;
import com.woowacamp.soolsool.core.member.domain.MemberRepository;
import com.woowacamp.soolsool.core.receipt.domain.Receipt;
import com.woowacamp.soolsool.core.receipt.domain.ReceiptMapper;
import com.woowacamp.soolsool.core.receipt.domain.ReceiptStatus;
import com.woowacamp.soolsool.core.receipt.domain.repository.ReceiptRepository;
import com.woowacamp.soolsool.core.receipt.domain.repository.ReceiptStatusCache;
import com.woowacamp.soolsool.core.receipt.domain.repository.redisson.ReceiptRedisRepository;
import com.woowacamp.soolsool.core.receipt.domain.vo.ReceiptStatusType;
import com.woowacamp.soolsool.core.receipt.dto.response.ReceiptDetailResponse;
import com.woowacamp.soolsool.core.receipt.exception.ReceiptErrorCode;
import com.woowacamp.soolsool.global.aop.DistributedLock;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private static final int RECEIPT_EXPIRED_MINUTES = 5;
    private final ReceiptMapper receiptMapper;
    private final ReceiptRepository receiptRepository;
    private final ReceiptStatusCache receiptStatusCache;
    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;

    private final ReceiptRedisRepository receiptRedisRepository;


    @Transactional
    public Long addReceipt(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new SoolSoolException(ReceiptErrorCode.MEMBER_NO_INFORMATION));

        final List<CartItem> cartItems = cartItemRepository.findAllByMemberId(memberId);

        final Long receiptId = receiptRepository.save(
            receiptMapper.mapFrom(memberId, cartItems, member.getMileage())).getId();

        receiptRedisRepository.addExpiredEvent(receiptId, memberId, RECEIPT_EXPIRED_MINUTES);

        return receiptId;
    }

    @Transactional(readOnly = true)
    public ReceiptDetailResponse findReceipt(final Long memberId, final Long receiptId) {
        final Receipt receipt = getReceipt(receiptId);

        if (!Objects.equals(receipt.getMemberId(), memberId)) {
            throw new SoolSoolException(NOT_EQUALS_MEMBER);
        }

        return ReceiptDetailResponse.from(receipt);
    }

    @Transactional
    @DistributedLock(lockName = "Receipt", entityId = "#receiptId")
    public void modifyReceiptStatus(
        final Long memberId,
        final Long receiptId,
        final ReceiptStatusType receiptStatusType
    ) {

        final Receipt receipt = getReceipt(receiptId);

        if (!Objects.equals(receipt.getMemberId(), memberId)) {
            throw new SoolSoolException(NOT_EQUALS_MEMBER);
        }

        if (receipt.isNotInProgress()) {
            throw new SoolSoolException(ReceiptErrorCode.UNMODIFIABLE_STATUS);
        }

        receipt.updateStatus(getReceiptStatus(receiptStatusType));
    }

    private Receipt getReceipt(final Long receiptId) {
        return receiptRepository.findById(receiptId)
            .orElseThrow(() -> new SoolSoolException(NOT_RECEIPT_FOUND));
    }

    private ReceiptStatus getReceiptStatus(final ReceiptStatusType receiptStatusType) {
        return receiptStatusCache.findByType(receiptStatusType)
            .orElseThrow(() -> new SoolSoolException(ReceiptErrorCode.NOT_RECEIPT_TYPE_FOUND));
    }

    @Transactional(readOnly = true)
    public Receipt getMemberReceipt(final Long memberId, final Long receiptId) {
        final Receipt receipt = receiptRepository.findById(receiptId)
            .orElseThrow(() -> new SoolSoolException(NOT_FOUND_RECEIPT));
        validateAccessibleReceipt(memberId, receipt);

        return receipt;
    }

    private void validateAccessibleReceipt(final Long memberId, final Receipt receipt) {
        if (!Objects.equals(memberId, receipt.getMemberId())) {
            throw new SoolSoolException(ACCESS_DENIED_RECEIPT);
        }
    }
}
