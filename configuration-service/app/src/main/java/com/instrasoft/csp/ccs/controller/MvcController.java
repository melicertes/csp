package com.instrasoft.csp.ccs.controller;

import com.instrasoft.csp.ccs.config.DataContextUrl;
import com.instrasoft.csp.ccs.config.PagesContextUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MvcController implements PagesContextUrl, DataContextUrl {

    private static final Logger LOG = LoggerFactory.getLogger(MvcController.class);

    /*
    MAIN Pages
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index(Model model) {
        model = this.init(model);
        model.addAttribute("dataDashboardUrl", DATA_BASEURL+DATA_DASHBOARD);
        model.addAttribute("navHomeClassActive", "active");
        return new ModelAndView("pages/dashboard", "pages", null);
    }

    @RequestMapping(value = PAGES_DASHBOARD, method = RequestMethod.GET)
    public ModelAndView dashboard(Model model) {
        model = this.init(model);
        model.addAttribute("dataDashboardUrl", DATA_BASEURL+DATA_DASHBOARD);
        model.addAttribute("navHomeClassActive", "active");
        return new ModelAndView("pages/dashboard", "pages", null);
    }

    @RequestMapping(value = PAGES_MANAGE, method = RequestMethod.GET)
    public ModelAndView manage(@RequestParam("cspId") String cspId, Model model) {
        model = this.init(model);
        model.addAttribute("navHomeClassActive", "");
        return new ModelAndView("pages/manage", "pages", null);
    }

    /*
    CSP Pages
     */
    @RequestMapping(value = PAGES_CSP_LIST, method = RequestMethod.GET)
    public ModelAndView cspIndex(Model model) {
        model = this.init(model);
        model.addAttribute("addCspUrl", PAGES_CSP_ADD);

        model.addAttribute("navCspClassActive", "active");
        return new ModelAndView("pages/csp/list", "pages", null);
    }

    /*
    MODULE PAGES
     */
    @RequestMapping(value = PAGES_MODULE_LIST, method = RequestMethod.GET)
    public ModelAndView modulesIndex(Model model) {
        model = this.init(model);
        model.addAttribute("addModuleUrl", PAGES_MODULE_ADD);
        model.addAttribute("dataModuleUrl", DATA_BASEURL+DATA_MODULES);
        model.addAttribute("navModuleClassActive", "active");
        return new ModelAndView("pages/module/list", "pages", null);
    }

    @RequestMapping(value = PAGES_MODULE_ADD, method = RequestMethod.GET)
    public ModelAndView modulesAdd(Model model) {
        model = this.init(model);
        model.addAttribute("moduleSaveUrl", DATA_BASEURL + DATA_MODULE_SAVE);
        model.addAttribute("moduleListUrl", PAGES_MODULE_LIST);
        model.addAttribute("navModuleClassActive", "active");
        return new ModelAndView("pages/module/add", "pages/module", null);
    }

    private Model init(Model m) {
        m.addAttribute("dashboardUrl", PAGES_DASHBOARD);
        m.addAttribute("cspListUrl", PAGES_CSP_LIST);
        m.addAttribute("moduleListUrl", PAGES_MODULE_LIST);

        return m;
    }
}
