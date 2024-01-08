package com.woowacamp.soolsool.core.order.domain;

import com.woowacamp.soolsool.core.member.domain.Member;
import com.woowacamp.soolsool.core.member.domain.converter.MemberMileageConverter;
import com.woowacamp.soolsool.core.member.domain.vo.MemberMileage;
import com.woowacamp.soolsool.global.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "member_mileage_usages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberMileageUsage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "amount", nullable = false)
    @Convert(converter = MemberMileageConverter.class)
    private MemberMileage amount;

    @Builder
    public MemberMileageUsage(
            final Member member,
            final Order order,
            final BigInteger amount
    ) {
        this.member = member;
        this.order = order;
        this.amount = new MemberMileage(amount);
    }
}
