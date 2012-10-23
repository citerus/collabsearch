package se.citerus.collabsearch.adminui.logic;

import java.io.IOException;

import se.citerus.collabsearch.model.exceptions.UserNotFoundException;
import se.citerus.collabsearch.store.facades.UserDAO;
import se.citerus.collabsearch.store.mongodb.UserDAOMongoDB;

import com.mongodb.MongoException;
import com.mongodb.util.Hash;

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
		boolean userFound;
		try {
			userFound = userDAL.findUser(username, password);
		} catch (UserNotFoundException e) {
			userFound = false;
		}
		userDAL.disconnect();
		return userFound;
	}
	
	public boolean isAuthorized(String username, String role) throws IOException, UserNotFoundException {
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

	private void generateSaltForUser(String username) throws UserNotFoundException {
		try {
			userDAL.makeSaltForUser(username);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
