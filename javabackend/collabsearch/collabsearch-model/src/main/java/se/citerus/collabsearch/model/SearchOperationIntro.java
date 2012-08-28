package se.citerus.collabsearch.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SearchOperationIntro {
	private String title;
	private String descr;

	/**
	 * Serialization ctor.
	 */
	public SearchOperationIntro() {
		//intentionally left empty
	}
	
	public SearchOperationIntro(String title, String descr) {
		this.title = title;
		this.descr = descr;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}
	
	@Override
	public String toString() {
		return title + ", " + descr;
	}

}