package se.citerus.collabsearch.model;

import java.awt.geom.Point2D.Double;

public class SearchZone {
	private String id;
	private String title;
	private int priority;
	private double startingX;
	private double startingY;
	private Double[] zoneCoords;
	private SearchFinding[] findings;
	private int zoomLevel;
	
	public SearchZone() {
		zoneCoords = new Double[0];
		findings = new SearchFinding[0];
	}
	
	public SearchZone(String title, int priority, double startingX,
			double startingY, Double[] zoneCoords, SearchFinding[] findings) {
		this.title = title;
		this.priority = priority;
		this.startingX = startingX;
		this.startingY = startingY;
		this.zoneCoords = zoneCoords;
		this.findings = findings;
	}

	public SearchZone(String title, int priority, Double[] points, int zoomLevel) {
		this(title, priority, 0, 0, points, new SearchFinding[0]);
		this.zoomLevel = zoomLevel;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getStartingX() {
		return startingX;
	}

	public void setStartingX(double startingX) {
		this.startingX = startingX;
	}

	public double getStartingY() {
		return startingY;
	}

	public void setStartingY(double startingY) {
		this.startingY = startingY;
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
	
}
