package se.citerus.lookingfor.logic;

import java.io.IOException;
import java.util.List;

import se.citerus.lookingfor.DAL.UserDAL;

public class UserHandler {
	
	private UserDAL userDAL;

	public UserHandler() {
		userDAL = new UserDAL();
	}

	public List<User> getListOfUsers() {
		return userDAL.getAllUsers();
	}

	/**
	 * Edits user (if existing) or adds a new one with the included attributes.
	 */
	public void editUser(String name, String pass, String email,
			String tele, String role) {
		User user = new User(name,pass,email,tele,role);
		try {
			userDAL.addOrModifyUser(user);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public User getUserData(String selectedUser) throws Exception {
		return userDAL.getUserByUsername(selectedUser);
	}

	public Boolean removeUser(String username) {
		try {
			return userDAL.deleteUserByUsername(username);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}
