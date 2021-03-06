package se.citerus.collabsearch.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SearchOperation { //göra klasser från XSD?
	private String id;
	private String title;
	private String descr;
	private Date date;
	private String location;
	private Status status;
	
	private List<SearcherInfo> searchers;
	
	private List<SearchZone> zones;
	private List<SearchGroup> groups;
	
	/**
	 * Serialization ctor.
	 */
	public SearchOperation() {
		//Constructor intentionally left empty.
	}

	public SearchOperation(String id, String title, String descr, 
			Date date, String location, Status status) {
		this.id = id;
		this.title = title;
		this.descr = descr;
		this.date = date;
		this.location = location;
		this.status = status;
		
		zones = new ArrayList<SearchZone>();
		groups = new ArrayList<SearchGroup>();
		searchers = new ArrayList<SearcherInfo>();
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

	public List<SearchZone> getZones() {
		return zones;
	}

	public void setZones(List<SearchZone> zones) {
		this.zones = zones;
	}

	public List<SearchGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<SearchGroup> groups) {
		this.groups = groups;
	}

	public List<SearcherInfo> getSearchers() {
		return searchers;
	}

	public void setSearchers(List<SearcherInfo> searchers) {
		this.searchers = searchers;
	}

	@Override
	public String toString() {
		return title + ", " + descr + ", " 
			+ date.toString() + ", " + location
			+ ", " + getStatus().toString();
	}
	
}
