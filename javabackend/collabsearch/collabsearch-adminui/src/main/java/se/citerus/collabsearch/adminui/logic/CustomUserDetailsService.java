package se.citerus.collabsearch.adminui.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import se.citerus.collabsearch.model.DbUser;
import se.citerus.collabsearch.store.facades.UserDAO;

/**
 * This is custom service for retrieving users implemententing Spring's {@link UserDetailsService} for MongoDB.
 */
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	@Qualifier("userDAOMongoDB")
	private UserDAO userDAO;
	
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException, DataAccessException {
		UserDetails user = null;
		try {
			DbUser dbUser = userDAO.findUserByName(username);
			user =  new User(
				dbUser.getUsername(),
				dbUser.getPassword().toLowerCase(),
				dbUser.isEnabled(),
				dbUser.isAccountNonExpired(),
				dbUser.isCredentialsNonExpired(),
				dbUser.isAccountNonLocked(),
				getAuthorities(convertRoleToAccess(dbUser.getRole()))
			);
		} catch (Exception e) {
			e.printStackTrace();
			throw new UsernameNotFoundException("Error in retrieving user " + username);
		}
		return user;
	}
	
	private Integer convertRoleToAccess(String role) {
		int access = 0;
		if ("user".equals(role)) {
			access  = 2;
		} else if ("admin".equals(role)) {
			access = 1;
		}
		return access;
	}

	/**
	 * Retrieves the correct ROLE type depending on the access level, where access level is an Integer.
	 * Basically, this interprets the access value whether it's for a regular user or admin.
	 * 
	 * @param access an integer value representing the access of the user
	 * @return collection of granted authorities
	 */
	 public Collection<GrantedAuthority> getAuthorities(Integer access) {
			List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>(2);
			authList.add(new GrantedAuthorityImpl("ROLE_USER"));
			
			if ( access.compareTo(1) == 0) {
				authList.add(new GrantedAuthorityImpl("ROLE_ADMIN"));
			}
			
			return authList;
	  }
}
