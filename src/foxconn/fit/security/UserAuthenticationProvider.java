package foxconn.fit.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public class UserAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider{

	private UserDetailsService userDetailsService;
	
	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException("Bad credentials:" + userDetails);
        }

        String presentedPassword = authentication.getCredentials().toString();
        String password = userDetails.getPassword();

        if (!password.equals(presentedPassword)) {
            logger.debug("Authentication failed: 密码错误");

            throw new BadCredentialsException("Bad credentials:" + userDetails);
        }
	}

	@Override
	protected UserDetails retrieveUser(String username,UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		UserDetails user = getUserDetailsService().loadUserByUsername(username);
        
        return user;
	}
	
	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
	
}
