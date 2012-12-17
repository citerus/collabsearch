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
package se.citerus.collabsearch.publicwebsite.logic;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.citerus.collabsearch.publicwebsite.Controller;

import com.vaadin.Application;
import com.vaadin.ui.Window;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class MainApplication extends Application {
	private Window mainWindow;

	private static ThreadLocal<MainApplication> threadLocal = new ThreadLocal<MainApplication>();
	
	@Override
	public void init() {
		mainWindow = new Window("Missing People");
		setMainWindow(mainWindow);
		setTheme("mytheme");
		
		String restServerUrl = loadServerUrlFromPropFile();
		
		Controller controller = new Controller(mainWindow, restServerUrl);
		controller.startup();
	}
	
	private String loadServerUrlFromPropFile() {
		String url = "http://missingpeople-api.cloudfoundry.com/";
		InputStream stream = null;
		try {
			Properties prop = new Properties();
			stream = MainApplication.class.getResourceAsStream(
					"/sms-config.properties");
			if (stream != null) {
				prop.load(stream);
				url = prop.getProperty("ACCOUNT_SID");
			} else {
				System.err.println("Property file stream was null, using default URL: " + url);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return url;
	}

	public static MainApplication getInstance() {
		return threadLocal.get();
	}
	
	public static void setInstance(MainApplication mainApplication) {
		threadLocal.set(mainApplication);
	}
	
	public void onRequestStart(HttpServletRequest request, HttpServletResponse response) {
		MainApplication.setInstance(this);
	}
	
	public void onRequestEnd(HttpServletRequest request, HttpServletResponse response) {
		threadLocal.remove();
	}

}
