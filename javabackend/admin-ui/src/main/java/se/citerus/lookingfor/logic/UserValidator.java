package se.citerus.lookingfor.logic;

import java.util.List;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import se.citerus.lookingfor.DAL.UserDALMongoDB;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;

public class UserValidator implements Validator {

	public boolean supports(Class clazz) {
		return User.class.equals(clazz);
	}

	public void validate(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "username.empty");
		User user = (User) obj;
		
		String email = user.getEmail();
		if (!email.contains("@")) {
			errors.reject("email", "email.notemail");
		}
		try {
			EmailValidator ev = new EmailValidator("Invalid email address");
			ev.validate(email);
		} catch (InvalidValueException e1) {
			errors.reject("email", "email.invalid");
		}
		
		String tele = user.getTele();
		if (!tele.matches("\\+*[0-9]+")) {
			errors.reject("tele", "tele.invalid");
		}
		
		String role = user.getRole();
		UserHandler uh = new UserHandler();
		boolean roleFound = false;
		List<String> listOfRoles = uh.getListOfRoles();
		for (String rolename : listOfRoles) {
			if (role.equals(rolename)) {
				roleFound = true;
				break;
			}
		}
		if (roleFound == false) {
			errors.reject("role", "role.notfound");
		}
	}

}
