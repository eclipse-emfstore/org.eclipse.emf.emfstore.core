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

import org.eclipse.emf.emfstore.internal.client.ui.controller.UICreateRemoteProjectController;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.Test;

public class UICreateRemoteProjectControllerTest extends AbstractUIControllerTest {

	private UICreateRemoteProjectController createRemoteProjectController;

	@Override
	@Test
	public void testController() throws ESException {
		final int remoteProjectsSize = getServer().getRemoteProjects(getUsersession()).size();
		UIThreadRunnable.asyncExec(new VoidResult() {
			public void run() {
				createRemoteProjectController = new UICreateRemoteProjectController(
					getBot().getDisplay().getActiveShell(),
					getUsersession());
				createRemoteProjectController.execute();
			}
		});
		final SWTBotText text = getBot().text(0);
		text.setText("foo");
		final SWTBotButton okButton = getBot().button("OK");
		okButton.click();
		getBot().waitUntil(new DefaultCondition() {
			// BEGIN SUPRESS CATCH EXCEPTION
			public boolean test() throws Exception {
				return getServer().getRemoteProjects(getUsersession()).size() == remoteProjectsSize + 1;
			}

			// END SUPRESS CATCH EXCEPTION

			public String getFailureMessage() {
				return "Create remote project did not succeed.";
			}
		}, timeout());
		assertEquals(remoteProjectsSize + 1, getServer().getRemoteProjects(getUsersession()).size());
	}
}
