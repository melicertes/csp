package eu.europa.csp.vcbadmin.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import eu.europa.csp.vcbadmin.constants.MeetingStatus;
import eu.europa.csp.vcbadmin.model.Meeting;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
	List<Meeting> findByStart(ZonedDateTime start);

	List<Meeting> findByUserEmailAndStatus(String email, MeetingStatus status);

	@Query("select m from Meeting m where m.user.email = ?1 and (m.status=?2 or m.status=?3)")
	List<Meeting> findByUserEmailAndStatusOrStatus(String email, MeetingStatus status1, MeetingStatus status2);

	@Query("select m from Meeting m where m.user.email = ?1 and (m.status=?2 or m.status=?3 or m.status=?4 or m.status=?5)")
	List<Meeting> findByUserEmailAndStatusOrStatusOrStatus(String email, MeetingStatus status1, MeetingStatus status2,
			MeetingStatus status3, MeetingStatus status4);

	List<Meeting> findById(Long id);

	@Modifying(clearAutomatically = true)
	@Query(value = "update vcb_meeting set status = 'Expired' where status='Running' and DATE_ADD(start,INTERVAL duration/1000 MICROSECOND)<?1", nativeQuery = true)
	void updateRunningToExpired(ZonedDateTime now);

	@Modifying(clearAutomatically = true)
	@Query("update Meeting m set m.status = 'Running' where m.status='Pending' and not exists(select s from MeetingScheduledTask s where s.meeting.id=m.id and  s.taskType='START_MEETING' and s.completed=false) and m.start <?1")
	void updatePendingToRunning(ZonedDateTime now);

	// List<Meeting> findByStatusAndStartLessThan(MeetingStatus status,
	// ZonedDateTime now);
}
