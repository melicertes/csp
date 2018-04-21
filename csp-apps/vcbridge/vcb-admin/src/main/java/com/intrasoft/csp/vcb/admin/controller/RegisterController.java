package com.intrasoft.csp.vcb.admin.controller;

import java.io.IOException;

import com.intrasoft.csp.vcb.admin.repository.UserRepository;
import com.intrasoft.csp.vcb.commons.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.intrasoft.csp.vcb.admin.repository.EmailTemplateRepository;
import com.intrasoft.csp.vcb.admin.service.UserDetailsService;

@Controller
@RequestMapping("/register")
public class RegisterController {
	private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

	private UserRepository userRepository;

	private PasswordEncoder passwordEncoder;

	private EmailTemplateRepository emailTemplateRepository;

	@Autowired
	UserDetailsService userDetailsService;

	@Autowired
	public RegisterController(UserRepository userRepository, EmailTemplateRepository emailTemplateRepository,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.emailTemplateRepository = emailTemplateRepository;
	}

	@GetMapping
	public String getRegistrationPage(Model model) {
		model.addAttribute("userForm", new User());
		return "register";
	}

	@Value(value = "classpath:templates/email/invitation.html")
	private Resource invitationHTML;

	@Value(value = "classpath:templates/email/cancellation.html")
	private Resource cancellationHTML;

	@PostMapping
	public String postRegistrationPage(@Validated @ModelAttribute("userForm") User userForm, Model model)
			throws IOException {
		userDetailsService.createNewUser(userForm);

		// user.setInvitation(invitation);
		// user.setCancellation(cancellation);

		// userRepository.save(user);

		return "redirect:/login";
	}

}
