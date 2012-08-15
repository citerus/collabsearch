package se.citerus.lookingfor.logic;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.IntegerValidator;

@SuppressWarnings("serial")
public class PriorityNumberValidator implements Validator {

	private final String errorMessage;

	public PriorityNumberValidator(String errorMessage) {
		this.errorMessage = errorMessage;
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
