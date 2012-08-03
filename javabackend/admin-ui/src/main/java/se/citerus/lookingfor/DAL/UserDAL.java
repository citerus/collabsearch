package se.citerus.lookingfor.DAL;

import java.io.IOException;
import java.util.List;

import se.citerus.lookingfor.logic.User;

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
	
	public void addOrModifyUser(User user) throws IOException;
	
	public List<User> getAllUsers() throws IOException;
	
	public User getUserByUsername(String username) throws Exception;

	public Boolean deleteUserByUsername(String username) throws IOException;
	
	public List<String> getAllRoles() throws IOException;

	public boolean checkForDuplicateUserData(String username, String tele, String email) throws IOException;
	
}
