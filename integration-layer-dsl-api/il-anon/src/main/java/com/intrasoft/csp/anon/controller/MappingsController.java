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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
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

    @GetMapping("/mappings")
    public ModelAndView showMappings(Mapping mapping) {
        return new ModelAndView("pages/mappings", "mappings", getMappings());
    }

    @GetMapping("/mappings/{id}")
    public ModelAndView showMappings(@PathVariable Long id) {
        ModelAndView mav = new ModelAndView("pages/mappings");
        mav.addObject("mappings", getMappings());
        Mapping mapping = mappingRepository.findOne(id);
        LOG.info(mapping.toString());
        mav.addObject("mapping", mapping);
        return mav;
    }

    @PostMapping("/mapping/save")
    public ModelAndView addMapping(RedirectAttributes redirect, @Valid Mapping mapping, BindingResult bindingResult) throws IOException {
        LOG.info("CREATE mapping: " + mapping.toString());
        if (bindingResult.hasErrors()) {
            return new ModelAndView("pages/mappings");
        }
        Mapping newMapping = mappingRepository.save(mapping);
        ModelAndView mav = new ModelAndView("redirect:/mappings/" + mapping.getId());
        mav.addObject("mappings", getMappings());
        mav.addObject("mapping",newMapping);
        redirect.addFlashAttribute("msg", "Mapping created");
        return mav;
    }

    @GetMapping("/mapping/delete/{id}")
    public ModelAndView deleteMapping(@PathVariable Long id, RedirectAttributes redirect) throws IOException {
        LOG.info("DELETE mapping with id: " + id);
        mappingRepository.delete(id);
        ModelAndView mav = new ModelAndView("redirect:/mappings");
        mav.addObject("mappings",getMappings());
        redirect.addFlashAttribute("msg", "Mapping deleted");
        return mav;
    }

}
