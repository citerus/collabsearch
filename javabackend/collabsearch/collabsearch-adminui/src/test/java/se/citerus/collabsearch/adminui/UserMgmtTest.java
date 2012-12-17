/**
 * 
 */
package se.citerus.collabsearch.adminui;

import static org.springframework.util.Assert.notNull;

import java.io.IOException;
import java.util.Random;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.WriteResult;

import se.citerus.collabsearch.adminui.logic.UserService;
import se.citerus.collabsearch.model.User;
import se.citerus.collabsearch.model.exceptions.DuplicateUserDataException;
import se.citerus.collabsearch.model.exceptions.UserNotFoundException;
import se.citerus.collabsearch.store.inmemory.UserDAOInMemory;
import se.citerus.collabsearch.store.mongodb.UserDAOMongoDB;

/**
 * Unit tests for the user management.
 * @author Ola Rende
 */
public class UserMgmtTest {

	private static final Random RANDOM = new Random();

	private static UserService service;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AnnotationConfigApplicationContext context;
		context = new AnnotationConfigApplicationContext(UserService.class, UserDAOInMemory.class);
//		context = new AnnotationConfigApplicationContext(UserService.class, UserDAOMongoDB.class);
		service = context.getBean(UserService.class);
		service.setDebugMode();
		
		try {
			Mongo mongo = new Mongo();
			DB db = mongo.getDB("test");
			DBObject query = new BasicDBObject("username", Pattern.compile("testuser.*"));
			WriteResult result = db.getCollection("users").remove(query);
			System.out.println("Testusers removed: " + result.getN());
			mongo.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCreateAndFindUser() throws IOException,
			UserNotFoundException, DuplicateUserDataException {
		User user = new User("testuser1", "password", "testuser" + RANDOM.nextInt() + "@email.com", ""
				+ RANDOM.nextInt(), "user");
		service.addUser(user.getUsername(), user.getPassword(),
				user.getEmail(), user.getTele(), user.getRole());
		User foundUser = service.findUserByName(user.getUsername());
		notNull(foundUser);
		Assert.assertEquals(user.getUsername(), foundUser.getUsername());
//		Assert.assertEquals(user.getPassword(), foundUser.getPassword());
		Assert.assertEquals(user.getEmail(), foundUser.getEmail());
		Assert.assertEquals(user.getTele(), foundUser.getTele());
		Assert.assertEquals(user.getRole(), foundUser.getRole());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreateUserWithIllegalArguments() throws Exception {
		service.addUser(null, null, null, null, null);
	}
	
	@Test
	public void testEditUser() throws Exception {
		String userId = service.addUser("testuser2",
				"blab", "test@mail.se", "124214", "user");
		Assert.assertNotNull(userId);
		String username = "testuser2";
		String newEmail = "test2@mail.se";
		String newTele = "923875295";
		String newRole = "admin";
		service.editUser(userId, username, newEmail, newTele, newRole);
		User user = service.findUserByName(username);
		Assert.assertEquals(newEmail, user.getEmail());
		Assert.assertEquals(newTele, user.getTele());
		Assert.assertEquals(newRole, user.getRole());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testEditUserWithIllegalArguments() throws Exception {
		service.editUser(null, null, null, null, null);
	}
	
	@Test(expected=DuplicateUserDataException.class)
	public void testEditUserWithDuplicateData() throws Exception {
		String userId1 = service.addUser("testuser2.5", "hejsan", "testuser2.5@mail.se", "0951251992", "user");
		String userId2 = service.addUser("testuser2.75", "hejsan", "testuser2.75@mail.se", "09512519921", "user");
		//edit second user to use the first user's email and telephone number
		service.editUser(userId2, "testuser2.5", "testuser2.5@mail.se", "0951251992", "user");
	}
	
	@Test
	public void testEditUserDoesntChangePassword() throws Exception {
		String userId = service.addUser("testuser2.0", "password", "testuser2pointOh@mail.se", "29385728935732", "user");
		Assert.assertNotNull(userId);
		//change role to admin
		service.editUser(userId, "testuser2.0", "testuser2point1@mail.se", "29385728935731", "admin");
		User user = service.findUserById(userId);
		String passwordAsSHA256sum = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8";
		Assert.assertEquals(passwordAsSHA256sum, user.getPassword());
	}
	
	@Test(expected = UserNotFoundException.class)
	public void testFindNonExistentUser() throws IOException,
	UserNotFoundException {
		service.findUserByName("thisusernamedoesnotexist");
	}
	
	@Test
	public void testChangePassword() throws Exception {
		String origPassword = "blab";
		String userId = service.addUser("testuser3",
				origPassword, "test3@mail.se", "1209875214", "user");
		service.changePassword(userId, "blab2");
		User user = service.findUserById(userId);
		Assert.assertNotSame(origPassword, user.getPassword());
	}

	@Test(expected = UserNotFoundException.class)
	public void testDeleteUser() throws Exception {
		String username = "testuser4"; // create user
		User user = new User(username, "password", username + "@email.com",
				"" + RANDOM.nextInt(Integer.MAX_VALUE), "user");
		service.addUser(user.getUsername(), user.getPassword(),
				user.getEmail(), user.getTele(), user.getRole());
		service.removeUser(username); // delete user
		service.findUserByName(username); // search for deleted user
	}

	@Test(expected = UserNotFoundException.class)
	public void testDeleteNonExistentUser() throws Exception {
		service.removeUser("unknown");
	}

	@Test(expected = DuplicateUserDataException.class)
	public void testAddUserWithDuplicateData() throws Exception {
		String username = "testuser5";
		User user = new User(username, "passwörd", "test@mail.com", 
				"123456", "user");
		service.addUser(user.getUsername(), user.getPassword(),
				user.getEmail(), user.getTele(), user.getRole());
		service.addUser(user.getUsername(), user.getPassword(),
				user.getEmail(), user.getTele(), user.getRole());
	}

	@Test(expected = UserNotFoundException.class)
	public void testEditedUsernameReplacesOldUser() throws Exception {
		String userId = service.addUser("testuser6", "password", "testuser6@mail.se", "29385728935732234", "user");
		service.editUser(userId, "testuser6v2", "testuser6v2@mail.se", "2039858732", "user");
		User foundUser = service.findUserByName("testuser6v2");
		Assert.assertNotNull(foundUser);
		service.findUserByName("testuser6");
	}
}
