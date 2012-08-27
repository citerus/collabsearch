package se.citerus.collabsearch.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Status {
	
	private int id;
	private String name;
	private String descr;
	
	/**
	 * Serialization ctor.
	 */
	public Status() {
	}
	
	public Status(int id, String name, String descr) {
		super();
		this.id = id;
		this.name = name;
		this.descr = descr;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
