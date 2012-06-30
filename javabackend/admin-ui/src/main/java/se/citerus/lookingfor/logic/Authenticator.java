package se.citerus.lookingfor.logic;

import java.io.IOException;

import com.mongodb.MongoException;
import com.mongodb.util.Hash;

import se.citerus.lookingfor.DAL.UserDAL;

public class Authenticator {
	private UserDAL userDAL;

	public Authenticator() {
		try {
			userDAL = new UserDAL();
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Looks up a user by username and password.
	 * @return true if username and password are matched, else false.
	 */
	public boolean login(String username, char[] password) {
		if (username == null || password == null) {
			return false;
		}
		return userDAL.findUser(username, password);
	}
	
	public boolean isAuthorized(String username, String role) {
		return userDAL.findUserWithRole(username, role);
	}
	
	private String hashPassword(String username, String password) {		
		try {
			Long hash = Hash.hashBackwardLong(password);
			Long salt = userDAL.retrieveSaltForUser(username);
			return hash.toString() + salt.toString(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void generateSaltForUser(String username) {
		try {
			userDAL.makeSaltForUser(username);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
