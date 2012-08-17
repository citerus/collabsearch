package se.citerus.collabsearch.adminui.view.login;

import se.citerus.collabsearch.adminui.ViewSwitchController;

import com.vaadin.terminal.gwt.client.ui.AlignmentInfo.Bits;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class WelcomeView extends CustomComponent {
	
	private VerticalLayout mainLayout;
	private Button userButton;
	private Button logoutButton;
	private Button searchMissionButton;

	public WelcomeView(final ViewSwitchController listener) {
		buildMainLayout();
		setCompositionRoot(mainLayout);
		listener.setMainWindowCaption("Välkommen");
		
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
				listener.switchToSearchMissionListView();
			}
		});
	}

	private void buildMainLayout() {
		mainLayout = new VerticalLayout();
//		mainLayout.setWidth("300px");
//		mainLayout.setHeight("300px");
		mainLayout.setSizeFull();
		
		Label sizer1 = new Label("");
		sizer1.setHeight("100%");
		mainLayout.addComponent(sizer1);
		
		VerticalLayout outerLayout = new VerticalLayout();
		outerLayout.setWidth("100%");
		outerLayout.setHeight("100%");
		outerLayout.setSpacing(true);
		
		Label loginSuccessLabel = new Label("Inloggningen lyckades!");
		loginSuccessLabel.setWidth(null);
		outerLayout.addComponent(loginSuccessLabel);
		outerLayout.setComponentAlignment(loginSuccessLabel, Alignment.MIDDLE_CENTER);
		
		HorizontalLayout innerLayout = new HorizontalLayout();
		innerLayout.setSpacing(true);
		userButton = new Button("Användare");
		logoutButton = new Button("Logga ut");
		
		searchMissionButton = new Button("Sökuppdrag");
		innerLayout.addComponent(userButton);
		innerLayout.addComponent(logoutButton);
		innerLayout.addComponent(searchMissionButton);
		outerLayout.addComponent(innerLayout);
		outerLayout.setComponentAlignment(innerLayout, Alignment.MIDDLE_CENTER);
		
		mainLayout.addComponent(outerLayout);
		mainLayout.setComponentAlignment(outerLayout, Alignment.MIDDLE_CENTER);
		mainLayout.setExpandRatio(outerLayout, UNITS_PERCENTAGE);
		mainLayout.setMargin(true);
		
		Label sizer2 = new Label("");
		sizer2.setWidth("100%");
		mainLayout.addComponent(sizer2);
		
		mainLayout.setExpandRatio(sizer1, 0.5f);
		mainLayout.setExpandRatio(outerLayout, 2f);
		mainLayout.setExpandRatio(sizer2, 0.5f);
	}
}
