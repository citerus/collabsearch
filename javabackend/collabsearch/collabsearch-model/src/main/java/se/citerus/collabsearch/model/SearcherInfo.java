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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTele() {
		return tele;
	}

	public void setTele(String tele) {
		this.tele = tele;
	}

	public String getId() {
		return id;
	}
}
