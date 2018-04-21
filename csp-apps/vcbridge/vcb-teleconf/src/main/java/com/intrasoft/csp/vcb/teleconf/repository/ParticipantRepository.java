package com.intrasoft.csp.vcb.teleconf.repository;

import com.intrasoft.csp.vcb.commons.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Participant findByUsername(String username);

    Participant findById(Long id);

    List<Participant> findByMeetingId(Long meetingId);
}
