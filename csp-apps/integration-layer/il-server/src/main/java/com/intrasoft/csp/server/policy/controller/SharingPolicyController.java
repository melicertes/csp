package com.intrasoft.csp.server.policy.controller;

import com.intrasoft.csp.server.policy.domain.SharingPolicyRoutes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SharingPolicyController implements SharingPolicyRoutes{
    @RequestMapping("/" + SharingPolicyRoutes.HOME_URL)
    public String home(final Model model) {
        return HOME_TH;
    }
}
