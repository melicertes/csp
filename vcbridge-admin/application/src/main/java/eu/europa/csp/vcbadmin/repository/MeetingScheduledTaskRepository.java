package eu.europa.csp.vcbadmin.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.europa.csp.vcbadmin.model.MeetingScheduledTask;

public interface MeetingScheduledTaskRepository extends JpaRepository<MeetingScheduledTask, Long> {
	List<MeetingScheduledTask> findByTaskTimeLessThanAndCompletedIsFalse(ZonedDateTime time);

	List<MeetingScheduledTask> findById(Long id);
}
