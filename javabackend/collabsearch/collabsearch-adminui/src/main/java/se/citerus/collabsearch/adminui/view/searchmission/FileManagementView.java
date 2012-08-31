package se.citerus.collabsearch.adminui.view.searchmission;

import se.citerus.collabsearch.adminui.ViewSwitchController;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
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
		listener.setMainWindowCaption("Collaborative Search - Filhantering");
		
		mainLayout.addComponent(new Label("WIP"));
	}
	
	public void resetView(String missionName, String fileName) {
		if (missionName == null) {
			listener.displayError("Inget sökuppdrag funnet", "Inget sökuppdragsnamn funnet vid filhantering.");
			listener.switchToSearchMissionListView();
		}
		if (fileName == null) { //add file mode
			
		} else { //delete file mode
			
		}
	}
}
