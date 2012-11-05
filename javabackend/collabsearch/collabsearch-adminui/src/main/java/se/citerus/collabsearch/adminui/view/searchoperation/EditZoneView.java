package se.citerus.collabsearch.adminui.view.searchoperation;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.Validate;
import org.vaadin.hezamu.googlemapwidget.GoogleMap;
import org.vaadin.hezamu.googlemapwidget.GoogleMap.MapClickListener;
import org.vaadin.hezamu.googlemapwidget.overlay.BasicMarker;
import org.vaadin.hezamu.googlemapwidget.overlay.Marker;
import org.vaadin.hezamu.googlemapwidget.overlay.PolyOverlay;
import org.vaadin.hezamu.googlemapwidget.overlay.Polygon;

import se.citerus.collabsearch.adminui.ViewSwitchController;
import se.citerus.collabsearch.adminui.logic.SearchOperationService;
import se.citerus.collabsearch.model.SearchFinding;
import se.citerus.collabsearch.model.SearchZone;

import com.vaadin.Application;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class EditZoneView extends CustomComponent {
	
	private static final int DEFAULT_ZOOM = 9;
	private final ViewSwitchController listener;
	private VerticalLayout mainLayout;
	private GoogleMap map;
	private String zoneId;
	private ZoneViewFragment fragment;
	
	private List<Marker> markerPoints;
	private Random random;
	private Double mapCenter;
	private int mapZoom;

	public EditZoneView(ViewSwitchController listener) {
		this.listener = listener;
		mainLayout = new VerticalLayout();
		setCompositionRoot(mainLayout);
	}

	public void init() {
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false, true, false, true);
		
		fragment = new ZoneViewFragment();
		fragment.init("Redigera zon");
		fragment.setHeight("10%");
		mainLayout.addComponent(fragment);
		mainLayout.setComponentAlignment(fragment, Alignment.TOP_CENTER);
		
		markerPoints = new ArrayList<Marker>();
		
		fragment.saveButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					SearchOperationService service = new SearchOperationService();
					String title = fragment.nameField.getValue().toString();
					String prioStr = fragment.prioField.getValue().toString();
					Double[] points = null;
					Collection<PolyOverlay> overlays = map.getOverlays();
					if (overlays != null && !overlays.isEmpty()) {
						PolyOverlay overlay = overlays.iterator().next();
						points = overlay.getPoints();
					}
					service.editZone(zoneId, title, prioStr, points, map.getZoom());
					
					listener.switchToSearchMissionListView();
				} catch (Exception e) {
					e.printStackTrace();
					listener.displayError("Fel vid sparning", 
						"Ett fel uppstod vid sparningen av zonen");
				}
			}
		});
		
		fragment.backButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listener.switchToSearchMissionListView();
			}
		});
		
		fragment.clearMapButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				for (Marker marker : markerPoints) {
					map.removeMarker(marker);
				}
				markerPoints.clear();
				Collection<PolyOverlay> overlays = map.getOverlays();
				while (overlays.iterator().hasNext()) {
					PolyOverlay overlay = overlays.iterator().next();
					map.removeOverlay(overlay);
				}
			}
		});
		
		fragment.createZoneButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (markerPoints.size() < 3) {
					listener.displayError("Fel", "Zoner måste ha minst 3 punkter");
					return;
				}
				
				//Make points array one slot bigger to allow for polygon-closing point.
				Double[] points = new Double[markerPoints.size()+1];
				for (int i = 0; i < markerPoints.size(); i++) {
					points[i] = markerPoints.get(i).getLatLng();
				}
				//add a reference to the first point at the end of the array, to close the polygon.
				points[points.length-1] = markerPoints.get(0).getLatLng();
				
				map.addPolyOverlay(new Polygon(generateId(), points, 
						"#000000", 2, 1.0, "#F00C0C", 0.3, true));
				
				for (Marker marker : markerPoints) {
					map.removeMarker(marker);
				}
				markerPoints.clear();
			}
		});
		
		fragment.setMapCenterButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				mapCenter = map.getCenter();
				mapZoom = map.getZoom();
			}
		});
	}

	private Long generateId() {
		if (random == null) {
			random = new Random();
		}
		return random.nextLong();
	}

	public void resetView(String zoneId, String opId) {
		this.zoneId = zoneId;
		if (map == null) {
			initMap();
		}
		
		markerPoints.clear();
		
		map.removeAllMarkers();
		Collection<PolyOverlay> overlays = map.getOverlays();
		for (PolyOverlay overlay : overlays) {
			map.removeOverlay(overlay);
		}
		
		SearchOperationService service;
		SearchZone zone = null;
		try {
			service = new SearchOperationService();
			zone = service.getZone(zoneId);
			
			//paint the predefined zone
			Polygon overlay;
			Double[] points = zone.getZoneCoords();
			if (points.length > 0) { //TODO change here to enable multiple zones
				overlay = new Polygon(1L, points, 
						"#000000", 2, 1.0, "#F00C0C", 0.3, true);
				map.addPolyOverlay(overlay);
			}
			
			//center map on center of zone
			map.setCenter(points[0]);
			
			if (zone.getZoomLevel() > 0) {
				map.setZoom(zone.getZoomLevel());
			} else {
				map.setZoom(DEFAULT_ZOOM);
			}
			
			//paint the findings
			SearchFinding[] findings = zone.getFindings();
			long genId = 2L;
			for (SearchFinding finding : findings) {
				Double latLon = new Double(finding.getLat(), finding.getLon());
				BasicMarker marker = new BasicMarker(genId, latLon, finding.getTitle());
				marker.setInfoWindowContent(map, new Label(finding.getDescr()));
				marker.setDraggable(false);
				map.addMarker(marker);
				genId++;
			}
			
			fragment.nameField.setValue(zone.getTitle());
			fragment.prioField.setValue(zone.getPriority());
		} catch (Exception e) {
			e.printStackTrace();
			listener.displayError("Fel vid hämtning av data", 
				"Ett fel uppstod vid hämtningen av data från servern: " + e.getMessage());
		}
	}

	private void initMap() {
		map = makeGoogleMap();
		fragment.setMap(map);
		
		map.addListener(new MapClickListener() {
			@Override
			public void mapClicked(Double clickPos) {
				try {
					BasicMarker marker = new BasicMarker(generateId(), clickPos, "" + markerPoints.size());
					map.addMarker(marker);
					markerPoints.add(marker);
					System.out.println("Created marker at (" + clickPos.x + "," + clickPos.y + ")");
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
		});
		
		map.addListener(new GoogleMap.MarkerClickListener() {
			@Override
			public void markerClicked(Marker clickedMarker) {
				System.out.println("" + clickedMarker.getTitle() + " clicked!");
			}
		});
	}

	private GoogleMap makeGoogleMap() {
		Application application = getApplication();
		Validate.notNull(application);
		
		//example coords for Gothenburg
		final double lat = 11.977844238281250;
		final double lon = 57.717352096870876;
		
		Point2D.Double mapCenterMarker = new Point2D.Double(lat, lon);
		GoogleMap googleMap = new GoogleMap(application, mapCenterMarker, 9);
		googleMap.setWidth("100%");
		googleMap.setHeight("500px");
		
		return googleMap;
	}
}
