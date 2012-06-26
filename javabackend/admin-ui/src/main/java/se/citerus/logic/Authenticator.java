package se.citerus.logic;

import com.mongodb.MongoException;
import com.mongodb.util.Hash;

import se.citerus.DAL.UserDAL;

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
//		return true;
	}
	
	public boolean isAuthorized(String username, String role) {
		return userDAL.findUserWithRole(username, role);
	}
	
	private String hashPassword(String password) {
		Long l = Hash.hashBackwardLong(password);
		return l.toString();
	}
}
