package com.intrasoft.csp.vcb.teleconf.service;

import com.intrasoft.csp.vcb.commons.model.Participant;
import com.intrasoft.csp.vcb.teleconf.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private ParticipantRepository participantRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Participant participant = participantRepository.findByUsername(s);
        if (participant == null) {
            throw new UsernameNotFoundException("No user found with username: " + s);
        }

        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("PARTICIPANT");
        grantedAuthorities.add(grantedAuthority);
        User u = new User(participant.getUsername(), participant.getPassword(), true, true, true, true, grantedAuthorities);
        return u;
    }

}
