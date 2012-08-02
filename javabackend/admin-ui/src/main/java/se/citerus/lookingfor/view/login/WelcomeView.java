package se.citerus.lookingfor.view.login;

import se.citerus.lookingfor.ViewSwitchListener;

import com.vaadin.terminal.gwt.client.ui.AlignmentInfo.Bits;
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
//		mainLayout.setWidth("300px");
//		mainLayout.setHeight("300px");
		mainLayout.setSizeFull();
		
		VerticalLayout outerLayout = new VerticalLayout();
		outerLayout.setWidth("100%");
		
		Label loginSuccessLabel = new Label("Inloggningen lyckades!");
		outerLayout.addComponent(loginSuccessLabel);
		outerLayout.setComponentAlignment(loginSuccessLabel, Alignment.TOP_CENTER);
//		outerLayout.setExpandRatio(loginSuccessLabel, 1f);
		
		HorizontalLayout innerLayout = new HorizontalLayout();
		innerLayout.setSpacing(true);
		userButton = new Button("Användare");
		logoutButton = new Button("Logga ut");
		
		searchMissionButton = new Button("Sökuppdrag");
		innerLayout.addComponent(userButton);
		innerLayout.addComponent(logoutButton);
		innerLayout.addComponent(searchMissionButton);
		outerLayout.addComponent(innerLayout);
		outerLayout.setComponentAlignment(innerLayout, Alignment.TOP_CENTER);
		
		mainLayout.addComponent(outerLayout);
		mainLayout.setComponentAlignment(outerLayout, Alignment.TOP_CENTER);
		mainLayout.setExpandRatio(outerLayout, UNITS_PERCENTAGE);
		mainLayout.setMargin(true);
		mainLayout.setExpandRatio(outerLayout, 1f);
	}
}
