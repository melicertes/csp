package com.intrasoft.csp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.model.Rules;
import com.intrasoft.csp.model.Ruleset;
import com.intrasoft.csp.repository.RulesetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by chris on 26/6/2017.
 */

@Controller
public class RulesetsController {

    private static final Logger LOG = LoggerFactory.getLogger(RulesetsController.class);

    @Autowired
    RulesetRepository rulesetRepository ;

    @GetMapping("/rulesets")
    public String showRulesets(Ruleset ruleset, Model model) {
        List<Ruleset> rulesets =rulesetRepository.findAll();
        model.addAttribute("rulesets", rulesets);
        model.addAttribute("ruleset", ruleset);
        return "pages/rulesets";
    }

    @PostMapping("/rulesets")
    public String addRuleset(@RequestPart(value = "file", required = false) MultipartFile file,
                             RedirectAttributes redirectAttributes,
                             @ModelAttribute Ruleset ruleset,
                             BindingResult result,
                             Model model) throws IOException {

        if (result.hasErrors()) {
            LOG.error(result.getAllErrors().toString());
        }

        model.addAttribute("description", ruleset.getDescription());
        ruleset.setFilename(file.getOriginalFilename());
        ruleset.setFile(file.getBytes());
        String str = new String(ruleset.getFile());
        LOG.info(str);

        LOG.info("Import File");
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "pages/rulesets";
        }
        try {
//
            LOG.info(file.getOriginalFilename());
            byte[] bytes = file.getBytes();
            Path path = Paths.get(file.getOriginalFilename());
            Files.write(path, bytes);
            ruleset.setFile(file.getBytes());
            ruleset.setFilename(file.getOriginalFilename());
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "Something went wrong");
            return "pages/rulesets";
        }

        Rules rules = new ObjectMapper().readerFor(Rules.class).readValue(new String(file.getBytes()));
        LOG.info(rules.toString());

        rulesetRepository.save(ruleset);
        List<Ruleset> rulesets =rulesetRepository.findAll();
        model.addAttribute("rulesets", rulesets);
        return "pages/rulesets";
    }

/*    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllException(Exception ex) {

        ModelAndView model = new ModelAndView("pages/rulesets");
        model.addObject("errMsg", "this is Exception.class");

        return model;

    }*/
}
