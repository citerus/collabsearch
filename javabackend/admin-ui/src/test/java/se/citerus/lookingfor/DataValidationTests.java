package se.citerus.lookingfor;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import se.citerus.lookingfor.logic.User;
import se.citerus.lookingfor.logic.UserValidator;

public class DataValidationTests {

	private UserValidator uv;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void testUserValidator() {
		uv = new UserValidator();
		User user = null;
		Errors errors = null; //TODO implement Errors?
		
		final String okUsername = "user123";
		final String okPassword = "password";
		final String okEmail = "user@mail.test";
		final String okTele = "123456789";
		final String okRole = "admin";
		
		user = new User(okUsername, okPassword, okEmail, okTele, okRole);
		uv.validate(user, errors);
		if (errors.hasErrors()) {
			fail("Failed to validate acceptable user data");
		}
		
		user = new User(null, okPassword, okEmail, okTele, okRole);
		uv.validate(user, errors);
		if (!errors.hasErrors()) {
			fail("Validated unacceptable username: " + null);
		} else {
			assertNotNull("Did not get username validation error", errors.getFieldError("username"));
		}
		
		user = new User(okUsername, null, okEmail, okTele, okRole);
		uv.validate(user, errors);
		if (!errors.hasErrors()) {
			fail("Validated unacceptable password: " + null);
		} else {
			assertNotNull("Did not get password validation error", errors.getFieldError("password"));
		}
		
		user = new User(okUsername, okPassword, "user_at_email.test", okTele, okRole);
		uv.validate(user, errors);
		if (!errors.hasErrors()) {
			fail("Validated unacceptable email: " + "user_at_email.test");
		} else {
			assertNotNull("Did not get email validation error", errors.getFieldError("email"));
		}
		
		user = new User(okUsername, okPassword, okEmail, "error", okRole);
		uv.validate(user, errors);
		if (!errors.hasErrors()) {
			fail("Validated unacceptable telephone number: " + null);
		} else {
			assertNotNull("Did not get telephone number validation error", errors.getFieldError("tele"));
		}
		
		user = new User(okUsername, okPassword, okEmail, okTele, "unknown");
		uv.validate(user, errors);
		if (!errors.hasErrors()) {
			fail("Validated unacceptable role: " + "unknown");
		} else {
			assertNotNull("Did not get role validation error", errors.getFieldError("role"));
		}
	}
	
	@Test
	public void testSearchMissionValidator() {
		fail("Not yet implemented");
	}

	@Test
	public void testSearchOperationValidator() {
		fail("Not yet implemented");
	}
}
