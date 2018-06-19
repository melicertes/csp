package com.intrasoft.csp.vcb.admin.config;

import com.intrasoft.csp.vcb.admin.model.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

//@Component
public class AuthorizationFilter extends OncePerRequestFilter {

	private static final Logger LOG = LoggerFactory.getLogger(AuthorizationFilter.class);
	public static final String USER_HEADER = "Custom-User-Id";
	public static final String GROUP_HEADER = "Custom-User-Is-Member-Of";
	private AuthenticationManager authenticationManager;
	public AuthorizationFilter(AuthenticationManager authenticationManager) {
		Assert.notNull(authenticationManager, "authenticationManager cannot be null");
		this.authenticationManager = authenticationManager;
	}
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String userHeaderValue = request.getHeader(USER_HEADER);
		String groupHeaderValue = request.getHeader(GROUP_HEADER);

		LOG.debug("-- userHeaderValue: " + userHeaderValue);
		LOG.debug("-- groupHeaderValue: " + groupHeaderValue);

		if (StringUtils.isEmpty(userHeaderValue) || StringUtils.isEmpty(groupHeaderValue)) {
			// response.setStatus(401);
			// response.getWriter().write("Unauthorized");//this will override the error
			// page
			response.sendError(401, "Unauthorized");
		} else {
			CustomUserDetails user;
			Collection<GrantedAuthority> authorities = new ArrayList<>();
			authorities.add(new SimpleGrantedAuthority(groupHeaderValue));

			user = new CustomUserDetails(userHeaderValue, "123456", authorities, null, null, null);
			user.setGroup(groupHeaderValue);

			Authentication auth = new UserPassAuthenticationToken(user, "12345", user.getAuthorities());

			SecurityContextHolder.getContext().setAuthentication(this.authenticationManager
					.authenticate(auth));
			filterChain.doFilter(request, response);
		}
	}
}