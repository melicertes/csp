package com.intrasoft.csp.anon.controller;

import com.intrasoft.csp.anon.model.Ruleset;
import com.intrasoft.csp.anon.repository.RulesetRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by chris on 26/6/2017.
 */

@Controller
public class RuleSetController {

    private static final Logger LOG = LoggerFactory.getLogger(RuleSetController.class);

    @Autowired
    RulesetRepository rulesetRepository ;

    @GetMapping("/rulesets")
    public String showRulesets(Ruleset ruleset, Model model) {
        List<Ruleset> rulesets =rulesetRepository.findAll();
        model.addAttribute("rulesets", rulesets);
        model.addAttribute("ruleset", ruleset);
        return "pages/rulesets";
    }

    @PostMapping("/rulesets/save")
    public String addRuleset(@RequestPart(value = "file", required = false) MultipartFile file,
                             RedirectAttributes redirectAttributes,
                             @ModelAttribute Ruleset ruleset,
                             BindingResult result,
                             Model model) throws IOException {

       /* if (result.hasErrors()) {
            LOG.error(result.getFieldError().toString());
            redirectAttributes.addFlashAttribute("error", result.getFieldError().toString());
            return "redirect:";
        }*/

        model.addAttribute("description", ruleset.getDescription());
        ruleset.setFilename(file.getOriginalFilename());
        ruleset.setFile(file.getBytes());
        String str = new String(ruleset.getFile());
        LOG.info(str);

        LOG.info("Import File");
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select a file to upload");
            return "redirect:";
        }
        try {

            LOG.info(file.getOriginalFilename());
            byte[] bytes = file.getBytes();
            Path path = Paths.get(file.getOriginalFilename());
            ruleset.setFile(bytes);
            ruleset.setFilename(file.getOriginalFilename());
        } catch (IOException e) {
            LOG.error("File upload failed: " + e);
            redirectAttributes.addFlashAttribute("error", "Something went wrong");
            return "redirect:";
        }

        rulesetRepository.save(ruleset);
        List<Ruleset> rulesets =rulesetRepository.findAll();
        model.addAttribute("rulesets", rulesets);
        redirectAttributes.addFlashAttribute("msg", "Ruleset imported.");
        return "redirect:";
    }

    @GetMapping("/rulesets/{id}")
    public ModelAndView showRuleset(@PathVariable Long id) {
        ModelAndView mav = new ModelAndView("pages/rulesets");
        mav.addObject("rulesets", rulesetRepository.findAll());
        Ruleset ruleset = rulesetRepository.findOne(id);
        LOG.info(ruleset.toString());
        mav.addObject("ruleset", ruleset);
        return mav;
    }

    @GetMapping("/ruleset/delete/{id}")
    public ModelAndView deleteMapping(@PathVariable Long id, RedirectAttributes redirect) throws ConstraintViolationException {
        LOG.info("DELETE ruleset with id: " + id);
        rulesetRepository.delete(id);
        ModelAndView mav = new ModelAndView("redirect:/rulesets");
        mav.addObject("mappings",rulesetRepository.findAll());
        redirect.addFlashAttribute("msg", "Ruleset deleted");
        return mav;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ModelAndView handleSqlException(HttpServletRequest request, Exception ex, RedirectAttributes redirect){
        LOG.error("Requested URL="+request.getRequestURL());
        LOG.error("Exception Raised");
        ModelAndView mav = new ModelAndView("redirect:/rulesets");
        mav.addObject("exception", ex);
        mav.addObject("url", request.getRequestURL());
        mav.addObject("mappings",rulesetRepository.findAll());
        redirect.addFlashAttribute("error", "Ruleset could not be deleted");

//        mav.setViewName("error");
        return mav;
    }

/*    @ExceptionHandler(IOException.class)
    public ModelAndView IOException(HttpServletRequest request, Exception ex, RedirectAttributes redirect){
        LOG.error("Requested URL="+request.getRequestURL());
        LOG.error("Exception Raised");
        ModelAndView mav = new ModelAndView("redirect:/rulesets");
        mav.addObject("exception", ex);
        mav.addObject("url", request.getRequestURL());
        mav.addObject("mappings",rulesetRepository.findAll());
        redirect.addFlashAttribute("error", "File could not be imported");

        return mav;
    }*/
}
