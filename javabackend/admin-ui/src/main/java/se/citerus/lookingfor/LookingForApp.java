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
package se.citerus.lookingfor;


import com.vaadin.Application;
import com.vaadin.ui.Window;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class LookingForApp extends Application {
	private Window window;
	private static LookingForApp APP;
	
	@Override
	public void init() {
		APP = this;
		window = new MainWindow("Missing People - Login");
		setMainWindow(window);
	}

	public static LookingForApp get() {
		return APP;
	}

	@Override
	public String getUser() {
		return (String)super.getUser();
	}

	public void setUser(String user) {
		super.setUser(user);
	}

}
