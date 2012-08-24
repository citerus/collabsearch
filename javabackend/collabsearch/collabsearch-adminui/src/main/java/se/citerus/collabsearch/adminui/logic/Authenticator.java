package se.citerus.collabsearch.adminui.logic;

import java.io.IOException;

import com.mongodb.MongoException;
import com.mongodb.util.Hash;

import se.citerus.collabsearch.adminui.DAL.UserDAO;
import se.citerus.collabsearch.adminui.DAL.UserDAOMongoDB;

public class Authenticator {
	
	private UserDAO userDAL;

	public Authenticator() {
		try {
			userDAL = new UserDAOMongoDB();
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Looks up a user by username and password.
	 * @return true if username and password are matched, else false.
	 * @throws IOException 
	 */
	public boolean login(String username, char[] password) throws IOException {
		if (username == null || password == null) {
			return false;
		}
		boolean userFound = userDAL.findUser(username, password);
		userDAL.disconnect();
		return userFound;
	}
	
	public boolean isAuthorized(String username, String role) throws IOException {
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
