/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.test.ui.controllers;

import org.eclipse.emf.emfstore.internal.client.ui.controller.UILoginSessionController;
import org.eclipse.emf.emfstore.internal.client.ui.controller.UILogoutSessionController;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.junit.Test;

/**
 * Tests login/logout
 *
 * @author emueller
 *
 */
public class UISessionControllerTest extends AbstractUIControllerTest {

	private UILoginSessionController loginSessionController;
	private UILogoutSessionController logoutSessionController;

	@Override
	@Test
	public void testController() throws ESException {
		logoutSession();
		login();
		logout();
	}

	private void logoutSession() throws ESException {
		getUsersession().logout();
	}

	private void login() {
		UIThreadRunnable.asyncExec(new VoidResult() {
			public void run() {
				loginSessionController = new UILoginSessionController(
					getBot().getDisplay().getActiveShell(),
					getServer());
				loginSessionController.execute();
			}
		});
		final SWTBotButton okButton = getBot().button("OK");
		okButton.click();
		assertNotNull(getServer().getLastUsersession());
		setUsersession(getServer().getLastUsersession());
	}

	private void logout() {
		UIThreadRunnable.syncExec(new VoidResult() {
			public void run() {
				logoutSessionController = new UILogoutSessionController(getBot().getDisplay().getActiveShell(),
					getUsersession());
				logoutSessionController.execute();
			}
		});
		assertNotNull(getServer().getLastUsersession());
	}
}
