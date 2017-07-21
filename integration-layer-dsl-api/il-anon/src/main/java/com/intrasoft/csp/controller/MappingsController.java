package com.intrasoft.csp.controller;

import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.model.IntegrationAnonData;
import com.intrasoft.csp.model.Ruleset;
import com.intrasoft.csp.repository.IntegrationAnonDataRepository;
import com.intrasoft.csp.repository.RulesetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

/**
 * Created by chris on 26/6/2017.
 */

@Controller
public class MappingsController {

    private static final Logger LOG = LoggerFactory.getLogger(MappingsController.class);

    @Autowired
    IntegrationAnonDataRepository integrationAnonDataRepository;

    @Autowired
    RulesetRepository rulesetRepository ;

    @ModelAttribute("integrationDataTypes")
    public IntegrationDataType[] integrationDataTypes() {
        return IntegrationDataType.values();
    }

    @ModelAttribute("integrationAnonDataAll")
    public List<IntegrationAnonData> integrationAnonDataAll() {
        return integrationAnonDataRepository.findAll();
    }

    @ModelAttribute("rulesets")
    public List<Ruleset> rulesets() {
        return rulesetRepository.findAll();
    }

    @GetMapping("/")
    public ModelAndView showRecords(IntegrationAnonData integrationAnonData, Model model) {
        ModelAndView mav = new ModelAndView("pages/mappings");
        mav.addObject(integrationAnonData);
        return mav;
    }

    @PostMapping("/")
    public ModelAndView addMapping(RedirectAttributes redirectAttributes, @ModelAttribute IntegrationAnonData integrationAnonData, Model model) throws IOException {
        LOG.info("POST: " + integrationAnonData.toString());
        integrationAnonDataRepository.save(integrationAnonData);
        ModelAndView mav = new ModelAndView("pages/mappings");
        mav.addObject(integrationAnonData);
        mav.addObject(integrationAnonDataAll());
        return mav;
    }

}
