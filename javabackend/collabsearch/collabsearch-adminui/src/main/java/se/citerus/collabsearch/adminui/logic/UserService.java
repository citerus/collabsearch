package se.citerus.collabsearch.adminui.logic;

import java.io.IOException;
import java.util.List;

import se.citerus.collabsearch.adminui.DAL.UserDAO;
import se.citerus.collabsearch.adminui.DAL.UserDAOMongoDB;
import se.citerus.collabsearch.model.User;

public class UserService { //TODO refactor into spring service

	private UserDAO userDAL;

	public UserService() {
		//TODO choose type of DAL by config file
		//userDAL = new UserDALInMemory();
		userDAL = new UserDAOMongoDB();
	}

	public List<User> getListOfUsers() throws IOException {
		return userDAL.getAllUsers();
	}

	public User getUserData(String selectedUser) throws Exception {
		return userDAL.getUserByUsername(selectedUser);
	}

	public void removeUser(String username) throws Exception {
		userDAL.deleteUserByUsername(username);
	}

	/**
	 * Edits user (if existing) or adds a new one with the included attributes.
	 */
	public void editUser(User user) throws Exception {
		userDAL.editExistingUser(user);
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

	public void addUser(User user) throws Exception {
		userDAL.addNewUser(user);
	}

}
