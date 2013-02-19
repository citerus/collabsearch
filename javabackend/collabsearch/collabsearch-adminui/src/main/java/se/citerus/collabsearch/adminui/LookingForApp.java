package se.citerus.collabsearch.adminui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import se.citerus.collabsearch.adminui.view.MainWindow;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.Window;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
@Component("applicationBean")
@Scope("prototype")
public class LookingForApp extends Application implements HttpServletRequestListener {
	public static final boolean debugMode = true;
	
	public WebApplicationContext appContext;
	private Window window;
	private static ThreadLocal<LookingForApp> threadLocal = new ThreadLocal<LookingForApp>();

	@Override
	public void init() {
		setInstance(this);
		
		window = new MainWindow();
		((MainWindow)window).initWindow();
		setMainWindow(window);
		setTheme("mytheme");
		setLogoutURL(getURL() + "jsp/logout");
	}

	public static LookingForApp getInstance() {
		return threadLocal.get();
	}

	public static void setInstance(LookingForApp lookingForApp) {
		threadLocal.set(lookingForApp);
	}

	public void onRequestStart(HttpServletRequest request, HttpServletResponse response) {
		LookingForApp.setInstance(this);
	}

	public void onRequestEnd(HttpServletRequest request, HttpServletResponse response) {
		threadLocal.remove();
	}

	public void setWebApplicationContext(WebApplicationContext appContext) {
		this.appContext = appContext;
	}

}
