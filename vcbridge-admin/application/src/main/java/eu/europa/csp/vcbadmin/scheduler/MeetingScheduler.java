package eu.europa.csp.vcbadmin.scheduler;

import java.time.ZonedDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import eu.europa.csp.vcbadmin.constants.MeetingScheduledTaskType;
import eu.europa.csp.vcbadmin.model.MeetingScheduledTask;
import eu.europa.csp.vcbadmin.repository.MeetingScheduledTaskRepository;
import eu.europa.csp.vcbadmin.service.MeetingNotFound;
import eu.europa.csp.vcbadmin.service.MeetingService;

@Component
public class MeetingScheduler {
	private static final Logger log = LoggerFactory.getLogger(MeetingScheduler.class);
	@Autowired
	private MeetingScheduledTaskRepository meetingScheduledTaskRepository;
	@Autowired
	private MeetingService meetingService;

	@Scheduled(fixedRate = 60000)
	public void checkExpired() {
		List<MeetingScheduledTask> tasks = meetingScheduledTaskRepository
				.findByTaskTimeLessThanAndCompletedIsFalse(ZonedDateTime.now());
		log.info("Scheduler is now checking for MeetingScheduledTask(s)...");

		for (MeetingScheduledTask task : tasks) {
			log.info("The scheduled task: {}", task);
			if (task.getTaskType().equals(MeetingScheduledTaskType.START_MEETING)) {
				// openfire and mail agent
			} else {
				try {
					meetingService.completeMeeting(task.getMeeting());
				} catch (MeetingNotFound e) {
					log.error("Scheduler: Meeting not found", e);
				}
			}
		}
	}
}
