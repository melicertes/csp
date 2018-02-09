package eu.europa.csp.vcbadmin.controller;

import eu.europa.csp.vcbadmin.model.*;
import eu.europa.csp.vcbadmin.model.EmailTemplatesForm.EmailTmpltForm;
import eu.europa.csp.vcbadmin.repository.EmailTemplateRepository;
import eu.europa.csp.vcbadmin.repository.UserRepository;
import eu.europa.csp.vcbadmin.service.EmailTemplateService;
import eu.europa.csp.vcbadmin.service.exception.CannotRemainWithNoActiveTemplates;
import eu.europa.csp.vcbadmin.service.exception.EmailTemplateNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Scanner;

@Controller
public class EmailTemplateController {
    private static final Logger log = LoggerFactory.getLogger(EmailTemplateController.class);

    @Autowired
    UserRepository userRepository;
    @Value(value = "${event.show.timezone.default:Europe/Athens}")
    String tz_default;
    @Value(value = "classpath:templates/email/invitation.html")
    private Resource invitationHTML;
    @Value(value = "classpath:templates/email/cancellation.html")
    private Resource cancellationHTML;
    @Autowired
    private EmailTemplateRepository emailTemplateRepository;
    @Autowired
    private EmailTemplateService emailTemplateService;

    @GetMapping(value = {"/listEmailTemplates"})
    public String listTemplates(Model model, Authentication auth,
                                @PageableDefault(value = 10, page = 0) Pageable pageable) {
        String user_tz = ((CustomUserDetails) auth.getPrincipal()).getTimezone();
        try {
            ZoneId.of(user_tz);
        } catch (Exception e) {
            user_tz = tz_default;
        }
        model.addAttribute("userTZ", user_tz);
        PageWrapper<EmailTemplate> templates;
        templates = new PageWrapper<>(emailTemplateRepository.findByUserEmail(auth.getName(), pageable),
                "/listEmailTemplates");
        model.addAttribute("templates", templates);
        return "listTemplate";
    }

    @PostMapping("/deleteEmailTemplates")
    public String deleteTemplates(
            @RequestParam(value = "id") @Size(min = 1, message = "Please select at least one meeting to cancel") Long[] ids,
            RedirectAttributes model, Authentication auth) throws IOException {
        try {
            emailTemplateService.deleteEmailTemplates(ids, auth);
        } catch (EmailTemplateNotFound e) {
            model.addFlashAttribute("errors", Collections.singleton(e.getMessage()));
        } catch (CannotRemainWithNoActiveTemplates e) {
            model.addFlashAttribute("errors", Collections.singleton(e.getMessage()));
        }
        return "redirect:/listEmailTemplates";
    }

    @GetMapping("/emailTemplate")
    public String showForm(@RequestParam(required = false) Long id, Model model, Authentication auth) {
        String user_tz = ((CustomUserDetails) auth.getPrincipal()).getTimezone();
        try {
            ZoneId.of(user_tz);
        } catch (Exception e) {
            user_tz = tz_default;
        }
        model.addAttribute("userTZ", user_tz);
        EmailTemplate t;
        if (id == null) {
            t = new EmailTemplate();
        } else {
            t = emailTemplateRepository.findOneByIdAndUserEmail(id, auth.getName());
            if (t == null) {
                throw new AccessDeniedException("No permissions for this template");
            }
        }
        model.addAttribute("emailTmpltForm", t);
        return "createTemplate";
    }

    @PostMapping("/emailTemplate")
    public String createMeeting(@Valid @ModelAttribute("emailTmpltForm") EmailTmpltForm emailTmpltForm, Model model,
                                BindingResult bindingResult, Authentication auth) {
        EmailTemplate t;
        if (emailTmpltForm.getId() != null) {
            t = emailTemplateRepository.findOneByIdAndUserEmail(emailTmpltForm.getId(), auth.getName());
            if (t == null) {
                throw new AccessDeniedException("No permissions for this template");
            }
        } else {
            t = new EmailTemplate();
        }
        if (bindingResult.hasErrors()) {
            log.debug("Form has errors: " + bindingResult.getAllErrors().toString());
            return "createTemplate";
        }
        t.setUser(userRepository.findByEmail(auth.getName()).get());
        t.setActive(emailTmpltForm.getActive());
        t.setContent(emailTmpltForm.getContent());
        t.setModified(ZonedDateTime.now());
        t.setName(emailTmpltForm.getName());
        t.setSubject(emailTmpltForm.getSubject());
        t.setType(emailTmpltForm.getType());
        try {
            emailTemplateService.saveEmailTemplate(t, auth);
        } catch (CannotRemainWithNoActiveTemplates e) {
            model.addAttribute("errors", Collections.singleton(e.getMessage()));
            return "createTemplate";
        } catch (DataIntegrityViolationException ex) {
            bindingResult.rejectValue("name", "email.template.name", "Duplicate name");
            return "createTemplate";
        } catch (Exception ex) {
            model.addAttribute("errors", Collections.singleton(ex.getMessage()));
            return "createTemplate";
        }
        return "redirect:/listEmailTemplates";

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
        return "redirect:/";
    }
}
