/**
 * 
 */
package se.citerus.lookingfor;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.citerus.lookingfor.DAL.UserDAL;
import se.citerus.lookingfor.logic.User;

/**
 * 
 * @author ola
 */
public class UserTests {

	private static UserDAL userDAL;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		userDAL = new UserDAL();		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		userDAL.disconnect();
	}

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void testCreateAndFindUser() {
		try {
			userDAL.addOrModifyUser(new User("test","test"));
		} catch (IOException e) {
			e.printStackTrace();
			fail("IOException on user creation");
		}
		boolean userFound = userDAL.findUser("test", "test".toCharArray());
		assertTrue("User \"test\" not found in database", userFound == true);
	}
	
	@Test
	public void testFindNonExistentUser() {
		boolean userFound = userDAL.findUser("user12345", "test".toCharArray());
		assertTrue("User \"user12345\" found in database, should be missing", userFound == false);
	}

}
