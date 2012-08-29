package se.citerus.collabsearch.adminui.view.searchmission;

import se.citerus.collabsearch.adminui.ViewSwitchController;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

public class FileManagementView extends CustomComponent {
	private final ViewSwitchController listener;
	private VerticalLayout mainLayout;

	public FileManagementView(final ViewSwitchController listener) {
		this.listener = listener;
		mainLayout = new VerticalLayout();
		setCompositionRoot(mainLayout);
	}
	
	public void init() {
		
	}
	
	public void resetView() {
		
	}
}
