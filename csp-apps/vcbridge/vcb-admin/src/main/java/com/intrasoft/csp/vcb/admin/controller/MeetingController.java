package com.intrasoft.csp.vcb.admin.controller;

import java.io.IOException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import com.intrasoft.csp.vcb.admin.config.TeleconfProperties;
import com.intrasoft.csp.vcb.admin.config.VcbadminProperties;
import com.intrasoft.csp.vcb.admin.config.editors.DurationEditor;
import com.intrasoft.csp.vcb.admin.config.editors.ZoneDateTimeEditor;
import com.intrasoft.csp.vcb.admin.model.MeetingForm;
import com.intrasoft.csp.vcb.admin.model.ParticipantForm;
import com.intrasoft.csp.vcb.admin.repository.MeetingRepository;
import com.intrasoft.csp.vcb.admin.repository.UserRepository;
import com.intrasoft.csp.vcb.admin.service.MeetingService;
import com.intrasoft.csp.vcb.admin.service.exception.MeetingNotFound;
import com.intrasoft.csp.vcb.admin.model.CustomUserDetails;
import com.intrasoft.csp.vcb.commons.constants.MeetingScheduledTaskType;
import com.intrasoft.csp.vcb.commons.constants.MeetingStatus;
import com.intrasoft.csp.vcb.commons.model.Meeting;
import com.intrasoft.csp.vcb.commons.model.MeetingScheduledTask;
import com.intrasoft.csp.vcb.commons.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.intrasoft.csp.vcb.admin.model.PageWrapper;

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

	@Autowired
	TeleconfProperties jitsiProperties;

	@Autowired
	VcbadminProperties vcbadminProperties;

	@Value(value = "${event.show.timezone.default:Europe/Athens}")
	String tz_default;



	@InitBinder("meetingForm")
	public void dataBinding(WebDataBinder binder) {

		binder.registerCustomEditor(ZonedDateTime.class,
				new ZoneDateTimeEditor(DateTimeFormatter.ISO_ZONED_DATE_TIME, true));
		binder.registerCustomEditor(Duration.class, new DurationEditor("H:mm", true));

		// binder.addCustomFormatter(new DurationFormatter("HH:mm"),
		// "duration");
		// binder.addCustomFormatter(new
		// ZoneDateTimeFormatter(DateTimeFormatter.ISO_ZONED_DATE_TIME),"start");
	}

	@GetMapping("/createMeeting")
	public String showForm(Model model, Authentication auth) {
		CustomUserDetails principal = ((CustomUserDetails) auth.getPrincipal());
		String user_tz = principal.getTimezone();
		try {
			ZoneId.of(user_tz);
		} catch (Exception e) {
			user_tz = tz_default;
		}
		MeetingForm formMeeting = new MeetingForm();
		LinkedList<ParticipantForm> l = new LinkedList<>();
		l.add(new ParticipantForm(principal.getFirstname(), principal.getLastname(), principal.getUsername()));
		formMeeting.setEmails(l);
		model.addAttribute("userTZ", user_tz);
		model.addAttribute("meetingForm", formMeeting);
		return "createMeeting";
	}

	@PostMapping("/createMeeting")
	public String createMeeting(@Valid @ModelAttribute("meetingForm") MeetingForm meetingForm,
			BindingResult bindingResult, Authentication auth, Model model) {
		List<ParticipantForm> emails = meetingForm.getEmails().stream()
				.filter(s -> Objects.nonNull(s) && s.getEmail() != null && (!s.getEmail().trim().isEmpty()))
				.collect(Collectors.toList());

		if (emails.size() == 0) {
			bindingResult.rejectValue("emails", "errors.participants.number",
					"At least one participant (either Team Contact OR External) must be added");
		}
		if (meetingForm.getDuration() != null) {
			if (meetingForm.getDuration().compareTo(Duration.ofMinutes(vcbadminProperties.getMaxMeetingDuration())) > 0
					|| meetingForm.getDuration()
							.compareTo(Duration.ofMinutes(vcbadminProperties.getMinMeetingDuration())) < 0) {
				bindingResult.rejectValue("duration", "errors.duration.hour.limits",
						"Duration must be withing [00:30-8:00]");
			}
		}
		if (meetingForm.getStart() != null) {
			if (meetingForm.getStart().isBefore(ZonedDateTime.now())) {
				bindingResult.rejectValue("start", "errors.start.must.be.after.now",
						"Start datetime cannot refer to past");
			}
		}


		meetingForm.setEmails(new LinkedList<>(emails)); // important: update
		// the correct email
		// list

		if (bindingResult.hasErrors()) {
			log.debug("{}", bindingResult.getAllErrors().toString());

			String user_tz = meetingForm.getTimeZone();
			try {
				ZoneId.of(user_tz);
			} catch (Exception e) {
				user_tz = tz_default;
			}
			model.addAttribute("userTZ", user_tz);
			if (bindingResult.hasFieldErrors("start")) {
				meetingForm.setStart(null);
				meetingForm.setStartDate(null);
			}
			model.addAttribute("meetingForm", meetingForm);
			return "createMeeting";
		}

		log.debug("Meeting validated ok: {}", meetingForm);
		log.debug(meetingForm.toString());

		Optional<User> user = userRepository.findByEmail(auth.getName());
		Meeting m = MeetingForm.createMeetingFromForm(meetingForm, user.get());
		String url = String.format("%s?uid=%s", jitsiProperties.buildURI(), m.getUid());
		m.setUrl(url);
		log.debug("Start of meeting: {}", m.getStart());
		log.debug("Now - 30 min: {}", ZonedDateTime.now().minusMinutes(30));

		ZonedDateTime invitationDate = ZonedDateTime.now()
				.plusMinutes(vcbadminProperties.getEmailNotifications().getWaitAfterSubmission());
		if (invitationDate.isAfter(
				m.getStart().minusMinutes(vcbadminProperties.getEmailNotifications().getMinTimeAllowedBefore()))) {
			invitationDate = m.getStart()
					.minusMinutes(vcbadminProperties.getEmailNotifications().getMinTimeAllowedBefore());
		} else if (invitationDate.isBefore(
				m.getStart().minusMinutes(vcbadminProperties.getEmailNotifications().getMaxTimeAllowedBefore()))) {
			invitationDate = m.getStart()
					.minusMinutes(vcbadminProperties.getEmailNotifications().getMaxTimeAllowedBefore());
		}
		meetingService.createMeeting(m, Arrays.asList(
				// new
				// MeetingScheduledTask(MeetingScheduledTaskType.START_MEETING,
				// m.getStart().minusMinutes(30))
				new MeetingScheduledTask(MeetingScheduledTaskType.START_MEETING, invitationDate),
				new MeetingScheduledTask(MeetingScheduledTaskType.END_MEETING, m.getExpectedEnd().plusMinutes(30))));
		return "redirect:/listMeeting/scheduled";
	}

	@PostMapping("/cancelMeeting")
	public String cancelMeeting(
			@RequestParam(value = "id") @Size(min = 1, message = "Please select at least one meeting to cancel") Long[] ids,
			Model model) throws IOException {
		try {
			meetingService.cancelMeetings(ids);
		} catch (MeetingNotFound e) {
			model.addAttribute("errors", e.getMessage());
		}

		return "redirect:/listMeeting/scheduled";
	}

	@GetMapping(value = { "/listMeeting/{type}", "/" })

	public String listMeeting(Model model, @PathVariable(name = "type", required = false) String past,
			Authentication auth, @PageableDefault(value = 10, page = 0) Pageable pageable) {
		PageWrapper<Meeting> meetings;
		Boolean isPast = "past".equals(past);
		if (isPast) {
			meetings = new PageWrapper<>(
					meetingRepository.findByUserEmailAndStatusOrStatusOrStatus(auth.getName(), MeetingStatus.Cancel,
							MeetingStatus.Completed, MeetingStatus.Expired, MeetingStatus.Error, pageable),
					"/listMeeting/" + past);
		} else {
			past = "scheduled";
			meetings = new PageWrapper<>(meetingRepository.findByUserEmailAndStatusOrStatus(auth.getName(),
					MeetingStatus.Pending, MeetingStatus.Running, pageable), "/listMeeting/" + past);
		}
		model.addAttribute("past", isPast);
		model.addAttribute("meetingType", isPast ? "Past Meetings" : "Scheduled Meetings");
		model.addAttribute("meetings", meetings);
		String user_tz = ((CustomUserDetails) auth.getPrincipal()).getTimezone();
		try {
			ZoneId.of(user_tz);
		} catch (Exception e) {
			user_tz = tz_default;
		}
		model.addAttribute("userTZ", user_tz);
		return "listMeeting";
	}

	@GetMapping(value = { "/retryMeeting" })
	public String retryMeeting(Long id, RedirectAttributes model, Authentication auth) {
		if (!meetingService.retryMeeting(id)) {
			model.addFlashAttribute("errors", Collections.singleton("Either too late to retry, or no such meeting"));
		}
		return "redirect:/listMeeting/scheduled";
	}
}
