package se.citerus.collabsearch.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A lighter version of the SearchOperation class meant to be used in
 * the public interface to provide an introduction to a search operation
 * with the user able to request more information based.
 * @author Ola Rende
 */
@XmlRootElement
public class SearchOperationIntro {
	private String id;
	private String title;
	private String descr;

	/**
	 * Serialization ctor.
	 */
	public SearchOperationIntro() {
		//intentionally left empty
	}
	
	public SearchOperationIntro(String id, String title, String descr) {
		this.id = id;
		this.title = title;
		this.descr = descr;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
