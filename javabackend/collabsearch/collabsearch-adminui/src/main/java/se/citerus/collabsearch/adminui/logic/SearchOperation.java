package se.citerus.collabsearch.adminui.logic;

import java.util.Date;

public class SearchOperation {
	String title;
	String descr;
	Date date;
	
	public SearchOperation(String title, String descr, Date date) {
		super();
		this.title = title;
		this.descr = descr;
		this.date = date;
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
	
	@Override
	public String toString() {
		return title + ", " + descr + ", " + date.toString();
	}
	
}
