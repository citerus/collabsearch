package se.citerus.collabsearch.model;

public class DbUser {

	private final String username;
	private final String password;
	private final String role;
	
	private final boolean enabled; 
	private final boolean accountNonExpired; 
	private final boolean credentialsNonExpired; 
	private final boolean accountNonLocked;

	public DbUser(String username, String password, String role) {
		this.username = username;
		this.password = password;
		this.role = role;
		enabled = accountNonExpired = credentialsNonExpired = accountNonLocked = true;
	}
	
	public DbUser(String username, String password, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked, String role) {
		this.username = username;
		this.password = password;
		this.enabled = enabled;
		this.accountNonExpired = accountNonExpired;
		this.credentialsNonExpired = credentialsNonExpired;
		this.accountNonLocked = accountNonLocked;
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getRole() {
		return role;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}
	
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}
	
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

}
