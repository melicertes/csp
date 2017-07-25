package com.intrasoft.csp.anon.controller;

import com.intrasoft.csp.anon.model.Mapping;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.anon.model.Ruleset;
import com.intrasoft.csp.anon.repository.MappingRepository;
import com.intrasoft.csp.anon.repository.RulesetRepository;
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
    MappingRepository mappingRepository;

    @Autowired
    RulesetRepository rulesetRepository ;

    @ModelAttribute("integrationDataTypes")
    public IntegrationDataType[] integrationDataTypes() {
        return IntegrationDataType.values();
    }

    @ModelAttribute("mappings")
    public List<Mapping> getMappings() {
        return mappingRepository.findAll();
    }

    @ModelAttribute("rulesets")
    public List<Ruleset> rulesets() {
        return rulesetRepository.findAll();
    }

    @GetMapping("/")
    public ModelAndView showMappings(Mapping mapping, Model model) {
        ModelAndView mav = new ModelAndView("pages/mappings");
        mav.addObject(mapping);
        return mav;
    }

    @PostMapping("/")
    public ModelAndView addMapping(RedirectAttributes redirectAttributes, @ModelAttribute Mapping mapping, Model model) throws IOException {
        LOG.info("POST: " + mapping.toString());
        mappingRepository.save(mapping);
        ModelAndView mav = new ModelAndView("pages/mappings");
        mav.addObject(mapping);
        mav.addObject(getMappings());
        return mav;
    }

}
