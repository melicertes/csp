package eu.europa.csp.vcbadmin.service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.csp.vcbadmin.constants.EmailTemplateType;
import eu.europa.csp.vcbadmin.model.EmailTemplate;
import eu.europa.csp.vcbadmin.model.User;
import eu.europa.csp.vcbadmin.repository.EmailTemplateRepository;
import eu.europa.csp.vcbadmin.repository.UserRepository;
import eu.europa.csp.vcbadmin.service.exception.CannotRemainWithNoActiveTemplates;
import eu.europa.csp.vcbadmin.service.exception.EmailTemplateNotFound;

@Service
public class EmailTemplateService {
	private static final Logger log = LoggerFactory.getLogger(EmailTemplateService.class);
	@Autowired
	EmailTemplateRepository emailTemplateRepository;
	@Autowired
	UserRepository userRepository;
	@Value(value = "classpath:templates/email/invitation.html")
	private Resource invitationHTML;

	@Value(value = "classpath:templates/email/cancellation.html")
	private Resource cancellationHTML;

	@Transactional
	public EmailTemplate saveEmailTemplate(EmailTemplate template, Authentication auth)
			throws CannotRemainWithNoActiveTemplates {
		List<EmailTemplate> existing = emailTemplateRepository.findByTypeAndActiveIsTrueAndUserEmail(template.getType(),
				auth.getName());
		if (template.getActive()) {
			existing.forEach(e -> e.setActive(false));
			emailTemplateRepository.save(existing);
			template.setActive(true);
		} else {
			if (existing.isEmpty()) {
				throw new CannotRemainWithNoActiveTemplates(
						"Cannot remain with no active email templates (" + template.getType() + ")");
			}
		}
		template = emailTemplateRepository.save(template);
		return template;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void deleteEmailTemplates(Long[] ids, Authentication auth)
			throws EmailTemplateNotFound, IOException, CannotRemainWithNoActiveTemplates {
		LinkedList<EmailTemplate> templates = new LinkedList<>();
		boolean recreate = false;
		int alltemplates_count = emailTemplateRepository.findByUserEmail(auth.getName()).size();
		int all_active_templates_invitation_count = emailTemplateRepository
				.findByTypeAndActiveIsTrueAndUserEmail(EmailTemplateType.INVITATION, auth.getName()).size();
		int all_active_templates_cancellation_count = emailTemplateRepository
				.findByTypeAndActiveIsTrueAndUserEmail(EmailTemplateType.CANCELLATION, auth.getName()).size();

		if (alltemplates_count == ids.length) {
			recreate = true;
		}

		for (long id : ids) {
			EmailTemplate m = emailTemplateRepository.findOneByIdAndUserEmail(id, auth.getName());
			if (m == null) {
				throw new EmailTemplateNotFound("Meeting with id " + id + " not found..");
			}
			templates.add(m);
		}
		if (templates.stream().filter(t -> t.getType().equals(EmailTemplateType.INVITATION) && t.getActive())
				.collect(Collectors.toList()).size() >= all_active_templates_invitation_count
				|| templates.stream().filter(t -> t.getType().equals(EmailTemplateType.CANCELLATION) && t.getActive())
						.collect(Collectors.toList()).size() >= all_active_templates_cancellation_count) {
			throw new CannotRemainWithNoActiveTemplates(
					"Cannot remain with no active templates at all (invitation and cancellation)");
		}
		emailTemplateRepository.deleteInBatch(templates);
		emailTemplateRepository.findAll();
		if (recreate) {
			User user = userRepository.findByEmail(auth.getName()).get();
			EmailTemplate et = new EmailTemplate("Auto-generated Invitation", true);
			et.setSubject("Invitation: [(${meeting_subject})]");
			String content = new Scanner(invitationHTML.getInputStream(), "utf-8").useDelimiter("\\Z").next();
			et.setContent(content);
			et.setType(EmailTemplateType.INVITATION);
			et.setUser(user);
			EmailTemplate invitation = emailTemplateRepository.save(et);

			log.debug("Constructing init cancellation email for user {}", user.getEmail());
			et = new EmailTemplate("Auto-generated Cancellation", true);
			et.setSubject("Cancelled: [(${meeting_subject})]");
			content = new Scanner(cancellationHTML.getInputStream(), "utf-8").useDelimiter("\\Z").next();
			et.setContent(content);
			et.setType(EmailTemplateType.CANCELLATION);
			et.setUser(user);
			EmailTemplate cancellation = emailTemplateRepository.save(et);
		}
		log.info("Deleted email templates...");
	}
}
