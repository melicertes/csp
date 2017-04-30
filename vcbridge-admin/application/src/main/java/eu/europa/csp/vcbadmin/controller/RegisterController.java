package eu.europa.csp.vcbadmin.controller;

import java.io.IOException;
import java.util.Scanner;

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

import eu.europa.csp.vcbadmin.constants.EmailTemplateType;
import eu.europa.csp.vcbadmin.model.EmailTemplate;
import eu.europa.csp.vcbadmin.model.User;
import eu.europa.csp.vcbadmin.repository.EmailTemplateRepository;
import eu.europa.csp.vcbadmin.repository.UserRepository;

@Controller
@RequestMapping("/register")
public class RegisterController {
	private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

	private UserRepository userRepository;

	private PasswordEncoder passwordEncoder;

	private EmailTemplateRepository emailTemplateRepository;

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
		userForm.setPassword(passwordEncoder.encode(userForm.getPassword()));
		log.info("Creating user {}", userForm);
		User user = userRepository.save(userForm);
		
		log.debug("Constructing init invitation email for user {}",user.getEmail());
		EmailTemplate et = new EmailTemplate();
		et.setSubject("Meeting invitation: [(${meeting_date})] [(${meeting_time})]");
		String content = new Scanner(invitationHTML.getInputStream(), "utf-8").useDelimiter("\\Z").next();
		et.setContent(content);
		et.setType(EmailTemplateType.INVITATION);
		et.setUser(user);
		EmailTemplate invitation = emailTemplateRepository.save(et);
		
		log.debug("Constructing init cancellation email for user {}",user.getEmail());
		et = new EmailTemplate();
		et.setSubject("Meeting cancellation: [(${meeting_date})] [(${meeting_time})]");
		content = new Scanner(cancellationHTML.getInputStream(), "utf-8").useDelimiter("\\Z").next();
		et.setContent(content);
		et.setType(EmailTemplateType.CANCELLATION);
		et.setUser(user);
		EmailTemplate cancellation = emailTemplateRepository.save(et);
		
		user.setInvitation(invitation);
		user.setCancellation(cancellation);
		
		userRepository.save(user);
		
		return "redirect:/login";
	}

}
