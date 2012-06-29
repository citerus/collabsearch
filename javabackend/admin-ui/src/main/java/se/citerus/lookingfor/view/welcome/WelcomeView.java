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
	
	public WelcomeView(final ViewSwitchListener listener) {
		setCaption("Välkommen!");
		VerticalLayout layout = new VerticalLayout();
		Label loginSuccessLabel = new Label("Inloggningen lyckades");
		layout.addComponent(loginSuccessLabel);
		layout.setComponentAlignment(loginSuccessLabel, Alignment.MIDDLE_CENTER);
		HorizontalLayout subLayout = new HorizontalLayout();
		subLayout.setSpacing(true);
		Button userButton = new Button("Användare");
		userButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				listener.switchToUserListView();
			}
		});
		Button logoutButton = new Button("Logga ut");
		
		logoutButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				listener.logoutAndReload();
			}
		});
		Button searchMissionButton = new Button("Sökuppdrag");
		searchMissionButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				listener.switchToSearchMissionView();
			}
		});
		subLayout.addComponent(userButton);
		subLayout.addComponent(logoutButton);
		subLayout.addComponent(searchMissionButton);
		layout.addComponent(subLayout);
		setCompositionRoot(layout);
	}
}
