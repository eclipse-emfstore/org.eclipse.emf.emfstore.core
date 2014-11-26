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

import org.eclipse.emf.emfstore.internal.client.ui.controller.UIDeleteRemoteProjectController;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.Test;

public class UIDeleteRemoteProjectControllerTest extends AbstractUIControllerTest {

	@Override
	@Test
	public void testController() throws ESException {
		final int remoteProjectsSize = getServer().getRemoteProjects().size();
		UIThreadRunnable.asyncExec(new VoidResult() {
			public void run() {
				UIDeleteRemoteProjectController deleteRemoteProjectController;
				try {
					deleteRemoteProjectController = new UIDeleteRemoteProjectController(getBot().getDisplay()
						.getActiveShell(),
						getUsersession(), getLocalProject().getRemoteProject());
					deleteRemoteProjectController.execute();
				} catch (final ESException e) {
					fail();
				}
			}
		});
		getBot().button("Yes").click();
		getBot().waitUntil(new DefaultCondition() {

			// BEGIN SUPRESS CATCH EXCEPTION
			public boolean test() throws Exception {
				return getServer().getRemoteProjects().size() == remoteProjectsSize - 1;
			}

			// END SUPRESS CATCH EXCEPTION

			public String getFailureMessage() {
				return "Delete remote project did not succeed.";
			}
		});
		assertEquals(remoteProjectsSize - 1, getServer().getRemoteProjects().size());
	}

}
