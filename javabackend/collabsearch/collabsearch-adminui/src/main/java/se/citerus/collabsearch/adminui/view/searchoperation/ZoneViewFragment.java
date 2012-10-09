package se.citerus.collabsearch.adminui.view.searchoperation;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * The ZoneViewFragment is used by both the NewZoneView and EditZoneView, 
 * as the two views share the same layout.
 * @author Ola Rende
 */
@SuppressWarnings("serial")
public class ZoneViewFragment extends CustomComponent {
	
	private VerticalLayout mainLayout;
	protected VerticalLayout mapLayout;
	protected TextField nameField;
	protected TextField prioField;
	protected Button saveButton;
	protected Button backButton;
	protected Label headerLabel;

	protected ZoneViewFragment() {
		mainLayout = new VerticalLayout();
		setCompositionRoot(mainLayout);
	}
	
	protected void init(String headerString) {
		mainLayout.setSizeFull(); //unnecessary?
		
		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setWidth("100%");
		
		headerLabel = new Label(headerString);
		headerLabel.setContentMode(Label.CONTENT_XHTML);
		topLayout.addComponent(headerLabel);
		
		backButton = new Button("Tillbaka");
		topLayout.addComponent(backButton);
		
		mainLayout.addComponent(topLayout);
		
		mapLayout = new VerticalLayout();
		mapLayout.setSizeFull();
		mainLayout.addComponent(mapLayout);
		
		HorizontalLayout textFieldLayout = new HorizontalLayout();
		textFieldLayout.setWidth("100%");
		
		nameField = new TextField("Titel");
		textFieldLayout.addComponent(nameField);
		
		prioField = new TextField("Prioritet");
		textFieldLayout.addComponent(prioField);
		
		mainLayout.addComponent(textFieldLayout);
		
		HorizontalLayout bottomLayout = new HorizontalLayout();
		bottomLayout.setWidth("100%");
		
		saveButton = new Button("Spara");
		bottomLayout.addComponent(saveButton);
	}
}
