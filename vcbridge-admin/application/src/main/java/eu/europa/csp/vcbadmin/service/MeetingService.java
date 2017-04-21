package eu.europa.csp.vcbadmin.service;

import java.util.Collection;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.csp.vcbadmin.constants.MeetingStatus;
import eu.europa.csp.vcbadmin.model.Meeting;
import eu.europa.csp.vcbadmin.model.MeetingScheduledTask;
import eu.europa.csp.vcbadmin.repository.MeetingRepository;
import eu.europa.csp.vcbadmin.repository.MeetingScheduledTaskRepository;

@Service
public class MeetingService {
	@Autowired
	MeetingRepository meetingRepository;
	@Autowired
	MeetingScheduledTaskRepository meetingScheduledTaskRepository;

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
}
