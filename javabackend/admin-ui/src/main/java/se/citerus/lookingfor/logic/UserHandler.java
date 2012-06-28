package se.citerus.lookingfor.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import se.citerus.lookingfor.DAL.UserDAL;

public class UserHandler {
	
	private UserDAL userDAL;

	public UserHandler() {
		userDAL = new UserDAL();		
	}

	public List<User> getListOfUsers() {
		return userDAL.getAllUsers();
	}

	public void editUser(String name, String pass, String email,
			String tele, String role) {
		User user = new User(name,pass,email,tele,role);
		try {
			userDAL.addOrModifyUser(user);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public User getUserData(User selectedUser) throws Exception {
		return userDAL.getUserByUsername(selectedUser.getUsername());
	}

}
