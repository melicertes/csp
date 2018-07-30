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
            filterChain.doFilter(request, response);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String currentUserName = "";
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName();
        }

        try {
            String mdcData = String.format("[%s] ", currentUserName);
            MDC.put("user", mdcData);
        } finally {
            MDC.clear();
        }
    }
}