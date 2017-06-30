package eu.europa.csp.vcbadmin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.europa.csp.vcbadmin.model.EmailTemplate;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {
	List<EmailTemplate> findByUserEmail(String email);
}
