package se.citerus.collabsearch.model.validator;

import com.vaadin.data.Validator;;

@SuppressWarnings("serial")
public class PhoneNumberValidator implements Validator {

	private final String errorMessage;

	public PhoneNumberValidator(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public void validate(Object value) throws InvalidValueException {
		if (value != null) {
			String strVal = ((String)value);
			if (!strVal.matches("[\\d\\s]+")) {
				throw new InvalidValueException(errorMessage);
			}
		} else {
			throw new InvalidValueException(errorMessage);
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
