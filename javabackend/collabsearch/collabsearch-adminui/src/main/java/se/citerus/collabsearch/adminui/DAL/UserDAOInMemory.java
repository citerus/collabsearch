package se.citerus.collabsearch.adminui.DAL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import se.citerus.collabsearch.model.User;

public class UserDAOInMemory implements UserDAO {
	
	private static List<User> allUsers;
	private static HashMap<String, Long> salts;
	private static List<String> allRoles;
	
	public UserDAOInMemory() {
		if (allUsers == null) {
			allUsers = new ArrayList<User>(1);
			User testUser = new User("test","test","test@test.test","123456789","admin");
			allUsers.add(testUser);
			
			salts = new HashMap<String, Long>();
			salts.put(testUser.getUsername(), 122141581250915L);
			
			allRoles = new ArrayList<String>(2);
			allRoles.add("admin");
			allRoles.add("user");
		}
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

	public void makeSaltForUser(String username) throws IOException {
		salts.put(username, 122141581250915L);
	}

	public long retrieveSaltForUser(String username) throws Exception {
		return salts.get(username);
	}

	public List<User> getAllUsers() {
		return allUsers;
	}

	public User getUserByUsername(String username) throws IOException {
		for (User user : allUsers) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}
		throw new IOException("Användare ej funnen!");
	}

	public void deleteUserByUsername(String username) throws IOException {
		for (int i = 0; i < allUsers.size(); i++) {
			User user = allUsers.get(i);
			if (user.getUsername().equals(username)) {
				allUsers.remove(i);
			}
		}
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

	public void editExistingUser(User user) throws IOException {
		for (int i = 0; i < allUsers.size(); i++) {
			User existingUser = allUsers.get(i);
			if (existingUser.getUsername().equals(user.getUsername())) {
				allUsers.set(i, user);
				return;
			}
		}
		throw new IOException("Användare ej funnen!");
	}

	public void addNewUser(User user) throws IOException {
		allUsers.add(user);
	}

}
