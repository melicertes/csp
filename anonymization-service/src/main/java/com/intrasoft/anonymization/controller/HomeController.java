package com.intrasoft.anonymization.controller;

import com.intrasoft.anonymization.model.IntegrationAnonData;
import com.intrasoft.anonymization.model.IntegrationDataType;
import com.intrasoft.anonymization.repository.IntegrationAnonDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 26/6/2017.
 */

@Controller
public class HomeController {

    private static final Logger LOG = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    IntegrationAnonDataRepository integrationAnonDataRepository;

    @GetMapping("/")
    public String showRecords(IntegrationAnonData integrationAnonData, Model model) {
        model.addAttribute("integrationAnonData", integrationAnonData);
        List<IntegrationAnonData> integrationAnonDataAll =integrationAnonDataRepository.findAll();
        model.addAttribute("integrationAnonDataAll", integrationAnonDataAll);
        return "pages/home";
    }

    @PostMapping("/")
    public String addRecord(@ModelAttribute IntegrationAnonData integrationAnonData) {
        LOG.info("POST1: " + integrationAnonData.toString());
        integrationAnonDataRepository.save(integrationAnonData);
        return "pages/home";
    }
}
