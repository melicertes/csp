package com.intrasoft.csp.controller;

import com.intrasoft.csp.model.IntegrationAnonData;
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

    @GetMapping("/")
    public String showRecords(IntegrationAnonData integrationAnonData, Model model) {
        model.addAttribute("integrationAnonData", integrationAnonData);
        List<IntegrationAnonData> integrationAnonDataAll =integrationAnonDataRepository.findAll();
        model.addAttribute("integrationAnonDataAll", integrationAnonDataAll);
        model.addAttribute("rulesets", rulesetRepository.findAll());
        return "pages/mappings";
    }

    @PostMapping("/")
    public String addMapping(RedirectAttributes redirectAttributes, @ModelAttribute IntegrationAnonData integrationAnonData, Model model) throws IOException {
        LOG.info("POST1: " + integrationAnonData.toString());
        model.addAttribute("integrationAnonData", integrationAnonData);
        List<IntegrationAnonData> integrationAnonDataAll =integrationAnonDataRepository.findAll();
        model.addAttribute("integrationAnonDataAll", integrationAnonDataAll);
        integrationAnonDataRepository.save(integrationAnonData);
        return "pages/mappings";
    }

}
