package se.citerus.lookingfor.logic;

import java.io.IOException;

import se.citerus.lookingfor.DAL.UserDAL;

public class UserHandler {

	public void editUser(String name, String pass, String email,
			String tele, String role) {
		User user = new User(name,pass,email,tele,role);
		try {
			new UserDAL().addOrModifyUser(user);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
