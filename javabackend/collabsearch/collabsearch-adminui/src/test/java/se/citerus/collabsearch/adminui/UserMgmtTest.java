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

/**
 * Unit tests for the user management.
 * @author Ola Rende
 */
public class UserMgmtTest {

	private static final Random RANDOM = new Random();

	private static UserService service;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(
					"se.citerus.collabsearch.adminui",
					"se.citerus.collabsearch.store"
				);
		service = context.getBean(UserService.class);
		service.setDebugMode();
		
		Mongo mongo = new Mongo();
		DB db = mongo.getDB("test");
		DBObject query = new BasicDBObject("username", Pattern.compile("testuser.*"));
		WriteResult result = db.getCollection("users").remove(query);
		System.out.println("Testusers removed: " + result.getN());
		mongo.close();
	}

	@Test
	public void testCreateAndFindUser() throws IOException,
			UserNotFoundException, DuplicateUserDataException {
		User user = new User("testuser1", "password", "testuser" + RANDOM.nextInt() + "@email.com", ""
				+ RANDOM.nextInt(), "user");
		service.addUser(user.getUsername(), user.getPassword(),
				user.getEmail(), user.getTele(), user.getRole());
		User foundUser = service.findUser(user.getUsername());
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
		String username = service.addUser("testuser2",
				"blab", "test@mail.se", "124214", "user");
		String newEmail = "test2@mail.se";
		String newTele = "923875295";
		String newRole = "admin";
		service.editUser(username, newEmail, newTele, newRole);
		User user = service.findUser(username);
		Assert.assertEquals(newEmail, user.getEmail());
		Assert.assertEquals(newTele, user.getTele());
		Assert.assertEquals(newRole, user.getRole());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testEditUserWithIllegalArguments() throws Exception {
		service.editUser(null, null, null, null);
	}
	
	@Test(expected=DuplicateUserDataException.class)
	public void testEditUserWithDuplicateData() throws Exception {
		service.addUser("testuser2.5", "hejsan", "testuser2.5@mail.se", "0951251992", "user");
		service.addUser("testuser2.75", "hejsan", "testuser2.75@mail.se", "09512519921", "user");
		//edit second user to use the first user's email and telephone number
		service.editUser("testuser2.75", "testuser2.5@mail.se", "0951251992", "user");
	}
	
	@Test(expected = UserNotFoundException.class)
	public void testFindNonExistentUser() throws IOException,
	UserNotFoundException {
		service.findUser("thisusernamedoesnotexist");
	}
	
	@Test
	public void testChangePassword() throws Exception {
		String origPassword = "blab";
		String username = service.addUser("testuser3",
				origPassword, "test3@mail.se", "1209875214", "user");
		service.changePassword(username, "blab2");
		User user = service.findUser(username);
		Assert.assertNotSame(origPassword, user.getPassword());
	}

	@Test(expected = Exception.class)
	public void testDeleteUser() throws Exception {
		String username = "testuser4"; // create user
		User user = new User(username, "password", username + "@email.com",
				"" + RANDOM.nextInt(Integer.MAX_VALUE), "user");
		service.addUser(user.getUsername(), user.getPassword(),
				user.getEmail(), user.getTele(), user.getRole());
		service.removeUser(username); // delete user
		service.findUser(username); // search for deleted user
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
}
