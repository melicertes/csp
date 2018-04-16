package com.intrasoft.csp.vcb.teleconf.controller;

import com.intrasoft.csp.vcb.teleconf.repository.MeetingRepository;
import com.intrasoft.csp.vcb.teleconf.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;

@Controller
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AppController {

    @Value("${jitsi.protocol}")
    private String protocol;

    @Value("${jitsi.host}")
    private String host;

    @Value("${jitsi.port}")
    private Integer port;


    @Autowired
    MeetingRepository meetingRepository;

    @Autowired
    ParticipantRepository participantRepository;



    @RequestMapping(value = "/app/vcb-link", method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity link() {

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);

        String uid = (String)session.getAttribute("uid");

        if (uid != null) {
            String link = protocol + "://" + host + ":" + port + "/" + uid.replaceAll("-", "");
            return new ResponseEntity<>(link, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/app/vcb-status", method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity sttaus() {

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);

        String uid = (String)session.getAttribute("uid");

        if (uid != null) {
            return new ResponseEntity<>(meetingRepository.findByUid(uid).getStatus().name(), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
    }




}
