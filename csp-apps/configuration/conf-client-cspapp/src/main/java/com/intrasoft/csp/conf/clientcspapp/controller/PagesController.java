package com.intrasoft.csp.conf.clientcspapp.controller;


import com.intrasoft.csp.conf.clientcspapp.context.ContextUrl;
import com.intrasoft.csp.conf.commons.types.ContactType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
public class PagesController implements ContextUrl {

    @Value("${client.ui.jiralink}")
    String jiraLink;

    @Value("${client.ui.statusInterval}")
    Integer statusInterval;

    @Value("${client.ui.refreshInterval}")
    Integer refreshInterval;



    @ModelAttribute("csp_contact_types")
    public ContactType[] contactTypes() {
        return ContactType.values();
    }

    /*
    MAIN Pages
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index(Model model) {
        model = this.init(model);

        model.addAttribute("navHomeClassActive", "active");

        model.addAttribute("asyncInterval", statusInterval);

        return new ModelAndView("pages/dashboard", "dashboard", model);
    }

    @RequestMapping(value = PAGE_DASHBOARD, method = RequestMethod.GET)
    public ModelAndView dashboard(Model model) {
        model = this.init(model);

        model.addAttribute("navHomeClassActive", "active");

        model.addAttribute("asyncInterval", statusInterval);

        return new ModelAndView("pages/dashboard", "dashboard", model);
    }

    @RequestMapping(value = PAGE_INSTALL, method = RequestMethod.GET)
    public ModelAndView install(Model model) {
        model = this.init(model);

        model.addAttribute("navInstallClassActive", "active");

        Integer installState = 0;
        if (installState == 0) {
            model.addAttribute("cspId", UUID.randomUUID().toString());
            model.addAttribute("cspRegisterApi", REST_REGISTER);
            return new ModelAndView("pages/install-register", "install-register", model);
        }
        return new ModelAndView("pages/install-complete", "install-complete", model);
    }

    @RequestMapping(value = PAGE_UPDATES, method = RequestMethod.GET)
    public ModelAndView updates(Model model) {
        model = this.init(model);

        model.addAttribute("navUpdatesClassActive", "active");

        return new ModelAndView("pages/updates", "updates", model);
    }

    @RequestMapping(value = PAGE_STATUS, method = RequestMethod.GET)
    public ModelAndView status(Model model) {
        model = this.init(model);

        model.addAttribute("navUpdatesClassActive", "active");

        model.addAttribute("moduleName", "MISP");
        model.addAttribute("moduleVersionFrom", "12.00");
        model.addAttribute("moduleVersionTo", "12.21");
        model.addAttribute("refreshInterval", refreshInterval);
        model.addAttribute("logUrl", REST_LOG);

        return new ModelAndView("pages/status", "status", model);
    }




    /*
    Internal methods
     */
    private Model init(Model m) {
        m.addAttribute("dashboardUrl", PAGE_DASHBOARD);
        m.addAttribute("installUrl", PAGE_INSTALL);
        m.addAttribute("updatesUrl", PAGE_UPDATES);
        m.addAttribute("statusUrl", PAGE_STATUS);
        m.addAttribute("contactUrl", jiraLink);

        m.addAttribute("navHomeClassActive", "");
        m.addAttribute("navInstallClassActive", "");
        m.addAttribute("navUpdatesClassActive", "");

        return m;
    }
}
