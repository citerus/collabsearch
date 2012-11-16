/**
 * 
 */
package se.citerus.collabsearch.adminui;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import se.citerus.collabsearch.adminui.logic.Authenticator;
import se.citerus.collabsearch.adminui.logic.SearchOperationService;
import se.citerus.collabsearch.adminui.logic.UserService;
import se.citerus.collabsearch.model.User;
import se.citerus.collabsearch.model.exceptions.DuplicateUserDataException;
import se.citerus.collabsearch.model.exceptions.UserNotFoundException;

/**
 * Unit tests for the user management.
 * @author Ola Rende
 */
public class UserMgmtTests {

	private static final Random RANDOM = new Random();
	
	private static UserService service;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(
						"se.citerus.collabsearch.adminui",
						"se.citerus.collabsearch.store");
		service = context.getBean(UserService.class);
		service.setDebugMode();
	}

	@Test
	public void testCreateAndFindUser() throws IOException, UserNotFoundException, DuplicateUserDataException {
		String username = "testuser" + RANDOM.nextInt();
		User dummyUser = new User(username, "password", username + "@email.com", 
				"" + RANDOM.nextInt(), "user");
		service.addUser(dummyUser);
		User user = service.getUserData(dummyUser.getUsername());
	}
	
	@Test(expected=UserNotFoundException.class)
	public void testFindNonExistentUser() throws IOException, UserNotFoundException {
		service.getUserData("thisusernamedoesnotexist");
	}
	
	@Test
	public void testUserAuthFailure() throws Exception {
		boolean userAuthResult;
		Authenticator auth = new Authenticator();
		userAuthResult = auth.login("testusr", "wrongpassword".toCharArray());
		assertTrue("User \"test\" authenticated with wrong password, should throw error", userAuthResult == false);
	}
	
	@Test(expected=Exception.class)
	public void testDeleteUser() throws Exception {
		String username = "testuser" + RANDOM.nextInt(); //create user
		service.addUser(new User(username, "password", username + "@email.com", 
				"" + RANDOM.nextInt(), "user"));
		service.removeUser(username); //delete user
		service.getUserData(username); //search for deleted user
	}
	
	@Test(expected=UserNotFoundException.class)
	public void testDeleteNonExistentUser() throws Exception {
		service.removeUser("unknown");
	}
	
	@Test(expected=DuplicateUserDataException.class)
	public void testAddUserWithDuplicateData() throws Exception {
		String username = "testuser" + RANDOM.nextInt();
		service.addUser(new User(username, "test@mail.com", "123456", "user"));
		service.addUser(new User(username, "test@mail.com", "123456", "user"));
	}
}
