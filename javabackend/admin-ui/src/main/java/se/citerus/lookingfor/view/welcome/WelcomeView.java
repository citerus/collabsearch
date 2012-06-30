package se.citerus.lookingfor.view.welcome;

import se.citerus.lookingfor.ViewSwitchListener;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class WelcomeView extends CustomComponent {
	
	private VerticalLayout mainLayout;
	private Button userButton;
	private Button logoutButton;
	private Button searchMissionButton;

	public WelcomeView(final ViewSwitchListener listener) {
		buildMainLayout();
		setCompositionRoot(mainLayout);
		listener.setMainWindowCaption("Missing People - Välkommen");
		
		userButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				listener.switchToUserListView();
			}
		});
		logoutButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				listener.logoutAndReload();
			}
		});
		searchMissionButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				listener.switchToSearchMissionView();
			}
		});
	}

	private void buildMainLayout() {
		mainLayout = new VerticalLayout();
		Label loginSuccessLabel = new Label("Inloggningen lyckades");
		mainLayout.addComponent(loginSuccessLabel);
		mainLayout.setComponentAlignment(loginSuccessLabel, Alignment.MIDDLE_CENTER);
		HorizontalLayout subLayout = new HorizontalLayout();
		subLayout.setSpacing(true);
		userButton = new Button("Användare");
		logoutButton = new Button("Logga ut");
		
		searchMissionButton = new Button("Sökuppdrag");
		subLayout.addComponent(userButton);
		subLayout.addComponent(logoutButton);
		subLayout.addComponent(searchMissionButton);
		mainLayout.addComponent(subLayout);
	}
}
