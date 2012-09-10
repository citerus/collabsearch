package se.citerus.collabsearch.model;

public class Zone {
	private String id;
	private String name;
	
	public Zone() {
	}

	public Zone(String id, String name) {
		this.setId(id);
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
