package com.intrasoft.csp.conf.clientcspapp.controller;


import com.intrasoft.csp.conf.clientcspapp.context.ContextUrl;
import com.intrasoft.csp.conf.clientcspapp.service.BackgroundTaskService;
import com.intrasoft.csp.conf.clientcspapp.service.InstallationService;
import com.intrasoft.csp.conf.commons.types.ContactType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    @Autowired
    InstallationService installService;

    @Autowired
    BackgroundTaskService backgroundTaskService;

    @ModelAttribute("csp_contact_types")
    public ContactType[] contactTypes() {
        return ContactType.values();
    }

    /*
    MAIN Pages
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {

        return "redirect:dashboard.html";
    }

    @RequestMapping(value = PAGE_DASHBOARD, method = RequestMethod.GET)
    public ModelAndView dashboard(Model model) {
        model = this.init(model);

        if (installService.isInstallationOngoing()) {
            model.addAttribute("cspId",installService.getState().getCspId());
        }

        model.addAttribute("internetAvailable", backgroundTaskService.isInternetAvailable());
        model.addAttribute("navHomeClassActive", "active");

        model.addAttribute("asyncInterval", statusInterval);

        return new ModelAndView("pages/dashboard", "dashboard", model);
    }

    @RequestMapping(value = PAGE_INSTALL, method = RequestMethod.GET)
    public ModelAndView install(Model model) {
        model = this.init(model);

        model.addAttribute("navInstallClassActive", "active");

        if (installService.isInstallationComplete() || installService.isInstallationOngoing()) {
            return new ModelAndView("pages/install-complete", "install-complete", model);
        } else {
            model.addAttribute("cspId", UUID.randomUUID().toString());
            model.addAttribute("cspRegisterApi", REST_REGISTER);
            model.addAttribute("cspRegisterFilesApi", REST_REGISTER_FILES);

            return new ModelAndView("pages/install-register", "install-register", model);
        }
    }

    @RequestMapping(value = PAGE_UPDATES, method = RequestMethod.GET)
    public ModelAndView updates(Model model) {
        model = this.init(model);

        model.addAttribute("navUpdatesClassActive", "active");
        final String cspId = installService.getState().getCspId();
        if (cspId != null) {
            model.addAttribute("updatesRetrieveUrl", REST_UPDATESFOUND + "/" + cspId);
        }
        return new ModelAndView("pages/updates", "updates", model);
    }

    @RequestMapping(value = PAGE_SYSTEM, method = RequestMethod.GET)
    public ModelAndView system(Model model) {
        model = this.init(model);
        model.addAttribute("navSystemClassActive", "active");
        final String cspId = installService.getState().getCspId();
        if (cspId != null) {
            model.addAttribute("systemRetrieveUrl", REST_MODULESERVICES + "/" + cspId);
            //todo queryservice?
            //installService.queryService()
        }
        return new ModelAndView("pages/system", "system", model);
    }

    @RequestMapping(value = PAGE_LINKS, method = RequestMethod.GET)
    public ModelAndView getPageLinks(Model model) {
        model = this.init(model);

        //TODO if installation is complete, add the links here

        return new ModelAndView("fragments :: links", "links", model);
    }

    @RequestMapping(value = PAGE_STATUS, method = RequestMethod.GET)
    public ModelAndView status(Model model) {
        model = this.init(model);

        model.addAttribute("navUpdatesClassActive", "active");
    //TODO find the inprogress task or remove these attributes
        model.addAttribute("refreshInterval", refreshInterval);
        model.addAttribute("logUrl", REST_LOG);

        return new ModelAndView("pages/status", "status", model);
    }


    @RequestMapping(value = PAGE_DOWNLOADMODULE + "/{hash}", method = RequestMethod.GET)
    public String downloadModule(@PathVariable String hash) {
        backgroundTaskService.scheduleDownload(installService.findModuleByHash(hash));
        return "redirect:"+PAGE_STATUS;
    }


    @RequestMapping(value = PAGE_INSTALLMODULE + "/{hash}", method = RequestMethod.GET)
    public String installModule(@PathVariable String hash) {
        backgroundTaskService.scheduleInstall(installService.findModuleByHash(hash));
        return "redirect:"+PAGE_STATUS;
    }


    @RequestMapping(value = PAGE_REINSTALLMODULE + "/{hash}", method = RequestMethod.GET)
    public String reInstallModule(@PathVariable String hash) {
        backgroundTaskService.scheduleReInstall(installService.findModuleByHash(hash));
        return "redirect:"+PAGE_STATUS;
    }

    @RequestMapping(value = PAGE_DELETEMODULE + "/{hash}", method = RequestMethod.GET)
    public String deleteModule(@PathVariable String hash) {
        backgroundTaskService.scheduleDelete(installService.findModuleByHash(hash));
        return "redirect:"+PAGE_STATUS;
    }


    @RequestMapping(value = PAGE_STARTMODULES + "/{cspId}", method = RequestMethod.GET)
    public String startActiveModules(@PathVariable String cspId) {
        backgroundTaskService.scheduleStartActiveModules();
        return "redirect:"+PAGE_STATUS;
    }

    @RequestMapping(value = PAGE_STOPMODULES + "/{cspId}", method = RequestMethod.GET)
    public String stopActiveModules(@PathVariable String cspId) {
        backgroundTaskService.scheduleStopActiveModules();
        return "redirect:"+PAGE_STATUS;
    }



    /*
    Internal methods
     */
    private Model init(Model m) {
        m.addAttribute("dashboardUrl", PAGE_DASHBOARD);
        m.addAttribute("installUrl", PAGE_INSTALL);
        m.addAttribute("updatesUrl", PAGE_UPDATES);
        m.addAttribute("systemUrl", PAGE_SYSTEM);
        m.addAttribute("statusUrl", PAGE_STATUS);
        m.addAttribute("contactUrl", jiraLink);
        m.addAttribute("dashboardLinks", PAGE_LINKS);
        m.addAttribute("dashboardStatusUrl", REST_DASHSTATUS);

        m.addAttribute("startModulesUrl", PAGE_STARTMODULES);
        m.addAttribute("stopModulesUrl", PAGE_STOPMODULES);
        m.addAttribute("systemRetrieveUrl", REST_MODULESERVICES);


        m.addAttribute("navHomeClassActive", "");
        m.addAttribute("navInstallClassActive", "");
        m.addAttribute("navUpdatesClassActive", "");
        m.addAttribute("navSystemClassActive", "");

        return m;
    }
}
