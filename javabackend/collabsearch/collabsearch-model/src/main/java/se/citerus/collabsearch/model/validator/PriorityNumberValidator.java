package se.citerus.collabsearch.model.validator;

import com.vaadin.data.Validator;

@SuppressWarnings("serial")
public class PriorityNumberValidator implements Validator {

	public PriorityNumberValidator(String errorMessage) {
	}
	
	public void validate(Object value) throws InvalidValueException {
		try {
			int intValue = Integer.parseInt((String)value);
			if (intValue <= 0) {
				throw new InvalidValueException("Value is less than or equal to 0");
			}
//			if (intValue < 7 || intValue > 21) {
//				throw new InvalidValueException("Value is out of bounds (7-21)");
//			}
		} catch (NumberFormatException e) {
			throw new InvalidValueException("Value is not a number");
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
