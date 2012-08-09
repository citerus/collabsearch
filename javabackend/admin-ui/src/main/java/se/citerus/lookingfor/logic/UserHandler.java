package se.citerus.lookingfor.logic;

import java.io.IOException;
import java.util.List;

import se.citerus.lookingfor.DAL.UserDAL;
import se.citerus.lookingfor.DAL.UserDALMongoDB;

public class UserHandler {

	private UserDAL userDAL;

	public UserHandler() {
		userDAL = new UserDALMongoDB();
	}

	public List<User> getListOfUsers() throws IOException {
		return userDAL.getAllUsers();
	}

	public User getUserData(String selectedUser) throws Exception {
		return userDAL.getUserByUsername(selectedUser);
	}

	public Boolean removeUser(String username) throws Exception {
		try {
			return userDAL.deleteUserByUsername(username);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Edits user (if existing) or adds a new one with the included attributes.
	 */
	public void editUser(User user) throws Exception {
		userDAL.addOrModifyUser(user);
	}

	public List<String> getListOfRoles() {
		try {
			return userDAL.getAllRoles();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void cleanUp() {
		userDAL.disconnect();
	}

	/**
	 * Searches for duplicate users by username or telephone number or email.
	 * @return true if duplicates were found, else false.
	 */
	public boolean lookForDuplicates(String username, String tele, String email) throws Exception {
		return userDAL.checkForDuplicateUserData(username, tele, email);
	}

}
