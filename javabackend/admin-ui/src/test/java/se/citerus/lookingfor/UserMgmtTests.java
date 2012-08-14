/**
 * 
 */
package se.citerus.lookingfor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.citerus.lookingfor.logic.Authenticator;
import se.citerus.lookingfor.logic.User;
import se.citerus.lookingfor.logic.UserService;

/**
 * Unit tests for the user management.
 * @author Ola Rende
 */
public class UserMgmtTests {

	private UserService handler;

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
		try {
			handler.editUser(new User("test","test".toCharArray()));
		} catch (Exception e) {
			e.printStackTrace();
			fail("IOException on user creation");
		}
		try {
			boolean userFound = handler.getUserData("test") != null;
			assertTrue("User \"test\" not found in database", userFound == true);
		} catch (Exception e) {
			fail("Exception throw on user search");
		}
	}
	
	@Test
	public void testFindNonExistentUser() {
		User user = null;
		try {
			user = handler.getUserData("test");
			fail("Did not throw exception");
		} catch (Exception e) {
			//expected
		}
		assertTrue("User \"user12345\" found in database, should be missing", user == null);
	}
	
	@Test
	public void testUserAuthFailure() throws Exception {
		boolean userAuthResult;
		Authenticator auth = new Authenticator();
		userAuthResult = auth.login("test", "wrongpassword".toCharArray());
		assertTrue("User \"test\" authenticated with wrong password, should throw error", userAuthResult == false);
	}
	
	@Test
	public void testDeleteUser() {
		//create user
		try {
			handler.editUser(new User("testuser123","password"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		//delete user
		Boolean removeUserResult = false;
		try {
			removeUserResult = handler.removeUser("testuser123");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		assertTrue("Removal of testuser123 returned false, should be true", removeUserResult);
		
		//search for deleted user
		try {
			handler.getUserData("testuser123");
			fail("Did not throw exception");
		} catch (Exception e) {
			//expected
		}
	}
	
	@Test
	public void testDeleteNonExistentUser() {
		Boolean removeUserResult = true;
		try {
			removeUserResult = handler.removeUser("unknown");
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertFalse("Removal of nonexistent user \"unknown\" returned true, should be false", removeUserResult);
	}
}
