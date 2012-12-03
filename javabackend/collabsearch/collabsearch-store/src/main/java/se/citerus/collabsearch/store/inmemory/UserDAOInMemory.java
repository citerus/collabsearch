package se.citerus.collabsearch.store.inmemory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Repository;

import se.citerus.collabsearch.model.DbUser;
import se.citerus.collabsearch.model.User;
import se.citerus.collabsearch.model.exceptions.DuplicateUserDataException;
import se.citerus.collabsearch.model.exceptions.UserNotFoundException;
import se.citerus.collabsearch.store.facades.UserDAO;

@Repository
public class UserDAOInMemory implements UserDAO {
	
	private static List<User> allUsers;
	private static List<String> allRoles;
	
	public UserDAOInMemory() {
		if (allUsers == null) {
			allUsers = new ArrayList<User>(1);
			User testUser = new User("admin","test","test@test.test","123456789","admin");
			allUsers.add(testUser);
			
			allRoles = new ArrayList<String>(2);
			allRoles.add("admin");
			allRoles.add("user");
		}
	}
	
	@PostConstruct
	private void init() {
		System.out.println("Initiated inmem db");
	}

	public boolean findUser(String username, char[] password) {
		for (int i = 0; i < allUsers.size(); i++) {
			User user = allUsers.get(i);
			if (user.getUsername().equals(username) && 
					user.getPassword().toCharArray().equals(password)) {
				return true;
			}
		}
		return false;
	}

	public void disconnect() {
		//no impl needed
	}

	public boolean findUserWithRole(String username, String role) {
		for (int i = 0; i < allUsers.size(); i++) {
			User user = allUsers.get(i);
			if (user.getUsername().equals(username) && 
					user.getRole().equals(role)) {
				return true;
			}
		}
		return false;
	}

	public List<User> getAllUsers() {
		return allUsers;
	}

	public User getUserByUsername(String username) throws IOException, UserNotFoundException {
		for (User user : allUsers) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}
		throw new UserNotFoundException(username);
	}

	public void deleteUserByUsername(String username) throws IOException, UserNotFoundException {
		for (int i = 0; i < allUsers.size(); i++) {
			User user = allUsers.get(i);
			if (user.getUsername().equals(username)) {
				allUsers.remove(i);
				return;
			}
		}
		throw new UserNotFoundException(username);
	}

	public List<String> getAllRoles() throws IOException {
		return allRoles;
	}

	public boolean checkForDuplicateUserData(String username, String tele, String email) {
		boolean result = false;
		for (User user : allUsers) {
			if (user.getUsername().equals(username) || user.getTele().equals(tele) || user.getEmail().equals(email)) {
				result = true;
			}
		}
		return result;
	}

	public void editExistingUser(User user) throws IOException, DuplicateUserDataException {
		for (int i = 0; i < allUsers.size(); i++) {
			User existingUser = allUsers.get(i);
			if (existingUser.getEmail().equals(user.getEmail()) 
					|| existingUser.getTele().equals(user.getTele())) {
				throw new DuplicateUserDataException();
			}
		}
		for (int i = 0; i < allUsers.size(); i++) {
			User existingUser = allUsers.get(i);
			if (existingUser.getUsername().equals(user.getUsername())) {
				allUsers.set(i, user);
				return;
			}
		}
		throw new IOException("Användare ej funnen!");
	}

	public String addNewUser(User user) throws IOException, DuplicateUserDataException {
		for (int i = 0; i < allUsers.size(); i++) {
			User existingUser = allUsers.get(i);
			if (existingUser.getEmail().equals(user.getEmail()) 
					|| existingUser.getTele().equals(user.getTele())) {
				throw new DuplicateUserDataException();
			}
		}
		allUsers.add(user);
		return user.getUsername();
	}

	@Override
	public void activateDebugMode() {
		//no op
	}

	@Override
	public DbUser findUserByName(String username) {
		//return a mockup admin user with password "test" (sha256 encoded)
		return new DbUser(username, "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08", "user");
	}

	@Override
	public void changePasswordForUser(String username, String hashedPassword) {
		for (User user : allUsers) {
			if (user.getUsername().equals(username)) {
				User user2 = new User(user.getUsername(), hashedPassword,
						user.getEmail(), user.getTele(), user.getRole());
				allUsers.set(allUsers.indexOf(user), user2);
			}
		}
	}

}
