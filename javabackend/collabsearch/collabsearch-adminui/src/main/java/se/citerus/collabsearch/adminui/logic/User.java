package se.citerus.collabsearch.adminui.logic;

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = 8232887479981262455L;
	
	private String username;
	private String password;
	private String email;
	private String tele;
	private String role;
	
	public User() {
		//intentionally left empty
	}

	public User(String username, String password, String email, String tele, String role) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.tele = tele;
		this.role = role;
	}
	
	public User(String username, String email, String tele, String role) {
		this.username = username;
		this.email = email;
		this.tele = tele;
		this.role = role;
	}

	public User(String username, char[] password) {
		this.username = username;
		this.password = String.valueOf(password);
	}

	public User(String username, String role) {
		this.username = username;
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}

	public String getTele() {
		return tele;
	}

	public String getRole() {
		return role;
	}
	
}
