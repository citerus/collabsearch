package se.citerus.collabsearch.adminui.view.searchoperation;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.Random;

import org.apache.commons.lang.Validate;
import org.vaadin.hezamu.googlemapwidget.GoogleMap;
import org.vaadin.hezamu.googlemapwidget.GoogleMap.MapClickListener;
import org.vaadin.hezamu.googlemapwidget.overlay.BasicMarker;
import org.vaadin.hezamu.googlemapwidget.overlay.Polygon;

import se.citerus.collabsearch.adminui.ViewSwitchController;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class ZoneEditView extends CustomComponent {
	
	private Random r = new Random();
	private VerticalLayout mainLayout;
	private final ViewSwitchController listener;
	private Application appWorkaround;
	private VerticalLayout mapLayout;
	private GoogleMap map;

	public ZoneEditView(ViewSwitchController listener) {
		this.listener = listener;
		mainLayout = new VerticalLayout();
		setCompositionRoot(mainLayout);
	}

	public void init() {
		mainLayout.setSizeFull();
		
		Label label = new Label("Hello Vaadin user");
		mainLayout.addComponent(label);
		
		mapLayout = new VerticalLayout();
		mapLayout.setSizeFull();
		mainLayout.addComponent(mapLayout);
		
		Button backButton = new Button("Tillbaka");
		backButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listener.switchToSearchMissionListView();
			}
		});
		mainLayout.addComponent(backButton);
	}

	public void resetView(String zoneId, String opName) {
		//mapLayout.removeComponent(googleMap);
		
		map = makeGoogleMap();
		mapLayout.addComponent(map);
		
		map.addListener(new MapClickListener() {
			@Override
			public void mapClicked(Double clickPos) {
				System.out.println("(" + clickPos.x + "," + clickPos.y + ")");
			}
		});
//		googleMap.addListener(markerClickListener);
	}

	private GoogleMap makeGoogleMap() {
		Application application = getApplication();
		Validate.notNull(application);
//		Validate.notNull(appWorkaround);
		
		final double lat = 22.3;
		final double lon = 60.4522;
		Point2D.Double itMillOfficeMarker = new Point2D.Double(lat, lon);
		GoogleMap googleMap = new GoogleMap(application, itMillOfficeMarker, 9);
		googleMap.setWidth("800px");
		googleMap.setHeight("600px");
		
		BasicMarker marker = new BasicMarker(1L, itMillOfficeMarker,
				"Test marker " + lat + "," + lon);
		marker.setInfoWindowContent(googleMap, new Label(marker.getTitle()));
		googleMap.addMarker(marker);
		
		BasicMarker marker2 = new BasicMarker(2L, new Point2D.Double(lat+1, lon), "Test marker 2");
		marker.setInfoWindowContent(googleMap, new Label(marker.getTitle()));
		googleMap.addMarker(marker2);
		
		Double[] points = new Double[]{
			new Double(22.376060485839844,60.45886826784022),
			new Double(22.405071258544922,60.46001085184192),
			new Double(22.396144866943360,60.44672053773407),
			new Double(22.376060485839844,60.45886826784022)
		};
		Polygon poly3 = new Polygon(4L, points, "#000000", 2, 1.0, "#F00C0C", 0.3, true);
		googleMap.addPolyOverlay(poly3);
		
//		PolyOverlay po = new PolyOverlay(5L, points, "#000000", 2, 1.0, true);
//		googleMap.addPolyOverlay(po);
//		po.setClickable(true);
		
//		googleMap.removeOverlay(new PolyOverlay(5L, null));
		
		return googleMap;
	}

//	@Override
//	public void attach() {
//		super.attach();
//		appWorkaround = getApplication();
//	}
}
