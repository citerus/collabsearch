package se.citerus.collabsearch.adminui.logic;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.stereotype.Service;

import se.citerus.collabsearch.model.User;
import se.citerus.collabsearch.model.exceptions.DuplicateUserDataException;
import se.citerus.collabsearch.model.exceptions.UserNotFoundException;
import se.citerus.collabsearch.store.facades.UserDAO;
import se.citerus.collabsearch.store.mongodb.UserDAOMongoDB;

@Service
public class UserService { //TODO refactor into spring service

	private UserDAO userDAO;

	public UserService() {
		//TODO choose type of DAO by config file
		//userDAL = new UserDALInMemory();
	}
	
	@PostConstruct
	public void init() {
		userDAO = new UserDAOMongoDB();
	}
	
	@PreDestroy
	public void cleanUp() {
		userDAO.disconnect();
	}

	public List<User> getListOfUsers() throws IOException {
		return userDAO.getAllUsers();
	}

	public User getUserData(String selectedUser) throws IOException, UserNotFoundException {
		return userDAO.getUserByUsername(selectedUser);
	}

	public void removeUser(String username) throws IOException, UserNotFoundException {
		userDAO.deleteUserByUsername(username);
	}

	/**
	 * Edits user (if existing) or adds a new one with the included attributes.
	 * @throws UserNotFoundException 
	 * @throws IOException 
	 */
	public void editUser(User user) throws IOException, UserNotFoundException {
		userDAO.editExistingUser(user);
	}

	public List<String> getListOfRoles() {
		try {
			return userDAO.getAllRoles();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Searches for duplicate users by username or telephone number or email.
	 * @return true if duplicates were found, else false.
	 * @throws IOException 
	 */
	public boolean lookForDuplicates(String username, String tele, String email) throws IOException {
		return userDAO.checkForDuplicateUserData(username, tele, email);
	}

	public void addUser(User user) throws IOException, DuplicateUserDataException {
		userDAO.addNewUser(user);
	}

	public void setDebugMode() {
		userDAO.activateDebugMode();
	}

}
