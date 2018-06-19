package com.intrasoft.csp.vcb.admin.service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.intrasoft.csp.vcb.admin.service.exception.MeetingNotFound;
import com.intrasoft.csp.vcb.admin.config.VcbadminProperties;
import com.intrasoft.csp.vcb.admin.repository.MeetingScheduledTaskRepository;
import com.intrasoft.csp.vcb.commons.constants.MeetingScheduledTaskType;
import com.intrasoft.csp.vcb.commons.constants.MeetingStatus;
import com.intrasoft.csp.vcb.commons.model.Meeting;
import com.intrasoft.csp.vcb.commons.model.MeetingScheduledTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import com.intrasoft.csp.vcb.admin.repository.MeetingRepository;

@Service
public class MeetingService {

	private static final Logger log = LoggerFactory.getLogger(MeetingService.class);

	@Autowired
	MeetingRepository meetingRepository;

	@Autowired
	MeetingScheduledTaskRepository meetingScheduledTaskRepository;

	@Autowired
	EmailService emailService;

	@Autowired
	VcbadminProperties vcbadminProperties;

	// @Autowired
	// EmailTemplateRepository emailTemplateRepository;

	@Transactional
	public Meeting createMeeting(Meeting meeting, Collection<MeetingScheduledTask> tasks) {
		meeting = meetingRepository.save(meeting);
		for (MeetingScheduledTask task : tasks) {
			task.setMeeting(meeting);
		}
		meetingScheduledTaskRepository.save(tasks);
		log.info("Meeting UID: " + meeting.getUid() + " created, Subject: " + meeting.getSubject() + ", Start: " + meeting.getStart());
		return meeting;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void cancelMeetings(Long[] ids) throws MeetingNotFound, IOException {
		LinkedList<Meeting> meetings = new LinkedList<>();
		for (long id : ids) {
			Meeting m = meetingRepository.findOne(id);
			if (m == null) {
				throw new MeetingNotFound("Meeting with id " + id + " not found..");
			}
			meetings.add(m);
			m.setStatus(MeetingStatus.Cancel);
			log.info("Meeting UID: " + m.getUid() + " cancelled, Subject: " + m.getSubject() + ", Start: " + m.getStart());
		}
		meetingRepository.save(meetings);
		log.debug("Sending cancellation emails...");
		meetings.clear();
		for (long id : ids) {
			Meeting m = meetingRepository.findOne(id);
			if (m == null) {
				throw new MeetingNotFound("Meeting with id " + id + " not found..");
			}
			meetings.add(m);
			List<MeetingScheduledTask> already_started = m.getScheduledTasks().stream()
					.filter(st -> st.getCompleted() && st.getTaskType().equals(MeetingScheduledTaskType.START_MEETING))
					.collect(Collectors.toList());
			if (!already_started.isEmpty()) {
				log.debug("Sending cancellation emails for meeting {}", m.getId());
				emailService.prepareAndSend(m.getUser().getCancellation(), m);
			} else {
				log.debug("Not sending email for meeting {} because email invitations haven't been sent yet", m.getId());
			}
			m.getScheduledTasks().forEach(st -> st.setCompleted(true));
		}

		meetingRepository.save(meetings);
	}

	@Transactional
	public void completeMeeting(Meeting meeting) throws MeetingNotFound {
		Meeting m = meetingRepository.findOne(meeting.getId());
		if (m == null) {
			throw new MeetingNotFound("Meeting with id " + meeting.getId() + " not found..");
		}
		m.setStatus(MeetingStatus.Completed);
		m.getScheduledTasks().forEach(st -> st.setCompleted(true));
		meetingRepository.save(m);
	}

	@Transactional
	public void failMeeting(Meeting meeting) throws MeetingNotFound {
		meeting.setStatus(MeetingStatus.Error);
		meeting.getScheduledTasks().forEach(t -> t.setFailed(vcbadminProperties.getMaxTaskRetries()));
		meetingRepository.save(meeting);
	}

	@Transactional
	public boolean retryMeeting(Long id) {
		List<MeetingScheduledTask> tasks = meetingScheduledTaskRepository
				.findByMeetingIdAndCompletedIsFalseAndMeetingStartGreaterThan(id, ZonedDateTime.now());
		if (tasks.isEmpty())
			return false;
		for (MeetingScheduledTask task : tasks) {
			task.setFailed(0);
		}
		log.debug("Set task trials to 0 for {} meeting tasks", tasks.size());
		meetingScheduledTaskRepository.save(tasks);
		return true;
	}
}
