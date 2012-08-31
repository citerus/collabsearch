package se.citerus.collabsearch.adminui;

import se.citerus.collabsearch.adminui.logic.Authenticator;
import se.citerus.collabsearch.adminui.view.login.LoginView;
import se.citerus.collabsearch.adminui.view.login.WelcomeView;
import se.citerus.collabsearch.adminui.view.searchmission.FileManagementView;
import se.citerus.collabsearch.adminui.view.searchmission.SearchMissionEditView;
import se.citerus.collabsearch.adminui.view.searchmission.SearchMissionListView;
import se.citerus.collabsearch.adminui.view.searchoperation.GroupEditView;
import se.citerus.collabsearch.adminui.view.searchoperation.SearchOperationEditView;
import se.citerus.collabsearch.adminui.view.searchoperation.ZoneEditView;
import se.citerus.collabsearch.adminui.view.usermgmt.UserEditView;
import se.citerus.collabsearch.adminui.view.usermgmt.UserListView;

import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.LoginForm.LoginListener;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class MainWindow extends Window implements LoginListener, ViewSwitchController {

	private static final boolean debugMode = false;
	
	private LoginView loginView;
	private WelcomeView welcomeView;
	private UserListView userListView;
	private UserEditView userEditView;
	private SearchMissionListView searchMissionListView;
	private SearchMissionEditView searchMissionEditView;
	private SearchOperationEditView searchOperationEditView;
	private FileManagementView fileMgmtView;
	private GroupEditView groupEditView;
	private ZoneEditView zoneEditView;

	public MainWindow() {
		setCaption("Collaborative Search - Inloggning");
	}
	
	public void initWindow() {
		if (LookingForApp.getInstance().getUser() == null) {
			switchToLoginView();
		} else {
			switchToWelcomeView();
		}
	}

	@Override
	public void onLogin(LoginEvent event) {
		try {
			Authenticator authenticator = new Authenticator();
			if (authenticator.login(event.getLoginParameter("username"), 
	        		event.getLoginParameter("password").toCharArray())) {
				LookingForApp.getInstance().setUser(event.getLoginParameter("username"));
	        	switchToWelcomeView();
	        } else {
	        	displayNotification("Inloggning misslyckades", "Fel användarnamn eller lösenord");
	        }
		} catch (Exception e) {
			displayError("Fel", e.getMessage());
		}
	}

	@Override
	public void displayNotification(String caption, String message) {
		showNotification(caption, message);
	}

	@Override
	public void logoutAndReload() {
		LookingForApp.getInstance().setUser(null);
		getApplication().close();
	}

	@Override
	public void displayError(String caption, String message) {
		showNotification(caption, message, Notification.TYPE_ERROR_MESSAGE);
	}

	@Override
	public void setMainWindowCaption(String caption) {
		setCaption(caption);
	}

	@Override
	public void switchToLoginView() {
		if (loginView == null) {
			loginView = new LoginView(this);
		}
		setContent(loginView);
	}

	@Override
	public void switchToWelcomeView() {
		if (welcomeView == null) {
			welcomeView = new WelcomeView(this);
		}
		setContent(welcomeView);
	}

	@Override
	public void switchToUserListView() {
		if (userListView == null) {
			userListView = new UserListView(this);
		}
		userListView.resetView();
		setContent(userListView);
	}
	
	@Override
	public void switchToUserEditView(String selectedUsername) {
		if (userEditView == null) {
			userEditView = new UserEditView(this);
		}
		userEditView.resetView(selectedUsername);
		setContent(userEditView);
	}

	@Override
	public void switchToSearchMissionListView() {
		if (searchMissionListView == null) {
			searchMissionListView = new SearchMissionListView(this);
			searchMissionListView.init();
		}
		searchMissionListView.resetView();
		setContent(searchMissionListView);
	}

	@Override
	public void switchToSearchMissionEditView(String selectedSearchMissionName) {
		if (searchMissionEditView == null) {
			searchMissionEditView = new SearchMissionEditView(this);
			searchMissionEditView.init();
		}
		searchMissionEditView.resetView(selectedSearchMissionName);
		setContent(searchMissionEditView);
	}

	@Override
	public void switchToSearchOperationEditView(String opName, String missionName) {
		if (searchOperationEditView == null) {
			searchOperationEditView = new SearchOperationEditView(this);
			searchOperationEditView.init();
		}
		searchOperationEditView.resetView(opName, missionName);
		setContent(searchOperationEditView);
	}

	@Override
	public void switchToGroupEditView(String groupId, String opName, String missionName) {
		if (groupEditView == null) {
			groupEditView = new GroupEditView(this);
			groupEditView.init();
		}
		groupEditView.resetView(groupId, opName, missionName);
		setContent(groupEditView);
	}

	@Override
	public void refreshOpsTable() {
		searchMissionEditView.refreshOpsTable();
	}

	@Override
	public void switchToFileManagementView(String missionName, String fileName) {
		if (fileMgmtView == null) {
			fileMgmtView = new FileManagementView(this);
			fileMgmtView.init();
		}
		fileMgmtView.resetView(missionName, fileName);
		setContent(fileMgmtView);
	}

	@Override
	public void refreshMissionTable() {
		searchMissionListView.refreshMissionTree();
	}

	@Override
	public void switchToZoneEditView(String zoneId, String opName) {
		if (zoneEditView == null) {
			zoneEditView = new ZoneEditView(this);
			zoneEditView.init();
		}
		zoneEditView.resetView(zoneId, opName);
		setContent(zoneEditView);
	}

}
