package eu.europa.csp.vcbadmin.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import eu.europa.csp.vcbadmin.constants.MeetingStatus;
import eu.europa.csp.vcbadmin.model.Meeting;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
	List<Meeting> findByStart(ZonedDateTime start);

	List<Meeting> findByUserEmailAndStatus(String email, MeetingStatus status);

	@Query("select m from Meeting m where m.user.email = ?1 and (m.status=?2 or m.status=?3)")
	List<Meeting> findByUserEmailAndStatusOrStatus(String email, MeetingStatus status1, MeetingStatus status2);

	@Query("select m from Meeting m where m.user.email = ?1 and (m.status=?2 or m.status=?3 or m.status=?4)")
	List<Meeting> findByUserEmailAndStatusOrStatusOrStatus(String email, MeetingStatus status1, MeetingStatus status2,
			MeetingStatus status3);

	List<Meeting> findById(Long id);
}
