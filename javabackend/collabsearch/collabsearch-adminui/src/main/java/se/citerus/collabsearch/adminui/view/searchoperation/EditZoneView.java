package se.citerus.collabsearch.adminui.view.searchoperation;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import org.apache.commons.lang.Validate;
import org.vaadin.hezamu.googlemapwidget.GoogleMap;
import org.vaadin.hezamu.googlemapwidget.GoogleMap.MapClickListener;
import org.vaadin.hezamu.googlemapwidget.overlay.BasicMarker;
import org.vaadin.hezamu.googlemapwidget.overlay.BasicMarkerSource;
import org.vaadin.hezamu.googlemapwidget.overlay.InfoWindowTab;
import org.vaadin.hezamu.googlemapwidget.overlay.Marker;
import org.vaadin.hezamu.googlemapwidget.overlay.MarkerSource;
import org.vaadin.hezamu.googlemapwidget.overlay.PolyOverlay;
import org.vaadin.hezamu.googlemapwidget.overlay.Polygon;

import se.citerus.collabsearch.adminui.ViewSwitchController;
import se.citerus.collabsearch.adminui.logic.SearchMissionService;
import se.citerus.collabsearch.adminui.logic.SearchOperationService;
import se.citerus.collabsearch.model.SearchFinding;
import se.citerus.collabsearch.model.SearchZone;

import com.google.gwt.requestfactory.shared.Service;
import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;

@SuppressWarnings("serial")
public class EditZoneView extends CustomComponent {
	
	private static final int DEFAULT_ZOOM = 9;
	private VerticalLayout mainLayout;
	private final ViewSwitchController listener;
	private GoogleMap map;
	private String zoneId;
	private String opId;
	private double startingX;
	private double startingY;
	private ZoneViewFragment fragment;

	public EditZoneView(ViewSwitchController listener) {
		this.listener = listener;
		mainLayout = new VerticalLayout();
		setCompositionRoot(mainLayout);
	}

	public void init() {
		mainLayout.setSizeFull();
		
		fragment = new ZoneViewFragment();
		fragment.init("<h1>Redigera zon</h1>");
		fragment.setHeight("10%");
		mainLayout.addComponent(fragment);
		
		fragment.saveButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					SearchOperationService service = new SearchOperationService();
					
					SearchZone zone = new SearchZone();
					zone.setId(zoneId);
					String prioStr = fragment.prioField.getValue().toString();
					zone.setPriority(Integer.parseInt(prioStr));
					zone.setStartingX(startingX);
					zone.setStartingY(startingY);
					Collection<PolyOverlay> overlays = map.getOverlays();
					if (!overlays.isEmpty()) {
						PolyOverlay overlay = overlays.iterator().next();
						zone.setZoneCoords(overlay.getPoints());
					}
					service.editZone(zoneId, zone);
					
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
	}

	public void resetView(String zoneId, String opId) {
		this.zoneId = zoneId;
		if (map == null) {
			initMap();
		}
		
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
			
			startingX = zone.getStartingX();
			startingY = zone.getStartingY();
			map.setCenter(new Double(startingX, startingY));
			map.setZoom(DEFAULT_ZOOM);
			
			//paint the predefined zone
			Double[] points = zone.getZoneCoords();
			if (points.length > 0) {
				map.addPolyOverlay(new Polygon(1L, points, "#000000", 2, 1.0, "#F00C0C", 0.3, true));
			}
			
			//paint the findings
			SearchFinding[] findings = zone.getFindings();
			long genId = 2L;
			for (SearchFinding finding : findings) {
				BasicMarker marker = new BasicMarker(genId, null, finding.getTitle());
				marker.setInfoWindowContent(map, new Label(finding.getDescr()));
				marker.setDraggable(false);
				map.addMarker(marker);
				genId++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		fragment.nameField.setValue("Exempelzon");
		fragment.prioField.setValue("1");
	}

	private void initMap() {
//		mapLayout.removeComponent(map);
		map = makeGoogleMap();
		fragment.mapLayout.addComponent(map);
		
		map.addListener(new MapClickListener() {
			@Override
			public void mapClicked(Double clickPos) {
				System.out.println("(" + clickPos.x + "," + clickPos.y + ")");
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
		
		Point2D.Double itMillOfficeMarker = new Point2D.Double(lat, lon);
		GoogleMap googleMap = new GoogleMap(application, itMillOfficeMarker, 9);
		googleMap.setWidth("800px");
		googleMap.setHeight("600px");
		
//		BasicMarker marker = new BasicMarker(1L, itMillOfficeMarker,
//				"Test marker " + lat + "," + lon);
//		marker.setInfoWindowContent(googleMap, new Label(marker.getTitle()));
//		googleMap.addMarker(marker);
//		
//		BasicMarker marker2 = new BasicMarker(2L, new Point2D.Double(lat+1, lon), "Test marker 2");
//		marker.setInfoWindowContent(googleMap, new Label(marker.getTitle()));
//		googleMap.addMarker(marker2);
		
//		Double[] points = new Double[]{
//			new Double(22.376060485839844,60.45886826784022),
//			new Double(22.405071258544922,60.46001085184192),
//			new Double(22.396144866943360,60.44672053773407),
//			new Double(22.376060485839844,60.45886826784022)
//		};
//		Polygon poly3 = new Polygon(4L, points, "#000000", 2, 1.0, "#F00C0C", 0.3, true);
//		poly3.setClickable(true);
//		googleMap.addPolyOverlay(poly3);
		
//		PolyOverlay po = new PolyOverlay(5L, points, "#000000", 2, 1.0, true);
//		po.setClickable(true);
//		googleMap.addPolyOverlay(po);
		
//		googleMap.removeOverlay(new PolyOverlay(5L, null));
		
//		googleMap.removeOverlay(poly3);
		
		return googleMap;
	}
}
