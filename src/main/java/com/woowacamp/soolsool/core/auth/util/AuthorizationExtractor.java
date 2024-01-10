package com.woowacamp.soolsool.core.auth.util;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class AuthorizationExtractor {

    private static final String EXTRACT_TYPE = "Bearer ";

    public String extractToken(final HttpServletRequest request) {
        final Enumeration<String> headers = request.getHeaders(AUTHORIZATION);

        while (headers.hasMoreElements()) {
            final String token = headers.nextElement();
            if (token.toLowerCase().startsWith(EXTRACT_TYPE.toLowerCase())) {
                return token.substring(EXTRACT_TYPE.length()).trim();
            }
        }

        return Strings.EMPTY;
    }
}
