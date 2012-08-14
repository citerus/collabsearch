package se.citerus.lookingfor;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.citerus.lookingfor.logic.PhoneNumberValidator;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;

public class DataValidationTests {

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
		Validator validator = null;
		
		final String okUsername = "user123";
		final String okPassword = "password";
		final String okEmail = "user@mail.test";
		final String okTele = "123456789";
		final String okRole = "admin";
		
		//testing username validation
		validator = new StringLengthValidator(
			"Name validation error", 1, 99, false);
		generalValidationChecks(validator, "username", okUsername);
		
		//testing password validation
		validator = new StringLengthValidator(
				"Password validation error", 1, 99, false);
		generalValidationChecks(validator, "password", okPassword);
		
		//testing email validation
		validator = new EmailValidator("");
		generalValidationChecks(validator, "email", okEmail);
		try {
			validator.validate("mail_at_mail.com");
		} catch (InvalidValueException e) {
			//ok
		}
		
		//testing phone number validation
		validator = new PhoneNumberValidator("");
		generalValidationChecks(validator, "phone number", okTele);
		try {
			validator.validate("555-HEJSAN");
			fail("Failed to invalidate invalid phone number");
		} catch (InvalidValueException e) {
			//ok
		}
		
		//testing role validation
		validator = new StringLengthValidator("", 1, 99, false);
		generalValidationChecks(validator, "role", okRole);
	}

	@Test
	public void testSearchMissionValidator() {
		Validator validator = null;
		
		final String validName = "Example search mission";
		final String validDescr = "";
		final String valid
	}

	@Test
	public void testSearchOperationValidator() {
		//fail("Not yet implemented");
	}
	
	private void generalValidationChecks(Validator validator, String msg, String validData) {
		try {
			validator.validate(validData);
		} catch (InvalidValueException e) {
			fail("Failed to validate valid " + msg);
		}
		try {
			validator.validate("");
			fail("Failed to invalidate empty " + msg);
		} catch (InvalidValueException e) {
			//ok
		}
	}
}
