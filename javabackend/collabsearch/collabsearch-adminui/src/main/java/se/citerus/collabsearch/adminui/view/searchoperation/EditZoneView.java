package se.citerus.collabsearch.adminui.view.searchoperation;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.hezamu.googlemapwidget.GoogleMap;
import org.vaadin.hezamu.googlemapwidget.GoogleMap.MapClickListener;
import org.vaadin.hezamu.googlemapwidget.overlay.BasicMarker;
import org.vaadin.hezamu.googlemapwidget.overlay.Marker;
import org.vaadin.hezamu.googlemapwidget.overlay.PolyOverlay;
import org.vaadin.hezamu.googlemapwidget.overlay.Polygon;

import se.citerus.collabsearch.adminui.ViewSwitchController;
import se.citerus.collabsearch.adminui.logic.SearchOperationService;
import se.citerus.collabsearch.model.SearchFinding;
import se.citerus.collabsearch.model.SearchGroup;
import se.citerus.collabsearch.model.SearchZone;
import se.citerus.collabsearch.model.exceptions.SearchGroupNotFoundException;
import se.citerus.collabsearch.model.exceptions.SearchOperationNotFoundException;
import se.citerus.collabsearch.model.exceptions.SearchZoneNotFoundException;

import com.vaadin.Application;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Configurable(preConstruction=true)
public class EditZoneView extends CustomComponent {
	
	private static final double DEFAULT_LAT = 15.11718750000000;
	private static final double DEFAULT_LON = 62.30879369102805;
	private static final int DEFAULT_ZOOM = 5;
	
	private final ViewSwitchController listener;
	private VerticalLayout mainLayout;
	private GoogleMap map;
	private String zoneId;
	private ZoneViewFragment fragment;
	
	@Autowired
	private SearchOperationService service;
	
	private List<Marker> markerPoints;
	private Random random;

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
					String title = fragment.nameField.getValue().toString();
					String prioStr = fragment.prioField.getValue().toString();
					Double[] points = null;
					Collection<PolyOverlay> overlays = map.getOverlays();
					if (overlays != null && !overlays.isEmpty()) {
						PolyOverlay overlay = overlays.iterator().next();
						points = overlay.getPoints();
					}
					Double center = (points == null || points.length == 0) ? map.getCenter() : points[0];
					String groupId = null;
					Object checkboxSelection = fragment.assignedGroupDropdown.getValue();
					if (checkboxSelection != null) {
						Item item = fragment.assignedGroupDropdown.getItem(checkboxSelection);
						groupId = item.getItemProperty("id").getValue().toString();
					}
					service.editZone(zoneId, title, prioStr, points, map.getZoom(), center, groupId);
					
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
		
		SearchZone zone = null;
		try {
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
			map.setCenter(zone.getCenter());
			
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
			
			fragment.assignedGroupDropdown.select(null);
			try {
				List<SearchGroup> groupList = service.getSearchGroupsByOp(
						service.getZoneParent(zoneId));
				fragment.setupGroupComboBox(groupList);
				String groupId = zone.getGroupId();
				Container container = fragment.assignedGroupDropdown.getContainerDataSource();
				for (Object itemId : container.getItemIds()) {
					Item item = container.getItem(itemId);
					String id = item.getItemProperty("id").toString();
					if (id.equals(groupId)) {
						fragment.assignedGroupDropdown.select(itemId);
					}
				}
			} catch (SearchGroupNotFoundException e) {
				//no groups created for this op, this is acceptable.
			}
			
			fragment.nameField.setValue(zone.getTitle());
			fragment.prioField.setValue(zone.getPriority());
		} catch (SearchZoneNotFoundException e) {
			listener.displayError("Fel", "Sökzonen ej funnen");
			listener.switchToSearchMissionListView();
		} catch (SearchOperationNotFoundException e) {
			e.printStackTrace();
			listener.displayError("Fel", 
					"Zonen har ingen tillhörade sökoperation");
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
		Application app = getApplication();
		Validate.notNull(app);
		
		Double mapCenter = new Double(DEFAULT_LAT, DEFAULT_LON);
		GoogleMap googleMap = new GoogleMap(app, mapCenter, DEFAULT_ZOOM);
		googleMap.setWidth("100%");
		googleMap.setHeight("500px");
		
		return googleMap;
	}
}
