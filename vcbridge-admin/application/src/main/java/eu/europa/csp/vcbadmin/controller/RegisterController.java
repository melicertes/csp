package eu.europa.csp.vcbadmin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.europa.csp.vcbadmin.model.User;
import eu.europa.csp.vcbadmin.repository.UserRepository;

@Controller
@RequestMapping("/register")
public class RegisterController {
	private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
	private UserRepository userRepository;

	private PasswordEncoder passwordEncoder;

	@Autowired
	public RegisterController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping
	public String getRegistrationPage(Model model) {
		model.addAttribute("userForm", new User());
		return "register";
	}

	@PostMapping
	public String postRegistrationPage(@Validated @ModelAttribute("userForm") User userForm, Model model) {
		userForm.setPassword(passwordEncoder.encode(userForm.getPassword()));
		log.info("Creating user {}", userForm);
		userRepository.save(userForm);
		return "redirect:/login";
	}
}
