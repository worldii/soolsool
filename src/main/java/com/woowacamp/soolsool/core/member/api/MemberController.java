package com.woowacamp.soolsool.core.member.api;

import com.woowacamp.soolsool.core.member.application.MemberService;
import com.woowacamp.soolsool.core.member.dto.LoginUser;
import com.woowacamp.soolsool.core.member.dto.NoAuth;
import com.woowacamp.soolsool.core.member.dto.request.MemberAddRequest;
import com.woowacamp.soolsool.core.member.dto.request.MemberMileageChargeRequest;
import com.woowacamp.soolsool.core.member.dto.request.MemberModifyRequest;
import com.woowacamp.soolsool.core.member.dto.response.MemberDetailResponse;
import com.woowacamp.soolsool.core.member.exception.MemberResultCode;
import com.woowacamp.soolsool.global.aop.RequestLogging;
import com.woowacamp.soolsool.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @NoAuth
    @RequestLogging
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addMember(
            @RequestBody @Valid final MemberAddRequest memberAddRequest
    ) {
        memberService.addMember(memberAddRequest);

        return ResponseEntity.status(OK)
                .body(ApiResponse.from(MemberResultCode.MEMBER_CREATE_SUCCESS));
    }

    @RequestLogging
    @GetMapping
    public ResponseEntity<ApiResponse<MemberDetailResponse>> findMemberDetails(
            @LoginUser final Long memberId
    ) {
        final MemberDetailResponse memberDetailResponse = memberService.findMember(memberId);

        return ResponseEntity.status(OK)
                .body(ApiResponse.of(MemberResultCode.MEMBER_FIND_SUCCESS, memberDetailResponse));
    }

    @RequestLogging
    @PatchMapping
    public ResponseEntity<ApiResponse<Void>> modifyMember(
            @LoginUser final Long memberId,
            @RequestBody @Valid final MemberModifyRequest memberModifyRequest
    ) {
        memberService.modifyMember(memberId, memberModifyRequest);

        return ResponseEntity.status(OK)
                .body(ApiResponse.from(MemberResultCode.MEMBER_MODIFY_SUCCESS));
    }

    @RequestLogging
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @LoginUser final Long memberId
    ) {
        memberService.removeMember(memberId);

        return ResponseEntity.status(NO_CONTENT)
                .body(ApiResponse.from(MemberResultCode.MEMBER_DELETE_SUCCESS));
    }

    @RequestLogging
    @PatchMapping("/mileage")
    public ResponseEntity<ApiResponse<Void>> addMemberMileage(
            @LoginUser final Long memberId,
            @RequestBody @Valid final MemberMileageChargeRequest memberMileageChargeRequest
    ) {
        memberService.addMemberMileage(memberId, memberMileageChargeRequest);

        return ResponseEntity.status(OK)
                .body(ApiResponse.from(MemberResultCode.MEMBER_MILEAGE_CHARGE_SUCCESS));
    }
}
