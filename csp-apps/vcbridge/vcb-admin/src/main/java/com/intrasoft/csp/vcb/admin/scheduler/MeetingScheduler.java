package com.intrasoft.csp.vcb.admin.scheduler;

import java.time.ZonedDateTime;
import java.util.List;

import com.intrasoft.csp.vcb.admin.config.VcbadminProperties;
import com.intrasoft.csp.vcb.admin.repository.MeetingRepository;
import com.intrasoft.csp.vcb.admin.repository.MeetingScheduledTaskRepository;
import com.intrasoft.csp.vcb.admin.service.EmailService;
import com.intrasoft.csp.vcb.admin.service.MeetingService;
import com.intrasoft.csp.vcb.admin.service.exception.MeetingNotFound;
import com.intrasoft.csp.vcb.commons.constants.MeetingScheduledTaskType;
import com.intrasoft.csp.vcb.commons.model.MeetingScheduledTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;


@Component
public class MeetingScheduler {
	private static final Logger log = LoggerFactory.getLogger(MeetingScheduler.class);
	@Autowired
	private MeetingScheduledTaskRepository meetingScheduledTaskRepository;
	@Autowired
	private MeetingService meetingService;
	@Autowired
	private MeetingRepository meetingRepository;

	@Autowired
	RetryTemplate retryTemplate;

//	@Autowired
//	OpenfireService openfireService;

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
						//openfireService.createMeeting(task.getMeeting());
						log.info("Meeting created...");

						log.info("Changing task to completed...");
						task.setCompleted(true);
						meetingScheduledTaskRepository.save(task);
						log.info("Task changed...");
						log.info("Sending invitation emails for meeting {}", task.getMeeting().getId());
						emailService.prepareAndSend(task.getMeeting().getUser().getInvitation(), task.getMeeting());
					} else {
						try {
							log.info("Finishing meeting...");
							meetingService.completeMeeting(task.getMeeting());
							//openfireService.deleteMeeting(task.getMeeting());
							log.info("Meeting finished...");
						} catch (MeetingNotFound e) {
							log.error("Scheduler: Meeting not found", e);
						}
					}
				} catch (Exception e) { // instead of OpenfireException
				//} catch (OpenfireException e) { // instead of OpenfireException
					log.error("Error in Meeting Scheduler");
					log.error(e.getMessage(), e);
					// task.setError(true);
					task.increaseFailed();
					if (task.getFailed().equals(vcbadminProperties.getMaxTaskRetries())) {
						meetingService.failMeeting(task.getMeeting());
					} else {
						meetingScheduledTaskRepository.save(task);
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Transactional
	@Scheduled(fixedRate = 60000)
	public void changeStatus() {
		/**
		 * @TODO
		 */
		meetingRepository.updateRunningToExpired(ZonedDateTime.now());
		meetingRepository.updatePendingToRunning(ZonedDateTime.now());
	}
}
