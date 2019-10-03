package com.intrasoft.csp.vcb.teleconf.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    Logger log = LoggerFactory.getLogger(CustomLogoutSuccessHandler.class);

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        if (authentication != null && authentication.getDetails() != null) {
            try {
                request.getSession().invalidate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Set the Server Status
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);

        //redirect to login
        String uidString = request.getParameter("uid");

        //Check if no parameters have been passed
        if (uidString == null) {
            httpServletResponse.sendRedirect("/error");
        } else {
            try {
                UUID uuid = UUID.fromString(uidString);
                if (uuid.toString().contentEquals(uidString)) {
                    httpServletResponse.sendRedirect("/?uid=" + uidString);
                } else {
                    throw new Exception("uid passed is not a valid UUID");
                }
            } catch (Exception e) {
                log.error("Invalid UUID passed through - ",e);
                httpServletResponse.sendRedirect("/error");
            }
        }
    }

}