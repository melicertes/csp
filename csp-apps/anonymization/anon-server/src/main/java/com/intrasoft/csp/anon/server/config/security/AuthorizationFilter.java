package com.intrasoft.csp.anon.server.config.security;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthorizationFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        String tmp = "";
        if (request.getHeaderNames().hasMoreElements()){
            tmp = request.getHeaderNames().nextElement();
            System.out.println(tmp);
        }

        filterChain.doFilter(request, response);
    }
}