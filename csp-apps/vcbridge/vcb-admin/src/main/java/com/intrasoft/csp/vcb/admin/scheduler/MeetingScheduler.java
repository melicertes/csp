package com.intrasoft.csp.vcb.admin.scheduler;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

import com.intrasoft.csp.vcb.admin.config.VcbadminProperties;
import com.intrasoft.csp.vcb.admin.repository.MeetingRepository;
import com.intrasoft.csp.vcb.admin.repository.MeetingScheduledTaskRepository;
import com.intrasoft.csp.vcb.admin.service.EmailService;
import com.intrasoft.csp.vcb.admin.service.MeetingService;
import com.intrasoft.csp.vcb.admin.service.exception.MeetingNotFound;
import com.intrasoft.csp.vcb.commons.constants.MeetingScheduledTaskType;
import com.intrasoft.csp.vcb.commons.constants.MeetingStatus;
import com.intrasoft.csp.vcb.commons.model.Meeting;
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
		log.debug("Scheduler is now checking for MeetingScheduledTask(s)...");
		try {
			for (MeetingScheduledTask task : tasks) {
				log.debug("Running scheduled task: {}", task);
				task.getMeeting().getParticipants().size();
				task.getMeeting().getUser();
				try {
					if (task.getTaskType().equals(MeetingScheduledTaskType.START_MEETING)) {
						log.debug("Creating meeting...");
						//openfireService.createMeeting(task.getMeeting());
						log.debug("Meeting created...");

						log.debug("Changing task to completed...");
						task.setCompleted(true);
						meetingScheduledTaskRepository.save(task);
						log.debug("Task changed...");
						log.debug("Sending invitation emails for meeting {}", task.getMeeting().getId());
						emailService.prepareAndSend(task.getMeeting().getUser().getInvitation(), task.getMeeting());
					} else {
						try {
							log.debug("Finishing meeting...");
							meetingService.completeMeeting(task.getMeeting());
							//openfireService.deleteMeeting(task.getMeeting());
							log.debug("Meeting finished...");
						} catch (MeetingNotFound e) {
							log.error("Scheduler: Meeting not found", e);
						}
					}
				} catch (Exception e) { // instead of OpenfireException
				//} catch (OpenfireException e) { // instead of OpenfireException
					log.error("Error in Meeting Scheduler: " + e.toString());
					log.debug(e.getMessage(), e);
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
			log.debug(e.getMessage(), e);
		}
	}

	@Transactional
	//@Scheduled(fixedRate = 60000)
	@Scheduled(fixedRate = 6000)
	public void changeStatus() {
		/**
		 * Usage with native query:
		 * meetingRepository.updateRunningToExpired(ZonedDateTime.now());
		 *
		 * Instead loop meetings and change their status after comparing start + duration ? now (in seconds)
		 */
		List<Meeting> meetingsRunningToExpired = meetingRepository.findByStatusOrStatus(MeetingStatus.Running, MeetingStatus.Expired);
		for (Meeting m : meetingsRunningToExpired) {
			ZonedDateTime first = m.getStart().plusSeconds(m.getDuration().getSeconds());
			ZonedDateTime second = ZonedDateTime.now();
			Comparator<ZonedDateTime> comparator = Comparator.comparing(
					zdt -> zdt.truncatedTo(ChronoUnit.SECONDS));
			Integer result = comparator.compare(first, second);
			if (result < 0) {
				m.setStatus(MeetingStatus.Expired);
				meetingRepository.save(m);
			}
		}

		/**
		 * Usage with native query:
		 * meetingRepository.updatePendingToRunning(ZonedDateTime.now());
		 *
		 * Instead loop meetings and change their status after querying for relevant scheduled tasks
		 */
		List<Meeting> meetingsPendingToRunning = meetingRepository.findByStatusAndStartLessThan(MeetingStatus.Pending, ZonedDateTime.now());
		for (Meeting m : meetingsPendingToRunning) {
			if (meetingScheduledTaskRepository.countByMeetingIdAndTaskTypeAndCompletedIsFalse(m.getId(), MeetingScheduledTaskType.START_MEETING) == 0) {
				m.setStatus(MeetingStatus.Running);
				meetingRepository.save(m);
			}
		}
	}
}
