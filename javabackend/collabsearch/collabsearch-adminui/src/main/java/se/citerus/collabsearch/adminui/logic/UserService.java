package se.citerus.collabsearch.adminui.logic;

import static org.apache.commons.lang.Validate.notEmpty;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;

import se.citerus.collabsearch.model.User;
import se.citerus.collabsearch.model.exceptions.DuplicateUserDataException;
import se.citerus.collabsearch.model.exceptions.UserNotFoundException;
import se.citerus.collabsearch.store.facades.UserDAO;

@Service
public class UserService { //TODO refactor into spring service

	@Autowired
	private UserDAO userDAO;

	public UserService() {
	}
	
	@PostConstruct
	public void init() {
		if (userDAO == null) {
			
		}
	}
	
	public void setDebugMode() {
		userDAO.activateDebugMode();
	}

	public List<User> getListOfUsers() throws IOException {
		return userDAO.getAllUsers();
	}

	public User findUser(String selectedUser) throws IOException, UserNotFoundException {
		return userDAO.getUserByUsername(selectedUser);
	}

	public void removeUser(String username) throws IOException, UserNotFoundException {
		userDAO.deleteUserByUsername(username);
	}

	public List<String> getListOfRoles() {
		try {
			return userDAO.getAllRoles();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void editUser(String username, String email, String tele, String role)
			throws IOException, UserNotFoundException, DuplicateUserDataException {
		notEmpty(username);
		notEmpty(email);
		notEmpty(tele);
		notEmpty(role);
		User user = new User(username, email, tele, role);
		userDAO.editExistingUser(user);
	}

	public String addUser(String username, String password, String email, String tele,
			String role) throws IOException, DuplicateUserDataException {
		notEmpty(username);
		notEmpty(password);
		notEmpty(email);
		notEmpty(tele);
		notEmpty(role);
		String hashedPassword = hashPassword(password);
		User user = new User(username, hashedPassword, email, tele, role);
		String id = userDAO.addNewUser(user);
		notEmpty(id);
		return id;
	}

	private String hashPassword(String password) {
		ShaPasswordEncoder encoder = new ShaPasswordEncoder(256);
		String hashedPassword = encoder.encodePassword(password, null);
		notEmpty(hashedPassword);
		return hashedPassword;
	}
	
	public void changePassword(String username, String newPassword) throws IOException {
		notEmpty(username);
		notEmpty(newPassword);
		userDAO.changePasswordForUser(username, hashPassword(newPassword));
	}

}
