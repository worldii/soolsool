package com.woowacamp.soolsool.core.liquor.domain.liquor;

import com.woowacamp.soolsool.core.liquor.domain.liquor.vo.LiquorStatusType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "liquor_status")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LiquorStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, length = 20)
    private LiquorStatusType type;

    public LiquorStatus(final LiquorStatusType type) {
        this.type = type;
    }
}
