package se.citerus.collabsearch.model;

public class SearchFinding {
	private String id;
	private Double lat;
	private Double lon;
	private String title;
	private String descr;
	
	public SearchFinding() {
	}
	
	public SearchFinding(String id, Double lat, Double lon, String title,
			String descr) {
		this.id = id;
		this.lat = lat;
		this.lon = lon;
		this.title = title;
		this.descr = descr;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
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
}
