package com.woowacamp.soolsool.core.member.domain;

import com.woowacamp.soolsool.core.member.domain.vo.MemberRoleType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRoleRepository extends JpaRepository<MemberRole, Long> {

    Optional<MemberRole> findByName(final MemberRoleType name);
}
