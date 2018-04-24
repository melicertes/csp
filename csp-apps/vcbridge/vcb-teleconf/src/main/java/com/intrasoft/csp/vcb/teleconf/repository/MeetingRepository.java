package com.intrasoft.csp.vcb.teleconf.repository;

import com.intrasoft.csp.vcb.commons.model.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    Meeting findByUid(String uid);
}
