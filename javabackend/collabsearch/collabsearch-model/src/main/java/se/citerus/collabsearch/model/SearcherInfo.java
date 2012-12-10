package se.citerus.collabsearch.model;

public class SearcherInfo {
	private String id;
	private String name;
	private String email;
	private String tele;
	
	public SearcherInfo() {
	}
	
	public SearcherInfo(String id, String name, String email, String tele) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.tele = tele;
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getTele() {
		return tele;
	}
	
	@Override
	public String toString() {
		return "id: " + id + " name: " + name + " email: " + email + " tele: " + tele;
	}
}
