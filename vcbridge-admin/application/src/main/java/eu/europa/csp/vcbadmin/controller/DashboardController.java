package eu.europa.csp.vcbadmin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

import eu.europa.csp.vcbadmin.config.OpenfireProperties;

@Controller
public class DashboardController {
	// String test;

	@Autowired
	public DashboardController(OpenfireProperties properties) {
		// this.test = properties.getTest();
	}

	@GetMapping("/")
	public String showHome(Model model) {
		// model.addAttribute("test", test);
		return "dashboard";
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity handleAllExceptions(Exception exc) {
		return ResponseEntity.status(500).build();
	}
}
