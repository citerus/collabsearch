package se.citerus.lookingfor.logic;

import java.util.Date;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;

@SuppressWarnings("serial")
public class DateValidator implements Validator {

	public void validate(Object value) throws InvalidValueException {
		Date date = (Date) value;
		if (date == null) {
			throw new InvalidValueException("Invalid date");
		}
	}
	
	public boolean isValid(Object value) {
		try {
			validate(value);
		} catch (InvalidValueException e) {
			return false;
		}
		return true;
	}

}
