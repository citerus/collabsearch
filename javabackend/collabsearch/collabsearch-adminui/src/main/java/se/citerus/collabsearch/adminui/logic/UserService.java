package se.citerus.collabsearch.adminui.logic;

import java.io.IOException;
import java.util.List;

import se.citerus.collabsearch.model.User;
import se.citerus.collabsearch.model.exceptions.UserNotFoundException;
import se.citerus.collabsearch.store.facades.UserDAO;
import se.citerus.collabsearch.store.mongodb.UserDAOMongoDB;

public class UserService { //TODO refactor into spring service

	private UserDAO userDAL;

	public UserService() {
		//TODO choose type of DAO by config file
		//userDAL = new UserDALInMemory();
		userDAL = new UserDAOMongoDB();
	}

	public List<User> getListOfUsers() throws IOException {
		return userDAL.getAllUsers();
	}

	public User getUserData(String selectedUser) throws IOException, UserNotFoundException {
		return userDAL.getUserByUsername(selectedUser);
	}

	public void removeUser(String username) throws IOException, UserNotFoundException {
		userDAL.deleteUserByUsername(username);
	}

	/**
	 * Edits user (if existing) or adds a new one with the included attributes.
	 * @throws UserNotFoundException 
	 * @throws IOException 
	 */
	public void editUser(User user) throws IOException, UserNotFoundException {
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
	 * @throws IOException 
	 */
	public boolean lookForDuplicates(String username, String tele, String email) throws IOException {
		return userDAL.checkForDuplicateUserData(username, tele, email);
	}

	public void addUser(User user) throws IOException {
		userDAL.addNewUser(user);
	}

}
