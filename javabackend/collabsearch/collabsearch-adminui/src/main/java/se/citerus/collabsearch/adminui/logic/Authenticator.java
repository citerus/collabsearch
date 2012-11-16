package se.citerus.collabsearch.adminui.logic;

import java.io.IOException;

import se.citerus.collabsearch.model.exceptions.UserNotFoundException;
import se.citerus.collabsearch.store.facades.UserDAO;
import se.citerus.collabsearch.store.mongodb.UserDAOMongoDB;

public class Authenticator { //TODO refactor to use UserService with DI
	
	private UserDAO userDAO;

	public Authenticator() {
		try {
			userDAO = new UserDAOMongoDB();
		} catch (Exception e) {
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
			userFound = userDAO.findUser(username, password);
		} catch (UserNotFoundException e) {
			userFound = false;
		}
		return userFound;
	}
	
	public boolean isAuthorized(String username, String role) throws IOException, UserNotFoundException {
		return userDAO.findUserWithRole(username, role);
	}
	
	private String hashPassword(String username, String password) {		
		try {
			Long hash = /* Hash.hashBackwardLong(password) */ 1L;
			Long salt = userDAO.retrieveSaltForUser(username);
			return hash.toString() + salt.toString(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void generateSaltForUser(String username) throws UserNotFoundException {
		try {
			userDAO.makeSaltForUser(username);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
