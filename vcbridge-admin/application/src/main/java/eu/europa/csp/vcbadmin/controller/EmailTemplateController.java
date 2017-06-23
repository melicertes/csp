package eu.europa.csp.vcbadmin.controller;

import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.europa.csp.vcbadmin.model.EmailTemplatesForm;
import eu.europa.csp.vcbadmin.model.User;
import eu.europa.csp.vcbadmin.repository.UserRepository;

@Controller
public class EmailTemplateController {
	private static final Logger log = LoggerFactory.getLogger(EmailTemplateController.class);

	@Autowired
	UserRepository userRepository;
	@Value(value = "classpath:templates/email/invitation.html")
	private Resource invitationHTML;

	@Value(value = "classpath:templates/email/cancellation.html")
	private Resource cancellationHTML;

	@GetMapping("/emailTemplates")
	public String showForm(Model model, Authentication auth) {
		Optional<User> user = userRepository.findByEmail(auth.getName());
		model.addAttribute("emailTemplates", EmailTemplatesForm.fromUser(user.get()));
		return "emailTemplate";
	}

	@PostMapping("/saveEmailTemplates")
	public String saveForm(@Valid @ModelAttribute("emailTemplates") EmailTemplatesForm emailTemplates,
			BindingResult bindingResult, Authentication auth,
			@RequestParam(value = "reset", required = false) String reset) throws IOException {

		if (reset == null && bindingResult.hasErrors()) {
			log.warn("Email template validation returned errors:\n{}", bindingResult.getAllErrors());
			return "emailTemplate";
		}
		log.debug("Email templates validated ok: {}", emailTemplates);

		Optional<User> user = userRepository.findByEmail(auth.getName());
		if (reset != null) {
			user.get().getInvitation().setSubject("Meeting invitation: [(${meeting_date})] [(${meeting_time})]");
			String content = new Scanner(invitationHTML.getInputStream(), "utf-8").useDelimiter("\\Z").next();
			user.get().getInvitation().setContent(content);

			user.get().getCancellation().setSubject("Meeting cancellation: [(${meeting_date})] [(${meeting_time})]");
			content = new Scanner(cancellationHTML.getInputStream(), "utf-8").useDelimiter("\\Z").next();
			user.get().getCancellation().setContent(content);

		} else {
			user.get().getInvitation().setSubject(emailTemplates.getInvitation().getSubject());
			user.get().getInvitation().setContent(emailTemplates.getInvitation().getContent());
			user.get().getCancellation().setSubject(emailTemplates.getCancellation().getSubject());
			user.get().getCancellation().setContent(emailTemplates.getCancellation().getContent());
		}

		userRepository.save(user.get());
		return "redirect:/listMeeting/scheduled";
	}
}
