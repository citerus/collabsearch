package se.citerus.logic;

import com.mongodb.MongoException;

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
	
	public boolean login(String username, char[] password) {
		return userDAL.findUser(username, password);
	}
}
