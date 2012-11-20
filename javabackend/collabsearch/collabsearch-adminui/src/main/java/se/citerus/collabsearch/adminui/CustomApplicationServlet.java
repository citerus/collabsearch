package se.citerus.collabsearch.adminui;

import java.io.BufferedWriter;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class CustomApplicationServlet extends AbstractApplicationServlet {

	private WebApplicationContext appContext;

	@Override
	protected Application getNewApplication(HttpServletRequest httpServletRequest) throws ServletException {
	    LookingForApp app = (LookingForApp) appContext.getBean("applicationBean");
	    app.setWebApplicationContext(appContext);
	    return  app;
	}
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    super.service(request, response);
	}
	
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
	    super.init(servletConfig);
	    appContext = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());
	}
	
	@Override
	protected Class<? extends Application> getApplicationClass() throws ClassNotFoundException {
		return LookingForApp.class;
	}
	
	@Override
	protected void writeAjaxPageHtmlVaadinScripts(Window window,
	        String themeName, Application application, BufferedWriter page,
	        String appUrl, String themeUri, String appId,
	        HttpServletRequest request) throws ServletException, IOException {
		
	    page.write("<script type=\"text/javascript\">\n");
	    page.write("//<![CDATA[\n");
	    page.write("document.write(\"<script language='javascript' src='http://code.jquery.com/jquery-1.8.0.min.js'><\\/script>\");\n");
	    page.write("//]]>\n</script>\n");
	    
	    super.writeAjaxPageHtmlVaadinScripts(window, themeName, application,
	        page, appUrl, themeUri, appId, request);
	}

}
