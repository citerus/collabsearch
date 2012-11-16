package se.citerus.collabsearch.adminui.view.login;

import static com.vaadin.ui.Alignment.MIDDLE_CENTER;
import se.citerus.collabsearch.adminui.ViewSwitchController;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.client.ui.AlignmentInfo.Bits;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class WelcomeView extends CustomComponent {
	
	private VerticalLayout mainLayout;
	private Button userButton;
	private Button logoutButton;
	private Button searchMissionButton;
	private final ViewSwitchController listener;

	public WelcomeView(final ViewSwitchController listener) {
		this.listener = listener;
		mainLayout = new VerticalLayout();
		setCompositionRoot(mainLayout);
	}
	
	public void init() {
		buildMainLayout();
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
//		mainLayout.setWidth("300px");
//		mainLayout.setHeight("300px");
		mainLayout.setSizeFull();
		mainLayout.setMargin(true);
		
		Label sizer1 = new Label("");
		sizer1.setHeight("100%");
		mainLayout.addComponent(sizer1);
		
		VerticalLayout outerLayout = new VerticalLayout();
		outerLayout.setWidth("100%");
		outerLayout.setHeight("100%");
		outerLayout.setSpacing(true);
		outerLayout.setMargin(false, false, true, false);
		
		Embedded embImg = new Embedded("", 
			new ThemeResource("../mytheme/dual_color_extended_trans.png"));
		outerLayout.addComponent(embImg);
		outerLayout.setComponentAlignment(embImg, MIDDLE_CENTER);
		mainLayout.addComponent(outerLayout);
		mainLayout.setComponentAlignment(outerLayout, MIDDLE_CENTER);
		
		Panel welcomePanel = new Panel();
		welcomePanel.setWidth("33%");
		welcomePanel.setStyleName("welcome-panel");
		
		VerticalLayout innerLayout = new VerticalLayout();
		welcomePanel.addComponent(innerLayout);
		
		Label loginSuccessLabel = new Label("Inloggningen lyckades!");
		loginSuccessLabel.setWidth(null);
		innerLayout.addComponent(loginSuccessLabel);
		innerLayout.setComponentAlignment(loginSuccessLabel, MIDDLE_CENTER);
		
		HorizontalLayout innerHorizontalLayout = new HorizontalLayout();
		innerHorizontalLayout.setSpacing(true);
		userButton = new Button("Användare");
		logoutButton = new Button("Logga ut");
		
		searchMissionButton = new Button("Sökuppdrag");
		innerHorizontalLayout.addComponent(userButton);
		innerHorizontalLayout.addComponent(logoutButton);
		innerHorizontalLayout.addComponent(searchMissionButton);
		
		innerLayout.addComponent(innerHorizontalLayout);
		innerLayout.setComponentAlignment(innerHorizontalLayout, MIDDLE_CENTER);
		
		mainLayout.addComponent(welcomePanel);
		mainLayout.setComponentAlignment(welcomePanel, MIDDLE_CENTER);
		
		Label sizer2 = new Label("");
		sizer2.setWidth("100%");
		mainLayout.addComponent(sizer2);
		
		mainLayout.setExpandRatio(sizer1, 0.5f);
		mainLayout.setExpandRatio(outerLayout, 2f);
		mainLayout.setExpandRatio(sizer2, 0.5f);
	}

	public void resetView() {
	}
}
