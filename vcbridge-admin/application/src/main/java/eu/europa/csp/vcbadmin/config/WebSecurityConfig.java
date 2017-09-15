package eu.europa.csp.vcbadmin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import eu.europa.csp.vcbadmin.service.UserDetailsService;

@Configuration
@EnableWebSecurity
@ComponentScan("eu.europa.csp.vcbadmin.config")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	private UserDetailsService userDetailsService;

	@Autowired
	public WebSecurityConfig(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new StandardPasswordEncoder();
	}

	@Value("${enable.oam}")
	boolean enableOAM;

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Autowired
	private CustomAuthenticationProvider customAuthenticationProvider;

	//
	@Autowired
	@Override
	public void configure(AuthenticationManagerBuilder myAuthenticationManager) throws Exception {
		myAuthenticationManager.authenticationProvider(customAuthenticationProvider);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		if (enableOAM) {
			http.addFilterBefore(new AuthorizationFilter(authenticationManager()), BasicAuthenticationFilter.class)
					;
			http.authenticationProvider(customAuthenticationProvider);
		} else {
			http.authorizeRequests().antMatchers("/resources/**").permitAll().antMatchers("/register").permitAll()
					.anyRequest().authenticated().and().formLogin().loginPage("/login").usernameParameter("username")
					.permitAll().and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll();
		}
	}
	// @Override
	// protected void configure(HttpSecurity http) throws Exception {
	// http.authorizeRequests().antMatchers("/resources/**").permitAll()
	// .antMatchers("/register").permitAll()
	// .anyRequest().authenticated().and().formLogin().loginPage("/login").usernameParameter("username")
	// .permitAll().and().logout().logoutRequestMatcher(new
	// AntPathRequestMatcher("/logout")).permitAll();
	// }

}