package com.intrasoft.csp.anon.server.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

@Component
public class RequestFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        String currentUserName = "";

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName();
        }

        try {
            String mdcData = String.format("[%s] ", currentUserName);
            MDC.put("user", mdcData);
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    @Override
    public void destroy() {

    }
}