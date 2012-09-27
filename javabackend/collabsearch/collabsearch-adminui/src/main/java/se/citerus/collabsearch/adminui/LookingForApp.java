/*
 * Copyright 2009 IT Mill Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package se.citerus.collabsearch.adminui;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.citerus.collabsearch.adminui.logic.LocalizationService;

import com.mongodb.Mongo;
import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.terminal.gwt.server.WebBrowser;
import com.vaadin.ui.Window;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class LookingForApp extends Application implements HttpServletRequestListener {
	public static final boolean debugMode = true;
	
	private Window window;

	private static ThreadLocal<LookingForApp> threadLocal = new ThreadLocal<LookingForApp>();

	@Override
	public void init() {
		setInstance(this);
		
		System.out.println("Client locale: " + getLocale());
		LocalizationService localizationService = new LocalizationService();
		localizationService.setup(getLocale());
		
		window = new MainWindow();
		((MainWindow)window).initWindow();
		setMainWindow(window);
	}

	public static LookingForApp getInstance() {
		return threadLocal.get();
	}

	public static void setInstance(LookingForApp lookingForApp) {
		threadLocal.set(lookingForApp);
	}

	@Override
	public String getUser() {
		return (String)super.getUser();
	}

	@Override
	public void setUser(Object user) {
		super.setUser((String)user);
	}

	public void onRequestStart(HttpServletRequest request, HttpServletResponse response) {
		LookingForApp.setInstance(this);
	}

	public void onRequestEnd(HttpServletRequest request, HttpServletResponse response) {
		threadLocal.remove();
	}

}
