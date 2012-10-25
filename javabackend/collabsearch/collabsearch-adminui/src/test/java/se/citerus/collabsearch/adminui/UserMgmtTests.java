/**
 * 
 */
package se.citerus.collabsearch.adminui;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import se.citerus.collabsearch.adminui.logic.Authenticator;
import se.citerus.collabsearch.adminui.logic.UserService;
import se.citerus.collabsearch.model.User;
import se.citerus.collabsearch.model.exceptions.UserNotFoundException;

/**
 * Unit tests for the user management.
 * @author Ola Rende
 */
public class UserMgmtTests {

	private static final Random RANDOM = new Random();
	private UserService handler;
	private User dummyUser;

	@Before
	public void setUp() throws Exception {
		handler = new UserService();
	}

	@After
	public void tearDown() throws Exception {
		handler.cleanUp();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DB db = new Mongo().getDB("lookingfor");
		DBCollection userColl = db.getCollection("users");
		Pattern regex = Pattern.compile("testuser", Pattern.CASE_INSENSITIVE);
		userColl.remove(new BasicDBObject("name", regex));
	}

	@Test
	public void testCreateAndFindUser() throws IOException, UserNotFoundException {
		String username = "testuser" + RANDOM.nextInt();
		dummyUser = new User(username, "password", username + "@email.com", 
				"" + RANDOM.nextInt(), "user");
		handler.addUser(dummyUser);
		User user = handler.getUserData(dummyUser.getUsername());
	}
	
	@Test(expected=UserNotFoundException.class)
	public void testFindNonExistentUser() throws IOException, UserNotFoundException {
		handler.getUserData("thisusernamedoesnotexist");
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
		handler.addUser(new User(username, "password", username + "@email.com", 
				"" + RANDOM.nextInt(), "user"));
		handler.removeUser(username); //delete user
		handler.getUserData(username); //search for deleted user
	}
	
	@Test(expected=UserNotFoundException.class)
	public void testDeleteNonExistentUser() throws Exception {
		handler.removeUser("unknown");
	}
	
	@Test(expected=Exception.class)
	public void testAddUserWithDuplicateData() throws Exception {
		handler.addUser(new User("omega", null, null, null));
	}
}
