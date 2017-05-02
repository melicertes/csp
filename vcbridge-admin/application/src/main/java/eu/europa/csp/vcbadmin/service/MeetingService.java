package eu.europa.csp.vcbadmin.service;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.csp.vcbadmin.config.VcbadminProperties;
import eu.europa.csp.vcbadmin.constants.MeetingScheduledTaskType;
import eu.europa.csp.vcbadmin.constants.MeetingStatus;
import eu.europa.csp.vcbadmin.model.Meeting;
import eu.europa.csp.vcbadmin.model.MeetingScheduledTask;
import eu.europa.csp.vcbadmin.repository.MeetingRepository;
import eu.europa.csp.vcbadmin.repository.MeetingScheduledTaskRepository;
import eu.europa.csp.vcbadmin.service.exception.MeetingNotFound;

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
		return meeting;
	}

	@Transactional
	public void cancelMeetings(Long[] ids) throws MeetingNotFound {
		LinkedList<Meeting> meetings = new LinkedList<>();
		for (long id : ids) {
			Meeting m = meetingRepository.findOne(id);
			if (m == null) {
				throw new MeetingNotFound("Meeting with id " + id + " not found..");
			}
			meetings.add(m);
			m.setStatus(MeetingStatus.Cancel);
			m.getScheduledTasks().forEach(st -> st.setCompleted(true));
		}
		meetingRepository.save(meetings);
		log.info("Sending cancellation emails...");
		for (long id : ids) {
			Meeting m = meetingRepository.findOne(id);
			if (m == null) {
				throw new MeetingNotFound("Meeting with id " + id + " not found..");
			}
			for (MeetingScheduledTask t : m.getScheduledTasks()) {
				if (t.getTaskType().equals(MeetingScheduledTaskType.START_MEETING)) {
					if (t.getCompleted() == true) {
						log.info("Sending cancellation emails for meeting {}", m.getId());
						emailService.prepareAndSend(m.getUser().getCancellation(), m);
						break;
					}
				}
			}
		}
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
		log.info("Set task trials to 0 for {} meeting tasks", tasks.size());
		meetingScheduledTaskRepository.save(tasks);
		return true;
	}
}
