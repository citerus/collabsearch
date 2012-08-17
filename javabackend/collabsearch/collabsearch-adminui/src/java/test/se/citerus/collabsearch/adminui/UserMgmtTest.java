/**
 * 
 */
package se.citerus.collabsearch.adminui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.citerus.collabsearch.adminui.logic.Authenticator;
import se.citerus.collabsearch.adminui.logic.User;
import se.citerus.collabsearch.adminui.logic.UserService;

/**
 * Unit tests for the user management.
 * @author Ola Rende
 */
public class UserMgmtTest {

	private static final Random RANDOM = new Random();
	private UserService handler;
	private User dummyUser;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		handler = new UserService();
	}

	@After
	public void tearDown() throws Exception {
		handler.cleanUp();
	}

	@Test
	public void testCreateAndFindUser() {
		String username = "testuser" + RANDOM.nextInt();
		dummyUser = new User(username, "password", username + "@email.com", 
				"" + RANDOM.nextInt(), "user");
		try {
			handler.addUser(dummyUser);
		} catch (Exception e) {
			fail("IOException on user creation: " + e.getMessage());
		}
		try {
			User user = handler.getUserData(dummyUser.getUsername());
		} catch (Exception e) {
			fail("User " + dummyUser.getUsername() + " should have been in database");
		}
	}
	
	@Test
	public void testFindNonExistentUser() {
		User user = null;
		String nonUser = "thisusernamedoesnotexist";
		try {
			user = handler.getUserData(nonUser);
			fail("Did not throw exception for non-existent user: " + nonUser);
		} catch (Exception e) {
			//expected
		}
		assertTrue("User \"user12345\" found in database, should be missing", user == null);
	}
	
	@Test
	public void testUserAuthFailure() throws Exception {
		boolean userAuthResult;
		Authenticator auth = new Authenticator();
		userAuthResult = auth.login("testusr", "wrongpassword".toCharArray());
		assertTrue("User \"test\" authenticated with wrong password, should throw error", userAuthResult == false);
	}
	
	@Test
	public void testDeleteUser() {
		//create user
		String username = "testuser" + RANDOM.nextInt();
		try {
			handler.addUser(new User(username, "password", username + "@email.com", 
					"" + RANDOM.nextInt(), "user"));
		} catch (Exception e1) {
			fail("Failed to create new user: " + e1.getMessage());
		}
		
		//delete user
		try {
			handler.removeUser(username);
		} catch (Exception e1) {
			fail("Deletable user not deleted");
		}
		
		//search for deleted user
		try {
			handler.getUserData(username);
			fail("Did not throw exception");
		} catch (Exception e) {
			//expected
		}
	}
	
	@Test
	public void testDeleteNonExistentUser() {
		try {
			handler.removeUser("unknown");
			fail("Did not throw exception on non-existent user deletion");
		} catch (Exception e) {
			//expected
		}
	}
	
	@Test
	public void testAddUserWithDuplicateData() {
		try {
			User user = new User("omega", null, null, null);
			handler.addUser(user);
			fail("Did not throw exception");
		} catch (Exception e) {
			//expected
		}
	}
}
