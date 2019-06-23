package com.intrasoft.csp.vcb.teleconf.controller;

import com.intrasoft.csp.vcb.commons.constants.MeetingStatus;
import com.intrasoft.csp.vcb.commons.model.Meeting;
import com.intrasoft.csp.vcb.commons.model.Participant;
import com.intrasoft.csp.vcb.teleconf.repository.MeetingRepository;
import com.intrasoft.csp.vcb.teleconf.repository.ParticipantRepository;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PageController {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    MeetingRepository meetingRepository;

    @Autowired
    ParticipantRepository participantRepository;


    @GetMapping({"/", "index"})
    //public ModelAndView index(Model model, @RequestParam(value = "uid", required = true) String uid, HttpServletRequest request) {
    public ModelAndView index(Model model, @RequestParam(value = "uid", required = true) String uid) {
        String view;
        model = init(model);
        model.addAttribute("uid", uid);

        //set uid to session
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        session.setAttribute("uid", uid);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Participant participant = participantRepository.findByUsername(auth.getName());
        model.addAttribute("participant", participant);

        Meeting meeting = meetingRepository.findByUid(uid);
        /*
        Pending, Running, Expired, Completed, Cancel, Error
         */
        if (meeting.getStatus().equals(MeetingStatus.Running) || meeting.getStatus().equals(MeetingStatus.Pending)) {
            /*
            Participant not assigned to the meeting
             */
            if (!participant.getMeeting().equals(meeting)) {
                model = notallowedMeeting(model, meeting);
                view = "forbidden";
            } else {
                model = activeMeeting(model, meeting);
                view = "index";
            }
        }
        else {
            model = inactiveMeeting(model, meeting);
            view = "expiredcompletedcancelerror";
        }



        return new ModelAndView(view, "", model);
    }

    @GetMapping("/login")
    public String login(Principal principal, @Valid @ModelAttribute RedirectModel model, BindingResult result) {
        if (!result.hasErrors() && principal != null) {
            // do not redirect for absolute URLs (i.e. https://evil.com)
            // do not redirect if we are not authenticated
            return "redirect:" + model.getContinue();
        }
        return "login";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handle404Exception(NoHandlerFoundException ex, Model model) {
        model = init(model);
        return new ModelAndView("error", "", model);
    }


    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        String uid = (String)session.getAttribute("uid");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:" + "login?continue=/?uid=" + uid;
    }


    private Model init(Model m) {
        return m;
    }

    private Model activeMeeting(Model model, Meeting meeting) {
        List<Participant> participants = participantRepository.findByMeetingId(meeting.getId());

        //participants list for UI
        String p = "";
        for (Participant pp : participants) {
            p += "<strong>" + pp.getFullname() + "</strong>&nbsp;&nbsp;" + pp.getEmail() + "<br/>";
        }

        //meeting information for UI
        String m = "";
        m += "Subject: <strong>" + meeting.getSubject() + "</strong><br/>";
        m += "Scheduled Start: <strong>" + meeting.getStart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm ZZZ")) + "</strong><br/>";
        m += "Scheduled End:&nbsp; <strong>" + meeting.getExpectedEnd().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm ZZZ")) + "</strong><br/>";


        model.addAttribute("meeting", meeting);
        model.addAttribute("participants", p);
        model.addAttribute("meetingInfo", m);

        model.addAttribute("title", "Home" + messageSource.getMessage("title", null, null));
        model.addAttribute("description", messageSource.getMessage("description", null, null));
        model.addAttribute("keywords", messageSource.getMessage("keywords", null, null));

        return model;
    }

    private Model inactiveMeeting(Model model, Meeting meeting) {
        model.addAttribute("meetingStatus", meeting.getStatus());

        if (meeting.getStatus().equals(MeetingStatus.Expired))
            model.addAttribute("meetingDetails", "Requested meeting has expired!");
        if (meeting.getStatus().equals(MeetingStatus.Completed))
            model.addAttribute("meetingDetails", "Requested meeting has been completed since: " + meeting.getExpectedEnd().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm ZZZ")));
        if (meeting.getStatus().equals(MeetingStatus.Cancel))
            model.addAttribute("meetingDetails", "Requested meeting has been cancelled by the organizer!");
        if (meeting.getStatus().equals(MeetingStatus.Error))
            model.addAttribute("meetingDetails", "There was an error initiating this meeting. Please contact administrator.");

        model.addAttribute("title", meeting.getStatus() + messageSource.getMessage("title", null, null));
        model.addAttribute("description", messageSource.getMessage("description", null, null));
        model.addAttribute("keywords", messageSource.getMessage("keywords", null, null));

        return model;
    }

    private Model notallowedMeeting(Model model, Meeting meeting) {
        model.addAttribute("title", meeting.getStatus() + messageSource.getMessage("title", null, null));
        model.addAttribute("description", messageSource.getMessage("description", null, null));
        model.addAttribute("keywords", messageSource.getMessage("keywords", null, null));

        return model;
    }

}


class RedirectModel {
    @Pattern(regexp="^/([^/].*)?$")
    @NotBlank
    private String continueUrl;

    public void setContinue(String continueUrl) {
        this.continueUrl = continueUrl;
    }

    public String getContinue() {
        return continueUrl;
    }
}