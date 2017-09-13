package com.intrasoft.csp.anon.server.config.security;

import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
class AuthenticationService {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationService.class);

    static final String HEADER_STRING = "Cookie";

    @Autowired
    RetryRestTemplate restTemplate;

    static void addAuthentication(HttpServletResponse res, String username) {

    }

    static Authentication getAuthentication(HttpServletRequest request) throws  IOException {
        String token = request.getHeader(HEADER_STRING);





        return null;
    }
}