package se.citerus.collabsearch.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SearchOperation {
	private String title;
	private String descr;
	private Date date;
	private String location;
	
	/**
	 * Serialization ctor.
	 */
	public SearchOperation() {
	}

	public SearchOperation(String title, String descr, Date date, String location) {
		super();
		this.title = title;
		this.descr = descr;
		this.date = date;
		this.location = location;
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return title + ", " + descr + ", " + date.toString() + ", " + location;
	}
	
}
