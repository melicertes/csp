package com.intrasoft.csp.libraries.headersauth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class AuthorizationFilter extends OncePerRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationFilter.class);
    public static final String USER_HEADER = "Custom-User-Id";
    public static final String GROUP_HEADER = "Custom-User-Is-Member-Of";
    public static final String ADMIN_GROUP = "csp-admin";
    private final String USER_KEY = "user";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String userHeaderValue = request.getHeader(USER_HEADER);
        String groupHeaderValue = request.getHeader(GROUP_HEADER);

        LOG.info("-- userHeaderValue: "+userHeaderValue);
        LOG.info("-- groupHeaderValue: "+groupHeaderValue);

        if(StringUtils.isEmpty(userHeaderValue) || StringUtils.isEmpty(groupHeaderValue) || !groupHeaderValue.toLowerCase().contains(ADMIN_GROUP)){
//            response.setStatus(401);
//            response.getWriter().write("Unauthorized");//this will override the error page
                response.sendError(401,"Unauthorized");
        }else{
            User user = new User();
            user.setUsername(userHeaderValue);
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(groupHeaderValue));
            user.setAuthorities(authorities);
            user.setGroup(groupHeaderValue);

            Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            Boolean successfulRegistration = injectUsernameInLogs(userHeaderValue);

            try {
                filterChain.doFilter(request, response);
            } finally {
                if (successfulRegistration) {
                    MDC.remove(USER_KEY);
                }
            }

        }
    }

    private Boolean injectUsernameInLogs(String username) {
        try {
            String mdcData = String.format("[%s] ", username);
            if(!StringUtils.isEmpty(mdcData)) {
                MDC.put(USER_KEY, mdcData);
                return true;
            }
        } catch (Exception e) {
            LOG.error("Error injecting user header value in logs. ", e);
        }
        return false;
    }
}