package com.intrasoft.csp.vcb.teleconf.config;


import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        if (authentication != null && authentication.getDetails() != null) {
            try {
                request.getSession().invalidate();
                //you can add more codes here when the user successfully logs out,
                //such as updating the database for last active.
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Set the Server Status
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);

        //redirect to login
        String queryString = request.getParameter("uid");

        //Check if no parameters have been passed
        if (queryString == null) {
            httpServletResponse.sendRedirect("/error");
        } else {
            httpServletResponse.sendRedirect("/?uid=" + queryString);
        }
    }

}