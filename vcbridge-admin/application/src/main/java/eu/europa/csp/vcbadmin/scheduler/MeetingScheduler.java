package eu.europa.csp.vcbadmin.scheduler;

import java.time.ZonedDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.csp.vcbadmin.config.VcbadminProperties;
import eu.europa.csp.vcbadmin.constants.MeetingScheduledTaskType;
import eu.europa.csp.vcbadmin.model.MeetingScheduledTask;
import eu.europa.csp.vcbadmin.repository.MeetingScheduledTaskRepository;
import eu.europa.csp.vcbadmin.service.EmailService;
import eu.europa.csp.vcbadmin.service.MeetingNotFound;
import eu.europa.csp.vcbadmin.service.MeetingService;
import eu.europa.csp.vcbadmin.service.OpenfireService;
import eu.europa.csp.vcbadmin.service.exception.OpenfireException;

@Component
public class MeetingScheduler {
	private static final Logger log = LoggerFactory.getLogger(MeetingScheduler.class);
	@Autowired
	private MeetingScheduledTaskRepository meetingScheduledTaskRepository;
	@Autowired
	private MeetingService meetingService;

	@Autowired
	RetryTemplate retryTemplate;

	@Autowired
	OpenfireService openfireService;

	@Autowired
	VcbadminProperties vcbadminProperties;
	
	@Autowired
	EmailService emailService;
	
	@Transactional
	@Scheduled(fixedRate = 60000)
	public void checkExpired() {
		List<MeetingScheduledTask> tasks = meetingScheduledTaskRepository
				.findByTaskTimeLessThanAndCompletedIsFalseAndFailedLessThan(ZonedDateTime.now(),
						vcbadminProperties.getMaxTaskRetries());
		log.info("Scheduler is now checking for MeetingScheduledTask(s)...");
		try {
			for (MeetingScheduledTask task : tasks) {
				log.info("Running scheduled task: {}", task);
				task.getMeeting().getParticipants().size();
				task.getMeeting().getUser();
				try {
					if (task.getTaskType().equals(MeetingScheduledTaskType.START_MEETING)) {
						log.info("Creating meeting...");
						openfireService.createMeeting(task.getMeeting());
						log.info("Meeting created...");

						log.info("Changing task to completed...");
						task.setCompleted(true);
						meetingScheduledTaskRepository.save(task);
						log.info("Task changed...");
						log.info("Sending invitation emails for meeting {}",task.getMeeting().getId());
						emailService.prepareAndSend(task.getMeeting().getUser().getInvitation(),  task.getMeeting());
					} else {
						try {
							log.info("Finishing meeting...");
							meetingService.completeMeeting(task.getMeeting());
							openfireService.deleteMeeting(task.getMeeting());
							log.info("Meeting finished...");
						} catch (MeetingNotFound e) {
							log.error("Scheduler: Meeting not found", e);
						}
					}
				} catch (OpenfireException e) {
					log.error("Error in Meeting Scheduler");
					log.error(e.getMessage(), e);
					// task.setError(true);
					task.increaseFailed();
					meetingScheduledTaskRepository.save(task);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
