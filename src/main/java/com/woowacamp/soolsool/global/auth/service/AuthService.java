package com.woowacamp.soolsool.global.auth.service;

import static com.woowacamp.soolsool.core.member.exception.MemberErrorCode.MEMBER_NO_INFORMATION;
import static com.woowacamp.soolsool.core.member.exception.MemberErrorCode.MEMBER_NO_MATCH_PASSWORD;

import com.woowacamp.soolsool.core.member.domain.Member;
import com.woowacamp.soolsool.core.member.domain.vo.MemberEmail;
import com.woowacamp.soolsool.core.member.repository.MemberRepository;
import com.woowacamp.soolsool.global.auth.dto.LoginRequest;
import com.woowacamp.soolsool.global.auth.dto.LoginResponse;
import com.woowacamp.soolsool.global.auth.util.TokenProvider;
import com.woowacamp.soolsool.global.exception.SoolSoolException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public LoginResponse createToken(final LoginRequest loginRequest) {
        final Member member = memberRepository.findByEmail(new MemberEmail(loginRequest.getEmail()))
            .orElseThrow(() -> new SoolSoolException(MEMBER_NO_INFORMATION));

        if (!member.matchPassword(loginRequest.getPassword())) {
            throw new SoolSoolException(MEMBER_NO_MATCH_PASSWORD);
        }

        return new LoginResponse(tokenProvider.createToken(member));
    }
}
