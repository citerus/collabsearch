package se.citerus.collabsearch.adminui.view.searchoperation;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.Collection;

import org.apache.commons.lang.Validate;
import org.vaadin.hezamu.googlemapwidget.GoogleMap;
import org.vaadin.hezamu.googlemapwidget.GoogleMap.MapClickListener;
import org.vaadin.hezamu.googlemapwidget.overlay.Marker;
import org.vaadin.hezamu.googlemapwidget.overlay.PolyOverlay;

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
public class NewZoneView extends CustomComponent { //TODO implement class

	private final ViewSwitchController listener;
	private VerticalLayout mainLayout;
	private VerticalLayout mapLayout;
	private GoogleMap map;
	private String opId;
	private double startingX;
	private double startingY;
	private ZoneViewFragment fragment;

	public NewZoneView(ViewSwitchController listener) {
		this.listener = listener;
		mainLayout = new VerticalLayout();
		setCompositionRoot(mainLayout);
	}

	public void init() {
		mainLayout.setSizeFull();
		
		fragment = new ZoneViewFragment();
		fragment.init("<h1>Skapa ny zon</h1>");
		mainLayout.addComponent(fragment);
		
		fragment.saveButton.addListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				SearchOperationService service = new SearchOperationService();
				
				SearchZone zone = new SearchZone();
				String prioStr = fragment.prioField.getValue().toString();
				zone.setPriority(Integer.parseInt(prioStr));
				zone.setStartingX(startingX);
				zone.setStartingY(startingY);
				Collection<PolyOverlay> overlays = map.getOverlays();
				if (!overlays.isEmpty()) {
					PolyOverlay overlay = overlays.iterator().next();
					zone.setZoneCoords(overlay.getPoints());
				}
				service.createZone(opId, zone);
			}
		});
		
		fragment.backButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listener.switchToSearchMissionListView();
			}
		});
	}

	public void resetView(String opId) {
		this.opId = opId;
		if (map == null) {
			initMap();
		}
	}
	
	private void initMap() {
//		mapLayout.removeComponent(map);
		map = makeGoogleMap();
		mapLayout.addComponent(map);
		
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
		
		final double lat = 22.3; //TODO replace with default view centered on Sweden
		final double lon = 60.4522;
		Point2D.Double itMillOfficeMarker = new Point2D.Double(lat, lon);
		GoogleMap googleMap = new GoogleMap(application, itMillOfficeMarker, 9);
		googleMap.setWidth("800px");
		googleMap.setHeight("600px");
		
		return googleMap;
	}

}
