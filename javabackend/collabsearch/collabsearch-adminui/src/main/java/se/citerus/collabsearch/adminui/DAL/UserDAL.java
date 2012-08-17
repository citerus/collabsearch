package se.citerus.collabsearch.adminui.DAL;

import java.io.IOException;
import java.util.List;

import se.citerus.collabsearch.adminui.logic.User;

/**
 * A facade for separating the higher levels from the db specific code.
 * @author Ola Rende
 */
public interface UserDAL {
	
	public boolean findUser(String username, char[] password) throws IOException;
	
	public void disconnect();
	
	public boolean findUserWithRole(String username, String role) throws IOException;
	
	public void makeSaltForUser(String username) throws IOException;
	
	public long retrieveSaltForUser(String username) throws Exception;
		
	public List<User> getAllUsers() throws IOException;
	
	public User getUserByUsername(String username) throws IOException;

	public void deleteUserByUsername(String username) throws IOException;
	
	public List<String> getAllRoles() throws IOException;

	public boolean checkForDuplicateUserData(String username, String tele, String email) throws IOException;

	public void editExistingUser(User user) throws IOException;

	public void addNewUser(User user) throws IOException;
	
}
