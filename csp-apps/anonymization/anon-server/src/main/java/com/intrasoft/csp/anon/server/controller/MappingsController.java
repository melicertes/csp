package com.intrasoft.csp.anon.server.controller;

import com.intrasoft.csp.anon.commons.model.MappingDTO;
import com.intrasoft.csp.anon.commons.model.RuleSetDTO;
import com.intrasoft.csp.anon.commons.model.SaveMappingDTO;
import com.intrasoft.csp.anon.server.service.AnonService;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * Created by chris on 26/6/2017.
 */

@Controller
public class MappingsController {

//    private static final Logger LOG = LoggerFactory.getLogger(MappingsController.class);
    private static Logger LOG_AUDIT = LoggerFactory.getLogger("audit-log");
    private static Logger LOG_EXCEPTION = LoggerFactory.getLogger("exc-log");

    @Autowired
    AnonService anonService;

    @ModelAttribute("integrationDataTypes")
    public IntegrationDataType[] integrationDataTypes() {
        return IntegrationDataType.values();
    }

    @ModelAttribute("mappings")
    public List<MappingDTO> getMappings() {
        return anonService.getAllMappings();
    }

    @ModelAttribute("rulesets")
    public List<RuleSetDTO> rulesets() {
        return anonService.getAllRuleSet();
    }

    @GetMapping("/mappings")
    public ModelAndView showMappings(@ModelAttribute("mapping") SaveMappingDTO mapping) {
        return new ModelAndView("pages/mappings", "mappings", getMappings());
    }

    @GetMapping("/mappings/{id}")
    public ModelAndView showMappings(@PathVariable Long id) {
        ModelAndView mav = new ModelAndView("pages/mappings");
        mav.addObject("mappings", getMappings());
        MappingDTO mapping = anonService.getMappingById(id);
        LOG_AUDIT.info("UI: GET mapping " + mapping.toString());
        mav.addObject("mapping", new SaveMappingDTO(mapping.getId(),mapping.getCspId(),mapping.getRuleSetDTO().getId(),mapping.getDataType()));
        return mav;
    }

    @PostMapping("/mapping/save")
    public ModelAndView addMapping(RedirectAttributes redirect, @ModelAttribute("mapping") SaveMappingDTO mapping, BindingResult bindingResult) throws IOException {
        LOG_AUDIT.info("UI: CREATE mapping: " + mapping.toString());
        if (bindingResult.hasErrors()) {
            return new ModelAndView("pages/mappings");
        }
        MappingDTO newMapping = anonService.saveMapping(mapping);
        ModelAndView mav = new ModelAndView("redirect:/mappings");
        redirect.addFlashAttribute("msg", "Mapping saved");
        return mav;
    }

    @GetMapping("/mapping/delete/{id}")
    public ModelAndView deleteMapping(@PathVariable Long id, RedirectAttributes redirect) throws IOException {
        LOG_AUDIT.info("UI: DELETE mapping with id: " + id);
        anonService.deleteMapping(id);
        ModelAndView mav = new ModelAndView("redirect:/mappings");
        redirect.addFlashAttribute("msg", "Mapping deleted");
        return mav;
    }
}
