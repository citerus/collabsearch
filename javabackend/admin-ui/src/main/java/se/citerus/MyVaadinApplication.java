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
package se.citerus;


import se.citerus.logic.Authenticator;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.LoginForm.LoginListener;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class MyVaadinApplication extends Application implements LoginListener {
	private Window window;

	@Override
	public void init() {
		window = new Window("Missing People prototype");
		setMainWindow(window);
		
		final TextField nameField = new TextField();
		final PasswordField passwordField = new PasswordField();
		
		Button loginButton = new Button("Login");
		loginButton.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				String username = nameField.getValue().toString();
				char[] password = passwordField.getValue().toString().toCharArray();
				Authenticator authenticator = new Authenticator();
				boolean b = authenticator.login(username, password);
				if (b) {
					window.showNotification("Successful login");
				} else {
					window.showNotification("Failed to login");
				}
			}
		});
		
		window.addComponent(nameField);
		window.addComponent(passwordField);
		window.addComponent(loginButton);
	}

	public void onLogin(LoginEvent event) {
		
	}

}
