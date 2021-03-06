package se.citerus.collabsearch.adminui.view.searchoperation;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.vol.Area;
import org.vaadin.vol.Bounds;
import org.vaadin.vol.OpenLayersMap;
import org.vaadin.vol.OpenLayersMap.ExtentChangeEvent;
import org.vaadin.vol.OpenLayersMap.ExtentChangeListener;
import org.vaadin.vol.OpenLayersMap.MapClickEvent;
import org.vaadin.vol.OpenLayersMap.MapClickListener;
import org.vaadin.vol.GoogleTerrainMapLayer;
import org.vaadin.vol.Point;
import org.vaadin.vol.PointInformation;
import org.vaadin.vol.PointVector;
import org.vaadin.vol.VectorLayer;

import se.citerus.collabsearch.adminui.logic.SearchOperationService;
import se.citerus.collabsearch.adminui.view.ViewSwitchController;
import se.citerus.collabsearch.model.SearchGroup;
import se.citerus.collabsearch.model.exceptions.SearchGroupNotFoundException;

import com.vaadin.data.Item;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Configurable(preConstruction=true)
public class NewZoneView extends CustomComponent {
	
	//XXX ALL OL-POINTS USE LON,LAT !!!

	private static final double DEFAULT_LON = 15.11718750000000;
	private static final double DEFAULT_LAT = 62.30879369102805;
	private static final int DEFAULT_ZOOM = 5;
	
	private final ViewSwitchController listener;
	private VerticalLayout mainLayout;
	private OpenLayersMap map;
	private String opId;
	private ZoneViewFragment fragment;
	
	@Autowired
	private SearchOperationService service;
	
	private VectorLayer pointLayer;
	private VectorLayer zoneLayer;

	public NewZoneView(ViewSwitchController listener) {
		this.listener = listener;
		mainLayout = new VerticalLayout();
		setCompositionRoot(mainLayout);
	}

	public void init() {
		listener.setMainWindowCaption("Missing People - Sökområde");
		
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false, true, false, true);
		
		fragment = new ZoneViewFragment();
		fragment.init("Skapa ny zon");
		mainLayout.addComponent(fragment);
		
		fragment.saveButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				saveZone();
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
				pointLayer.removeAllComponents();
				zoneLayer.removeAllComponents();
			}
		});
		
		fragment.createZoneButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				convertPointsToPolygon();
			}
		});
	}

	public void resetView(String opId) {
		this.opId = opId;
		if (map == null) {
			initMap();
		}
		
		pointLayer.removeAllComponents();
		zoneLayer.removeAllComponents();
		
		try {
			List<SearchGroup> groupList = service.getSearchGroupsByOp(opId);
			fragment.setupGroupComboBox(groupList);
		} catch (SearchGroupNotFoundException e) {
			//no groups created for this op, this is acceptable.
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		fragment.nameField.setValue("");
		fragment.prioField.setValue("");
		fragment.assignedGroupDropdown.select(
				fragment.assignedGroupDropdown.getNullSelectionItemId());
		
		map.setCenter(DEFAULT_LON, DEFAULT_LAT);
		map.setZoom(DEFAULT_ZOOM);
	}

	private void initMap() {
		map = setupMap();
		fragment.setMap(map);
		
		pointLayer = new VectorLayer();
		map.addLayer(pointLayer);
		
		zoneLayer = new VectorLayer();
		map.addLayer(zoneLayer);
		
		map.addListener(new MapClickListener() {
			@Override
			public void mapClicked(MapClickEvent event) {
				try {
					PointInformation clickPos = event.getPointInfo();
					PointVector marker = new PointVector(clickPos.getLon(), clickPos.getLat());
					pointLayer.addVector(marker);
					System.out.println("Created PointVector at (" + clickPos.getLon() + "," + clickPos.getLat() + ")");
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
		});
		
		map.addListener(new ExtentChangeListener() {
			@Override
			public void extentChanged(ExtentChangeEvent event) {
				int zoom = map.getZoom();
				if (zoom == 16) { //zoom level 16 breaks projection
					map.setZoom(15);
				}
			}
		});
	}
	
	private void saveZone() {
		try {
			String title = fragment.nameField.getValue().toString();
			String prioStr = fragment.prioField.getValue().toString();
			
			//convert zone polygon to coordinate points
			Point2D.Double[] points = convertPolygonToPoints();
			
			Point2D.Double center;
			center = (points.length > 0) ? getMapCenter() : points[0];
			
			String groupId = null;
			Object checkboxSelection = fragment.assignedGroupDropdown.getValue();
			if (checkboxSelection != null) {
				Item item = fragment.assignedGroupDropdown.getItem(checkboxSelection);
				groupId = item.getItemProperty("id").getValue().toString();
			}
			
			service.createZone(opId, title, prioStr, points, map.getZoom(), center, groupId);
			
			listener.switchToSearchMissionListView();
		} catch (Exception e) {
			e.printStackTrace();
			listener.displayError("Fel", "Ett fel uppstod vid " +
					"kommunikationen med servern, zonen har ej sparats.");
		}
	}
	
	private Point2D.Double[] convertPolygonToPoints() {
		Iterator<Component> it = zoneLayer.getComponentIterator();
		int count = 0;
		while (it.hasNext()) {
			it.next();
			count++;
		}
		
		if (count == 0) {
			return null;
		}
		
		it = zoneLayer.getComponentIterator();
		if (!it.hasNext()) {
			return null;
		}
		Area area = (Area) it.next();
		Point[] points = area.getPoints();
		Point2D.Double[] outputPoints = new Double[points.length]; 
		for (int i = 0; i < points.length; i++) {
			outputPoints[i] = new Double(points[i].getLon(), 
					points[i].getLat());
		}
		
		return outputPoints;
	}
	
	private void convertPointsToPolygon() {
		int numberOfPoints = 0;
		Iterator<Component> it = pointLayer.getComponentIterator();
		while (it.hasNext()) {
			it.next();
			numberOfPoints++;
		}
		
		if (numberOfPoints < 3) {
			listener.displayError("Fel", "Zoner måste ha minst 3 punkter");
			return;
		}
		
		Point[] points = new Point[numberOfPoints];
		it = pointLayer.getComponentIterator();
		int i = 0;
		while (it.hasNext()) {
			PointVector pv = (PointVector) it.next();
			Point point = pv.getPoint();
			points[i++] = new Point(point.getLon(), point.getLat());
		}
		
		Area area = new Area();
		area.setPoints(points);
//		zoneLayer.addVector(new Area(generateId(), points, 
//				"#000000", 2, 1.0, "#F00C0C", 0.3, true));
		zoneLayer.addVector(area);
		
		pointLayer.removeAllComponents();
	}
	
	private Point2D.Double getMapCenter() {
		//get map bounds, divide by map width/height
		Bounds bounds = map.getExtend();
		double yLen = bounds.getTop() - bounds.getBottom();
		double xLen = bounds.getRight() - bounds.getLeft();
		double yMidLen = yLen/2.0d;
		double xMidLen = xLen/2.0d;
		double yMid = bounds.getBottom() + yMidLen;
		double xMid = bounds.getLeft() + xMidLen;
		System.out.println("Using coords (" + xMid + "," + yMid + ") for map center");
		return new Double(xMid, yMid);
	}
	
	private OpenLayersMap setupMap() {
		OpenLayersMap map = new OpenLayersMap();
		map.setCenter(DEFAULT_LON, DEFAULT_LAT);
		map.setZoom(DEFAULT_ZOOM);
		map.setWidth("100%");
		map.setHeight("500px");
		
		GoogleTerrainMapLayer terrainLayer = new GoogleTerrainMapLayer();
		map.addLayer(terrainLayer);
		
		return map;
	}

}
