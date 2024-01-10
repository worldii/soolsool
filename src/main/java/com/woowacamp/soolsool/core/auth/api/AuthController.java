package com.woowacamp.soolsool.core.auth.api;

import com.woowacamp.soolsool.core.auth.application.AuthService;
import com.woowacamp.soolsool.core.auth.dto.LoginRequest;
import com.woowacamp.soolsool.core.auth.dto.LoginResponse;
import com.woowacamp.soolsool.core.auth.exception.AuthResultCode;
import com.woowacamp.soolsool.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            final HttpServletRequest httpServletRequest,
            @RequestBody final LoginRequest loginRequest
    ) {
        log.info("{} {} | request : {}",
                httpServletRequest.getMethod(), httpServletRequest.getServletPath(), loginRequest);

        final LoginResponse token = authService.createToken(loginRequest);

        return ResponseEntity.ok(ApiResponse.of(AuthResultCode.LOGIN_SUCCESS, token));
    }
}
