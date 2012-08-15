package se.citerus.lookingfor.logic;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;

@SuppressWarnings("serial")
public class DateValidator implements Validator {

	public void validate(Object value) throws InvalidValueException {
		try {
			//Date date = DateFormat.getInstance().parse(value.toString());
			Date date = DateFormat.getDateInstance().parse(value.toString());
			if (date == null) {
				throw new InvalidValueException("Invalid date");
			}
		} catch (ParseException e) {
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
