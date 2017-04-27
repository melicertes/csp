package eu.europa.csp.vcbadmin.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import eu.europa.csp.vcbadmin.model.EmailTemplatesForm;
import eu.europa.csp.vcbadmin.model.User;
import eu.europa.csp.vcbadmin.repository.UserRepository;

@Controller
public class EmailTemplateController {
	private static final Logger log = LoggerFactory.getLogger(EmailTemplateController.class);

	@Autowired
	UserRepository userRepository;

	@GetMapping("/emailTemplates")
	public String showForm(Model model, Authentication auth) {
		Optional<User> user = userRepository.findByEmail(auth.getName());
		model.addAttribute("emailTemplates", EmailTemplatesForm.fromUser(user.get()));
		return "emailTemplate";
	}

	@PostMapping("/saveEmailTemplates")
	public String saveForm(@Valid @ModelAttribute("emailTemplates") EmailTemplatesForm emailTemplates,
			BindingResult bindingResult, Authentication auth) {

		if (bindingResult.hasErrors()) {
			log.warn("Email template validation returned errors:\n{}",bindingResult.getAllErrors());
			return "emailTemplate";
		}

		log.debug("Email templates validated ok: {}", emailTemplates);

		Optional<User> user = userRepository.findByEmail(auth.getName());
		user.get().getInvitation().setSubject(emailTemplates.getInvitation().getSubject());
		user.get().getInvitation().setContent(emailTemplates.getInvitation().getContent());
		user.get().getCancellation().setSubject(emailTemplates.getCancellation().getSubject());
		user.get().getCancellation().setContent(emailTemplates.getCancellation().getContent());

		userRepository.save(user.get());
		return "redirect:/listMeeting";
	}
}
