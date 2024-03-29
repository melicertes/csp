package com.intrasoft.csp.vcb.admin.service;

import com.intrasoft.csp.vcb.admin.model.CustomUserDetails;
import com.intrasoft.csp.vcb.admin.repository.EmailTemplateRepository;
import com.intrasoft.csp.vcb.admin.repository.UserRepository;

import com.intrasoft.csp.vcb.commons.constants.EmailTemplateType;
import com.intrasoft.csp.vcb.commons.constants.UserRole;
import com.intrasoft.csp.vcb.commons.model.EmailTemplate;
import com.intrasoft.csp.vcb.commons.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Scanner;

@Service("customUserDetailsService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(UserDetailsService.class);

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Value(value = "classpath:templates/email/invitation.html")
    private Resource invitationHTML;

    @Value(value = "classpath:templates/email/cancellation.html")
    private Resource cancellationHTML;

    @Autowired
    private EmailTemplateRepository emailTemplateRepository;

    @Override
    public UserDetails loadUserByUsername(final String login) {

        log.debug("Authenticating {}", login);
        User user = userRepository.findByEmail(login.toLowerCase()).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User with email=%s was not found", login)));

        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getRole().name());
        grantedAuthorities.add(grantedAuthority);
        return new CustomUserDetails(user.getEmail(), user.getPassword(), grantedAuthorities, user.getTimezone(),
                user.getFirstName(), user.getLastName());
    }

    /**
     * @param username
     * @return if user created successfully
     */
    @Transactional
    public User createUserIfNotExists(String username) {
        Optional<User> u = userRepository.findByEmail(username);
        if (!u.isPresent()) {
            log.debug("User not found. Creating profile automatically..");
            // create profile automatically
            User new_user = new User();
            new_user.setEmail(username);
            new_user.setRole(UserRole.USER);
            new_user.setPassword("123456");
            new_user.setPasswordConfirm("123456");
            try {
                createNewUser(new_user);
                return new_user;
            } catch (Exception e) {
                // return failure whatever the exception is
                log.debug(e.getMessage(), e);
                return null;
            }
        }
        log.debug("User found in db.");
        return u.get();

    }

    @Transactional
    public User createNewUser(User userForm) throws IOException {
        userForm.setPassword(passwordEncoder.encode(userForm.getPassword()));
        log.debug("Creating user {}", userForm);
        User user = userRepository.save(userForm);

        log.debug("Constructing init invitation email for user {}", user.getEmail());
        EmailTemplate et = new EmailTemplate("Auto-generated Invitation", true);
        et.setSubject("Invitation: [(${meeting_subject})]");
        String content = new Scanner(invitationHTML.getInputStream(), "utf-8").useDelimiter("\\Z").next();
        et.setContent(content);
        et.setType(EmailTemplateType.INVITATION);
        et.setUser(user);
        EmailTemplate invitation = emailTemplateRepository.save(et);

        log.debug("Constructing init cancellation email for user {}", user.getEmail());
        et = new EmailTemplate("Auto-generated Cancellation", true);
        et.setSubject("Cancelled: [(${meeting_subject})]");
        content = new Scanner(cancellationHTML.getInputStream(), "utf-8").useDelimiter("\\Z").next();
        et.setContent(content);
        et.setType(EmailTemplateType.CANCELLATION);
        et.setUser(user);
        EmailTemplate cancellation = emailTemplateRepository.save(et);
        return user;
    }
}
