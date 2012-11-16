package se.citerus.collabsearch.model.validator;

import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

import se.citerus.collabsearch.model.validator.DateValidator;
import se.citerus.collabsearch.model.validator.PhoneNumberValidator;
import se.citerus.collabsearch.model.validator.PriorityNumberValidator;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.IntegerValidator;
import com.vaadin.data.validator.StringLengthValidator;

public class DataValidationTest {

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
		try {
			validator.validate("123 456 789");
		} catch (InvalidValueException e) {
			fail("Failed to validate valid phone number");
		}
		
		//testing role validation
		validator = new StringLengthValidator("", 1, 99, false);
		generalValidationChecks(validator, "role", okRole);
	}

	@Test
	public void testSearchMissionValidator() {
		Validator validator = null;
		
		final String validName = "Example search mission";
		final String validDescr = "beskrivning...";
		final int validPrio = 10;
		
		//name validation
		validator = new StringLengthValidator("", 1, 30, false);
		generalValidationChecks(validator, "name", validName);
		
		//descr validation
		generalValidationChecks(validator, "descr", validDescr);
		try {
			validator.validate("aaaaabbbbbcccccdddddeeeeefffffggggg");
			fail("Failed to invalidate too long description");
		} catch (InvalidValueException e) {
			//ok
		}
		
		//prio validation
		validator = new PriorityNumberValidator("");
		generalValidationChecks(validator, "prio", "" + validPrio);
		try {
			validator.validate("test");
			fail("Failed to invalidate non-integer prio");
		} catch (InvalidValueException e) {
			//ok
		}
		try {
			validator.validate("1.5");
			fail("Failed to invalidate non-integer prio");
		} catch (InvalidValueException e) {
			//ok
		}
		try {
			validator.validate("-1");
			fail("Failed to invalidate negative prio");
		} catch (InvalidValueException e) {
			//ok
		}
	}

	@Test
	public void testSearchOperationValidator() {
		Validator validator = null;
		
		final String validTitle = "Sökoperation 1";
		final long validDate = Calendar.getInstance().getTime().getTime();
		
		validator = new StringLengthValidator("", 3, 99, false);
		generalValidationChecks(validator, "title", validTitle);
		
		validator = new DateValidator();
		try {
			validator.validate(validDate);
		} catch (InvalidValueException e) {
			fail("Failed to validate valid " + "date");
		}
		try {
			validator.validate("");
			fail("Failed to invalidate empty " + "date");
		} catch (InvalidValueException e) {
			//ok
		}
		try {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.set(2011, 1, 30);
			Date date = calendar.getTime();
			validator.validate(date.toString());
			fail("Failed to invalidate invalid date");
		} catch (Exception e) {
			//ok
		}
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
