package se.citerus.collabsearch.store.facades;

import java.io.IOException;
import java.util.List;

import se.citerus.collabsearch.model.DbUser;
import se.citerus.collabsearch.model.User;
import se.citerus.collabsearch.model.exceptions.DuplicateUserDataException;
import se.citerus.collabsearch.model.exceptions.UserNotFoundException;

/**
 * A facade for separating the higher levels from the db specific code.
 * @author Ola Rende
 */
public interface UserDAO {
	
	public boolean findUser(String username, char[] password) throws IOException, UserNotFoundException;
	
	public void disconnect();
	
	public boolean findUserWithRole(String username, String role) throws IOException, UserNotFoundException;
	
	public void makeSaltForUser(String username) throws IOException, UserNotFoundException;
	
	public long retrieveSaltForUser(String username) throws Exception, UserNotFoundException;
	
	public List<User> getAllUsers() throws IOException;
	
	public User getUserByUsername(String username) throws IOException, UserNotFoundException;

	public void deleteUserByUsername(String username) throws IOException, UserNotFoundException;
	
	public List<String> getAllRoles() throws IOException;

	public boolean checkForDuplicateUserData(String username, String tele, String email) throws IOException;

	public void editExistingUser(User user) throws IOException, UserNotFoundException;

	public void addNewUser(User user) throws IOException, DuplicateUserDataException;

	public void activateDebugMode();

	public DbUser findUserByName(String username) throws UserNotFoundException;
	
}
