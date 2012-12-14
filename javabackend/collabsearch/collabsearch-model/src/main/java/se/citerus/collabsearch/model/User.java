package se.citerus.collabsearch.model;

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = 8232887479981262455L;
	
	private String id;
	private String username;
	private String password;
	private String email;
	private String tele;
	private String role;
	
	public User() {
		//intentionally left empty
	}

	public User(String id, String username, String password, String email, String tele, String role) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.email = email;
		this.tele = tele;
		this.role = role;
	}
	
	public User(String username, String password, String email, String tele, String role) {
		this(null, username, password, email, tele, role);
	}
	
	public User(String username, String email, String tele, String role) {
		this(null, username, null, email, tele, role);
	}

	public String getId() {
		return id;
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
