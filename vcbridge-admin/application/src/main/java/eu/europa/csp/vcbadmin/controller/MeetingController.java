package eu.europa.csp.vcbadmin.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.europa.csp.vcbadmin.config.OpenfireProperties;
import eu.europa.csp.vcbadmin.model.Meeting;
import eu.europa.csp.vcbadmin.model.MeetingForm;
import eu.europa.csp.vcbadmin.repository.MeetingRepository;


@Controller
public class MeetingController {
	@Autowired
	MeetingRepository meetingRepository;
	private OpenfireProperties openFireProperties;
	
	@GetMapping("/createMeeting")
	public String showForm(MeetingForm formMeeting) {
		return "createMeeting";
	}
	
	@Autowired
	public MeetingController(OpenfireProperties properties) {
		this.openFireProperties=properties;
	}

	@PostMapping("/createMeeting")
	public String checkPersonInfo(@Valid MeetingForm meetingForm, BindingResult bindingResult) {

		System.out.println(bindingResult.getAllErrors());
		if (bindingResult.hasErrors()) {
			return "createMeeting";
		}

		System.out.println("Created!!!!!!");
		System.out.println(meetingForm);
		String url=String.format("https://%s:%s/ofmeet/?r=%s", openFireProperties.getVideobridgeHost(),openFireProperties.getVideobridgeHost(),openFireProperties.getMeetingRoom());
		
		meetingRepository.save(new Meeting(meetingForm,url));
		return "redirect:/";
	}
	
	@PostMapping("/cancelMeeting")
	public String checkPersonInfo(@RequestParam(value="id") Long[] ids) {
		
		return "redirect:/listMeeting";
	}

	@GetMapping("/listMeeting")
	public String showMeeting(Model model) {
		Iterable<Meeting> meetings = meetingRepository.findAll();
		System.out.println();
		model.addAttribute("meetings", meetings);
		return "listMeeting";
	}
}
