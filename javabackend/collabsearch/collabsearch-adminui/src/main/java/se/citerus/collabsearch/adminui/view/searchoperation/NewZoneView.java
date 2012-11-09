package se.citerus.collabsearch.adminui.view.searchoperation;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
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
import se.citerus.collabsearch.model.SearchZone;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Configurable(preConstruction=true)
public class NewZoneView extends CustomComponent {

	private static final int DEFAULT_ZOOM = 5;
	private final ViewSwitchController listener;
	private VerticalLayout mainLayout;
	private GoogleMap map;
	private String opId;
	private ZoneViewFragment fragment;
	
	@Autowired
	private SearchOperationService service;
	
	private List<Marker> markerPoints;
	private Random random;
	private Double mapCenter;
	private int mapZoom;

	public NewZoneView(ViewSwitchController listener) {
		this.listener = listener;
		mainLayout = new VerticalLayout();
		setCompositionRoot(mainLayout);
	}

	public void init() {
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false, true, false, true);
		
		fragment = new ZoneViewFragment();
		fragment.init("Skapa ny zon");
		mainLayout.addComponent(fragment);
		
		markerPoints = new ArrayList<Marker>();
		
		fragment.saveButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try {
//					SearchOperationService service = new SearchOperationService();
					String title = fragment.nameField.getValue().toString();
					String prioStr = fragment.prioField.getValue().toString();
					Double[] points = null;
					Collection<PolyOverlay> overlays = map.getOverlays();
					if (!overlays.isEmpty()) {
						PolyOverlay overlay = overlays.iterator().next();
						points = overlay.getPoints();
					}
					service.createZone(opId, title, prioStr, points, map.getZoom());
					
					listener.switchToSearchMissionListView();
				} catch (Exception e) {
					e.printStackTrace();
					listener.displayError("Fel", "Ett fel uppstod vid " +
							"kommunikationen med servern, zonen har ej sparats.");
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
				for (PolyOverlay overlay : map.getOverlays()) {
					map.removeOverlay(overlay);
				}
			}
		});
		
		fragment.createZoneButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
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

	public void resetView(String opId) {
		this.opId = opId;
		if (map == null) {
			initMap();
		}
		
		map.removeAllMarkers();
		Collection<PolyOverlay> overlays = map.getOverlays();
		for (PolyOverlay overlay : overlays) {
			map.removeOverlay(overlay);
		}
	}

	private void initMap() {
		map = makeGoogleMap();
		fragment.setMap(map);
		
		map.addListener(new MapClickListener() {
			@Override
			public void mapClicked(Double clickPos) {
//				System.out.println("(" + clickPos.x + "," + clickPos.y + ")");
				BasicMarker marker = new BasicMarker(generateId(), 
						clickPos, "" + markerPoints.size());
				map.addMarker(marker);
				markerPoints.add(marker);
			}
		});
	}
	
	private Long generateId() {
		if (random == null) {
			random = new Random();
		}
		return random.nextLong();
	}

	private GoogleMap makeGoogleMap() {
		Application application = getApplication();
		Validate.notNull(application);
		
		//example coords for middle of Sweden
		final double lat = 15.11718750000000;
		final double lon = 62.30879369102805;
		
		Point2D.Double mapCenterMarker = new Point2D.Double(lat, lon);
		GoogleMap googleMap = new GoogleMap(application, mapCenterMarker, DEFAULT_ZOOM);
		googleMap.setWidth("100%");
		googleMap.setHeight("500px");
		
		return googleMap;
	}

}
