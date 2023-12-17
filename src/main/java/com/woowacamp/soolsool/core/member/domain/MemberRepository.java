package com.woowacamp.soolsool.core.member.domain;

import com.woowacamp.soolsool.core.member.domain.vo.MemberEmail;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(final MemberEmail email);
}
