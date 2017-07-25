package com.instrasoft.csp.ccs.controller;

import com.instrasoft.csp.ccs.config.context.DataContextUrl;
import com.instrasoft.csp.ccs.config.context.PagesContextUrl;
import com.instrasoft.csp.ccs.config.types.ContactType;
import com.instrasoft.csp.ccs.domain.data.table.ManagementRow;
import com.instrasoft.csp.ccs.domain.postgresql.*;
import com.instrasoft.csp.ccs.repository.*;
import com.instrasoft.csp.ccs.utils.VersionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MvcController implements PagesContextUrl, DataContextUrl {

    private static final Logger LOG = LoggerFactory.getLogger(MvcController.class);


    @Autowired
    CspRepository cspRepository;

    @Autowired
    CspContactRepository cspContactRepository;

    @Autowired
    CspIpRepository cspIpRepository;

    @Autowired
    CspInfoRepository cspInfoRepository;

    @Autowired
    CspModuleInfoRepository cspModuleInfoRepository;

    @Autowired
    CspManagementRepository cspManagementRepository;

    @Autowired
    ModuleRepository moduleRepository;

    @Autowired
    ModuleVersionRepository moduleVersionRepository;

    @ModelAttribute("csp_contact_type")
    public ContactType[] contactTypes() {
        return IntegrationDataType.values();
    }

    /*
    MAIN Pages
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index(Model model) {
        model = this.init(model);
        model.addAttribute("dataDashboardUrl", DATA_BASEURL+DATA_DASHBOARD);
        model.addAttribute("navHomeClassActive", "active");
        return new ModelAndView("pages/dashboard", "dashboard", model);
    }

    @RequestMapping(value = PAGES_DASHBOARD, method = RequestMethod.GET)
    public ModelAndView dashboard(Model model) {
        model = this.init(model);
        model.addAttribute("dataDashboardUrl", DATA_BASEURL+DATA_DASHBOARD);
        model.addAttribute("navHomeClassActive", "active");
        return new ModelAndView("pages/dashboard", "dashboard", model);
    }

    @RequestMapping(value = PAGES_MANAGE, method = RequestMethod.GET)
    public ModelAndView manage(@RequestParam("cspId") String cspId, Model model) {
        model = this.init(model);

        model.addAttribute("navHomeClassActive", "active");

        List<ManagementRow> managementRows = new ArrayList<>();
        List<Module> modules = moduleRepository.findAll();
        for(Module module : modules) {
            if (moduleVersionRepository.countByModuleId(module.getId()) > 0) {
                ManagementRow row = new ManagementRow();

                row.setModuleId(module.getId());
                row.setIsModuleDefault(module.getIsDefault());
                row.setModuleShortName(module.getName());

                CspInfo cspInfo = cspInfoRepository.findByCspId(cspId).get(0);
                List<CspModuleInfo> cspModuleInfos = cspModuleInfoRepository.findByCspInfoId(cspInfo.getId());
                List<CspManagement> cspManagementList = cspManagementRepository.findByCspIdAndModuleId(cspId, module.getId());
                if (cspManagementList.size() == 0) {
                    row.setModuleEnabled(false);
                    row.setInstalledVersion(null);
                }
                else {
                    row.setModuleEnabled(true);
                    row.setInstalledVersion(moduleVersionRepository.findOne(cspModuleInfos.get(0).getModuleVersionId()).getVersion());
                }


                List<Integer> availableVersions = moduleVersionRepository.findVersionsByModuleId(module.getId());
                row.setAvailableVersions(availableVersions);
                List<String> availableVersionsT = new ArrayList<>();
                for(int i=0; i<availableVersions.size(); i++) {
                    availableVersionsT.add(VersionParser.toString(availableVersions.get(i)));
                }
                row.setAvailableVersionsT(availableVersionsT);

                row.setSelectedVersion(null);
                if (cspManagementList.size() != 0) {
                    row.setSelectedVersion(moduleVersionRepository.findOne(cspManagementList.get(0).getModuleVersionId()).getVersion());
                }


                managementRows.add(row);
            }
        }
        model.addAttribute("managementRows", managementRows);
        model.addAttribute("cspId", cspId);

        model.addAttribute("dashboardUrl", PAGES_DASHBOARD);
        model.addAttribute("saveUrl", DATA_BASEURL + DATA_MANAGE);

        return new ModelAndView("pages/manage", "manage", model);
    }


    /*
    CSP Pages
     */
    @RequestMapping(value = PAGES_CSP_LIST, method = RequestMethod.GET)
    public ModelAndView cspList(Model model) {
        model = this.init(model);
        model.addAttribute("addCspUrl", PAGES_CSP_REGISTER);
        model.addAttribute("dataCspUrl", DATA_BASEURL + DATA_CSPS);
        model.addAttribute("removeCspUrl", DATA_BASEURL + DATA_CSP_REMOVE);
        model.addAttribute("navCspClassActive", "active");
        return new ModelAndView("pages/csp/list", "list", model);
    }

    @RequestMapping(value = PAGES_CSP_REGISTER, method = RequestMethod.GET)
    public ModelAndView cspRegister(Model model) {
        model = this.init(model);
        model.addAttribute("cspSaveUrl", DATA_BASEURL + DATA_CSP_SAVE);
        model.addAttribute("cspListUrl", PAGES_CSP_LIST);
        model.addAttribute("navCspClassActive", "active");
        return new ModelAndView("pages/csp/register", "register", model);
    }

    @RequestMapping(value = PAGES_CSP_UPDATE, method = RequestMethod.GET)
    public ModelAndView cspUpdate(@RequestParam("cspId") String cspId, Model model) {
        model = this.init(model);

        Csp csp = cspRepository.findOne(cspId);
        if (csp == null) {
            return new ModelAndView("error", "error", model);
    }

        model.addAttribute("cspUpdateUrl", DATA_BASEURL + DATA_CSP_UPDATE);
        model.addAttribute("cspListUrl", PAGES_CSP_LIST);

        model.addAttribute("cspData", cspRepository.findOne(cspId));
        model.addAttribute("cspContacts", cspContactRepository.findByCspId(cspId));
        model.addAttribute("cspInternalIps", cspIpRepository.findByCspIdAndExternal(cspId, 0));
        model.addAttribute("cspExternalIps", cspIpRepository.findByCspIdAndExternal(cspId, 1));

        model.addAttribute("navCspClassActive", "active");
        return new ModelAndView("pages/csp/update", "update", model);
    }



    /*
    MODULE PAGES
     */
    @RequestMapping(value = PAGES_MODULE_LIST, method = RequestMethod.GET)
    public ModelAndView moduleList(Model model) {
        model = this.init(model);
        model.addAttribute("addModuleUrl", PAGES_MODULE_REGISTER);
        model.addAttribute("dataModuleUrl", DATA_BASEURL + DATA_MODULES);
        model.addAttribute("removeModuleUrl", DATA_BASEURL + DATA_MODULE_REMOVE);
        model.addAttribute("navModuleClassActive", "active");
        return new ModelAndView("pages/module/list", "list", model);
    }

    @RequestMapping(value = PAGES_MODULE_REGISTER, method = RequestMethod.GET)
    public ModelAndView moduleRegister(Model model) {
        model = this.init(model);
        model.addAttribute("moduleSaveUrl", DATA_BASEURL + DATA_MODULE_SAVE);
        model.addAttribute("moduleListUrl", PAGES_MODULE_LIST);
        model.addAttribute("navModuleClassActive", "active");
        return new ModelAndView("pages/module/register", "register", model);
    }

    @RequestMapping(value = PAGES_MODULE_UPDATE, method = RequestMethod.GET)
    public ModelAndView moduleUpdate(@RequestParam("moduleId") Long moduleId, Model model) {
        model = this.init(model);

        Module module = moduleRepository.findOne(moduleId);
        if (module == null) {
            return new ModelAndView("error", "error", model);
        }

        model.addAttribute("moduleUpdateUrl", DATA_BASEURL + DATA_MODULE_UPDATE);
        model.addAttribute("moduleListUrl", PAGES_MODULE_LIST);

        model.addAttribute("moduleData", moduleRepository.findOne(moduleId));

        model.addAttribute("navModuleClassActive", "active");

        return new ModelAndView("pages/module/update", "update", model);
    }



    /*
    MODULE VERSION PAGES
     */
    @RequestMapping(value = PAGES_MODULE_VERSION_LIST, method = RequestMethod.GET)
    public ModelAndView moduleVersionsList(@RequestParam("moduleId") Long moduleId, Model model) {
        model = this.init(model);

        model.addAttribute("moduleData", moduleRepository.findOne(moduleId));

        model.addAttribute("addModuleVersionsUrl", PAGES_MODULE_VERSION_REGISTER + "?moduleId=" + moduleId);
        model.addAttribute("dataModuleVersionsUrl", DATA_BASEURL + DATA_MODULE_VERSION + "/" + moduleId + ".json");
        model.addAttribute("removeModuleVersionsUrl", DATA_BASEURL + DATA_MODULE_VERSION_REMOVE);

        model.addAttribute("navModuleClassActive", "active");

        return new ModelAndView("pages/module-version/list", "list", model);
    }

    @RequestMapping(value = PAGES_MODULE_VERSION_REGISTER, method = RequestMethod.GET)
    public ModelAndView moduleVersionRegister(@RequestParam("moduleId") Long moduleId, Model model) {
        model = this.init(model);

        model.addAttribute("moduleData", moduleRepository.findOne(moduleId));

        model.addAttribute("moduleVersionSaveUrl", DATA_BASEURL + DATA_MODULE_VERSION_SAVE + "/" + moduleId);
        model.addAttribute("moduleVersionListUrl", PAGES_MODULE_VERSION_LIST + "?moduleId=" + moduleId);

        model.addAttribute("navModuleClassActive", "active");

        return new ModelAndView("pages/module-version/register", "register", model);
    }

    @RequestMapping(value = PAGES_MODULE_VERSION_UPDATE, method = RequestMethod.GET)
    public ModelAndView moduleVersionUpdate(@RequestParam("moduleVersionId") Long moduleVersionId, Model model) {
        model = this.init(model);

        ModuleVersion moduleVersion = moduleVersionRepository.findOne(moduleVersionId);
        if (moduleVersion == null) {
            return new ModelAndView("error", "error", model);
        }

        model.addAttribute("moduleVersionUpdateUrl", DATA_BASEURL + DATA_MODULE_VERSION_UPDATE);
        model.addAttribute("moduleVersionListUrl", PAGES_MODULE_VERSION_LIST + "?moduleId=" + moduleVersion.getModuleId());

        model.addAttribute("moduleVersionData", moduleVersion);

        model.addAttribute("navModuleClassActive", "active");

        return new ModelAndView("pages/module-version/update", "update", model);
    }


    /*
    Internal methods
     */
    private Model init(Model m) {
        m.addAttribute("dashboardUrl", PAGES_DASHBOARD);
        m.addAttribute("cspListUrl", PAGES_CSP_LIST);
        m.addAttribute("moduleListUrl", PAGES_MODULE_LIST);

        return m;
    }
}
