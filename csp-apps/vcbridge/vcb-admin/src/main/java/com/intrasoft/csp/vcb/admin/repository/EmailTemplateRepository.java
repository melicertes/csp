package com.intrasoft.csp.vcb.admin.repository;

import java.util.List;

import com.intrasoft.csp.vcb.commons.constants.EmailTemplateType;
import com.intrasoft.csp.vcb.commons.model.EmailTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {
	List<EmailTemplate> findByUserEmail(String email);

	Page<EmailTemplate> findByUserEmail(String email, Pageable p);
	Page<EmailTemplate> findByUserId(Long userId, Pageable p);

	EmailTemplate findOneByIdAndUserEmail(Long id, String email);

	List<EmailTemplate> findByTypeAndActiveIsTrueAndUserEmail(EmailTemplateType cancellation, String name);

}
