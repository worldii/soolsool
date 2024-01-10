package com.woowacamp.soolsool.core.auth.exception;

import com.woowacamp.soolsool.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    TOKEN_ERROR(BAD_REQUEST.value(), "A101", "토큰이 유효하지 않습니다."),
    INVALID_AUTHORITY(UNAUTHORIZED.value(), "A102", "권한이 없습니다."),
    ;

    private final int status;
    private final String code;
    private final String message;
}
