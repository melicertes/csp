package eu.europa.csp.vcbadmin.controller;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.europa.csp.vcbadmin.config.OpenfireProperties;
import eu.europa.csp.vcbadmin.config.editors.DurationEditor;
import eu.europa.csp.vcbadmin.config.editors.ZoneDateTimeEditor;
import eu.europa.csp.vcbadmin.constants.MeetingScheduledTaskType;
import eu.europa.csp.vcbadmin.constants.MeetingStatus;
import eu.europa.csp.vcbadmin.model.EmailTemplate;
import eu.europa.csp.vcbadmin.model.Meeting;
import eu.europa.csp.vcbadmin.model.MeetingForm;
import eu.europa.csp.vcbadmin.model.MeetingScheduledTask;
import eu.europa.csp.vcbadmin.model.User;
import eu.europa.csp.vcbadmin.repository.EmailTemplateRepository;
import eu.europa.csp.vcbadmin.repository.MeetingRepository;
import eu.europa.csp.vcbadmin.repository.UserRepository;
import eu.europa.csp.vcbadmin.service.EmailService;
import eu.europa.csp.vcbadmin.service.MeetingNotFound;
import eu.europa.csp.vcbadmin.service.MeetingService;

@Controller
public class MeetingController {
	private static final Logger log = LoggerFactory.getLogger(MeetingController.class);
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	@Autowired
	MeetingRepository meetingRepository;

	@Autowired
	MeetingService meetingService;

	@Autowired
	UserRepository userRepository;
	
	private OpenfireProperties openFireProperties;

	
	
	@GetMapping("/createMeeting")
	public String showForm(MeetingForm formMeeting) {
		return "createMeeting";
	}

	@Autowired
	public MeetingController(OpenfireProperties properties) {
		this.openFireProperties = properties;
	}

	@InitBinder("meetingForm")
	public void dataBinding(WebDataBinder binder) {

		binder.registerCustomEditor(ZonedDateTime.class,
				new ZoneDateTimeEditor(DateTimeFormatter.ISO_ZONED_DATE_TIME, true));
		binder.registerCustomEditor(Duration.class, new DurationEditor("HH:mm", true));

		// binder.addCustomFormatter(new DurationFormatter("HH:mm"),
		// "duration");
		// binder.addCustomFormatter(new
		// ZoneDateTimeFormatter(DateTimeFormatter.ISO_ZONED_DATE_TIME),"start");
	}

	@PostMapping("/createMeeting")
	public String checkPersonInfo(@Valid @ModelAttribute("meetingForm") MeetingForm meetingForm,
			BindingResult bindingResult, Authentication auth) {
		List<String> emails = meetingForm.getEmails().stream().filter(s -> s != null && !s.isEmpty())
				.collect(Collectors.toList());
		if (emails.isEmpty()) {
			bindingResult.rejectValue("emails", "errors.emails.empty", "Please provide at least one participant");
		} else {
			for (String email : emails) {
				Pattern pattern = Pattern.compile(EMAIL_PATTERN);
				Matcher matcher = pattern.matcher(email);
				if (!matcher.matches()) {
					bindingResult.rejectValue("emails", "errors.emails.malformed", "Some emails are not well-formed");
				}
			}
		}
		if (meetingForm.getDuration() != null) {
			if (meetingForm.getDuration().compareTo(Duration.ofHours(8)) > 0
					|| meetingForm.getDuration().compareTo(Duration.ofMinutes(30)) < 0) {
				bindingResult.rejectValue("duration", "errors.duration.hour.limits",
						"Duration hours must [00:30-8:00]");
			}
		}
		if (bindingResult.hasErrors()) {
			return "createMeeting";
		}

		log.debug("Meeting validated ok: {}", meetingForm);
		log.debug(meetingForm.toString());

		Optional<User> user = userRepository.findByEmail(auth.getName());
		Meeting m = MeetingForm.createMeetingFromForm(meetingForm, user.get());
		String url = String.format("https://%s:%s/ofmeet/?r=%s", openFireProperties.getVideobridgeHost(),
				openFireProperties.getVideobridgeEndpointPort(), m.getRoom());
		m.setUrl(url);
		log.info("Start of meeting: {}", m.getStart());
		log.info("Now - 30 min: {}", ZonedDateTime.now().minusMinutes(30));
		if (m.getStart().minusMinutes(30).isBefore(ZonedDateTime.now())) {
			meetingService.createMeeting(m,
					Arrays.asList(
							MeetingScheduledTask.getNewCompleted(MeetingScheduledTaskType.START_MEETING,
									m.getStart().minusMinutes(30)),
							new MeetingScheduledTask(MeetingScheduledTaskType.END_MEETING,
									m.getExpectedEnd().plusMinutes(30))));
			// TODO fire immediately the email service and openfire stuff
		} else {
			meetingService.createMeeting(m,
					Arrays.asList(
							new MeetingScheduledTask(MeetingScheduledTaskType.START_MEETING,
									m.getStart().minusMinutes(30)),
							new MeetingScheduledTask(MeetingScheduledTaskType.END_MEETING,
									m.getExpectedEnd().plusMinutes(30))));
		}
		return "redirect:/listMeeting";
	}

	@PostMapping("/cancelMeeting")
	public String checkPersonInfo(
			@RequestParam(value = "id") @Size(min = 1, message = "Please select at least one meeting to cancel") Long[] ids,
			Model model) {
		try {
			meetingService.cancelMeetings(ids);
		} catch (MeetingNotFound e) {
			model.addAttribute("error", e.getMessage());
		}
		return "redirect:/listMeeting";
	}
	
	@GetMapping("/listMeeting")
	public String showMeeting(Model model, Authentication auth) {
		Iterable<Meeting> meetings = meetingRepository.findByUserEmailAndStatusOrStatus(auth.getName(),
				MeetingStatus.Pending, MeetingStatus.Running);
		Iterable<Meeting> pastMeetings = meetingRepository.findByUserEmailAndStatusOrStatusOrStatus(auth.getName(),
				MeetingStatus.Cancel, MeetingStatus.Completed, MeetingStatus.Expired);
		model.addAttribute("meetings", meetings);
		model.addAttribute("pastMeetings", pastMeetings);
		return "listMeeting";
	}
}
