package se.citerus.collabsearch.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * String array wrapper used for sending arrays of string over the Jersey REST web service.
 * @author Ola Rende
 */
@XmlRootElement
public class StringArrayWrapper {
	private String string;
	
	public StringArrayWrapper() {
	}

	public StringArrayWrapper(String string) {
		this.string = string;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}
}
