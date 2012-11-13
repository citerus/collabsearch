package se.citerus.collabsearch.model;

import java.awt.geom.Point2D.Double;

public class SearchZone {
	private String id;
	private String title;
	private int priority;
	private Double[] zoneCoords;
	private SearchFinding[] findings;
	private int zoomLevel;
	private Double center;
	private String groupId;
	
	public SearchZone() {
		zoneCoords = new Double[0];
		findings = new SearchFinding[0];
	}

	public SearchZone(String title, int priority, Double[] zoneCoords,
			int zoomLevel, Double center, String groupId) {
		this.title = title;
		this.priority = priority;
		this.zoneCoords = zoneCoords;
		this.zoomLevel = zoomLevel;
		this.center = center;
		this.groupId = groupId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Double[] getZoneCoords() {
		return zoneCoords;
	}

	public void setZoneCoords(Double[] zoneCoords) {
		this.zoneCoords = zoneCoords;
	}

	public SearchFinding[] getFindings() {
		return findings;
	}

	public void setFindings(SearchFinding[] findings) {
		this.findings = findings;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getZoomLevel() {
		return zoomLevel;
	}

	public void setZoomLevel(int zoomLevel) {
		this.zoomLevel = zoomLevel;
	}

	public Double getCenter() {
		return center;
	}

	public void setCenter(Double center) {
		this.center = center;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
}
