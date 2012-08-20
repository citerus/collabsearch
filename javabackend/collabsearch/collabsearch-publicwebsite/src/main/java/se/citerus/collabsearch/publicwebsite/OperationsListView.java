package se.citerus.collabsearch.publicwebsite;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class OperationsListView extends CustomComponent {

	private VerticalLayout mainLayout;
	private VerticalLayout listLayout;
	private Button addButton;
	private Button removeButton;

	public OperationsListView() {
		buildMainLayout();
		setCompositionRoot(mainLayout);
		
		addButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				listLayout.addComponent(buildListRowComponent("Sökuppdrag 1", "Beskrivning..."));
//				System.out.println("Button clicked");
			}
		});
	}
	
	private void buildMainLayout() {
		mainLayout = new VerticalLayout();
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);
		
		addButton = new Button("Add stuff to layout");
		mainLayout.addComponent(addButton);
		
		removeButton = new Button("Remove stuff from layout");
		mainLayout.addComponent(removeButton);
		
		final Panel listPanel = new Panel("Sökuppdrag");
		listPanel.setHeight("100%");
		listPanel.setWidth("50%");
		listLayout = new VerticalLayout();
		listLayout.setSpacing(true);
		listPanel.setContent(listLayout);
		mainLayout.addComponent(listPanel);
	}

	public void resetView() {
		//requery db for searchops list
	}
	
	private Component buildListRowComponent(String header, String descr) {
		Panel panel = new Panel();
		panel.setWidth("100%");
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth("100%");
		layout.setSpacing(true);
		Label headerLabel = new Label("<h2><b>" + header + "</b></h2>");
		headerLabel.setContentMode(Label.CONTENT_XHTML);
		layout.addComponent(headerLabel);
		Label descrLabel = new Label(descr);
		layout.addComponent(descrLabel);
		Button readMoreButton = new Button("Läs mer");
		layout.addComponent(readMoreButton);
		layout.setComponentAlignment(readMoreButton, Alignment.BOTTOM_RIGHT);
		panel.setContent(layout);
		return panel;
	}

}
