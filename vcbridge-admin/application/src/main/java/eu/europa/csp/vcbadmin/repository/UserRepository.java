package eu.europa.csp.vcbadmin.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import eu.europa.csp.vcbadmin.model.User;

@Repository
public interface UserRepository extends CrudRepository<User,Long> {
	Optional<User> findByEmail(String email);	
}
