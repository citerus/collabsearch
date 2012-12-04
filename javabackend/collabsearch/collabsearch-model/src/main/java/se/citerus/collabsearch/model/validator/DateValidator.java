package se.citerus.collabsearch.model.validator;

import java.util.Date;

import com.vaadin.data.Validator;

@SuppressWarnings("serial")
public class DateValidator implements Validator {

	public void validate(Object value) throws InvalidValueException {
		try {
			Date date = null;
			try { //try Date typecast first
				date = (Date) value;
			} catch (Exception e) { //fallback to Long-conversion
				date = new Date((Long)value);
			}
			if (date == null) {
				throw new InvalidValueException("Invalid date");
			}
		} catch (NumberFormatException e) {
			throw new InvalidValueException("Invalid date");
		} catch (Exception e) {
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
