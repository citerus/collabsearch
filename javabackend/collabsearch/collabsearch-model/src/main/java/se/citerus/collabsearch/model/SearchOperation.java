package se.citerus.collabsearch.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SearchOperation {
	private String title;
	private String descr;
	private Date date;
	private String location;
	private Status status;
	
	/**
	 * Serialization ctor.
	 */
	public SearchOperation() {
	}

	public SearchOperation(String title, String descr, 
			Date date, String location, Status status) {
		this.title = title;
		this.descr = descr;
		this.date = date;
		this.location = location;
		this.setStatus(status);
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
	
	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return title + ", " + descr + ", " 
			+ date.toString() + ", " + location
			+ ", " + getStatus().toString();
	}
	
}
