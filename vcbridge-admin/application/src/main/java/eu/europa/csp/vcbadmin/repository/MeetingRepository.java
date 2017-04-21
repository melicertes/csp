package eu.europa.csp.vcbadmin.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import eu.europa.csp.vcbadmin.model.Meeting;

public interface MeetingRepository extends CrudRepository<Meeting, Long> {
	List<Meeting> findByStart(LocalDateTime start);
	List<Meeting> findById(Long id);
}
