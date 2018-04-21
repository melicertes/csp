package com.intrasoft.csp.vcb.admin.repository;

import java.time.ZonedDateTime;
import java.util.List;

import com.intrasoft.csp.vcb.commons.model.MeetingScheduledTask;
import org.springframework.data.jpa.repository.JpaRepository;



public interface MeetingScheduledTaskRepository extends JpaRepository<MeetingScheduledTask, Long> {
	List<MeetingScheduledTask> findByTaskTimeLessThanAndCompletedIsFalseAndFailedLessThan(ZonedDateTime time,
			Integer maxRetries);

	List<MeetingScheduledTask> findById(Long id);

	List<MeetingScheduledTask> findByMeetingId(Long id);

	// List<MeetingScheduledTask> findByMeetingIdAndCompletedIsFalse(Long id);

	List<MeetingScheduledTask> findByMeetingIdAndCompletedIsFalseAndMeetingStartGreaterThan(Long id, ZonedDateTime now);
}
