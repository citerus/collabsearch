package se.citerus.lookingfor.logic;

import java.util.Map;

public class User {

	private final String name;
	private final String pass;
	private final String email;
	private final String tele;
	private final String role;

	public User(String name, String pass, String email, String tele, String role) {
		this.name = name;
		this.pass = pass;
		this.email = email;
		this.tele = tele;
		this.role = role;
	}

	public String getName() {
		return name;
	}

	public String getPass() {
		return pass;
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