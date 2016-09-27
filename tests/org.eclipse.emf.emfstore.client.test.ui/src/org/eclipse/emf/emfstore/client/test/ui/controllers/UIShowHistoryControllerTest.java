/*******************************************************************************
 * Copyright (c) 2011-2013 EclipseSource Muenchen GmbH and others.
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

import org.eclipse.emf.emfstore.internal.client.ui.controller.UIShowHistoryController;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.junit.Test;

/**
 * Test the {@link UIShowHistoryController} by displaying the history of a certain project.
 *
 * @author emueller
 *
 */
public class UIShowHistoryControllerTest extends AbstractUIControllerTestWithCommit {

	@Override
	@Test
	public void testController() throws ESException {

		createPlayerAndCommit();
		UIThreadRunnable.asyncExec(new VoidResult() {
			public void run() {
				final UIShowHistoryController showHistoryController = new UIShowHistoryController(
					getBot().getDisplay().getActiveShell(), getLocalProject());
				showHistoryController.execute();
			}
		});

		final SWTBotView historyView = getBot().viewById(
			"org.eclipse.emf.emfstore.client.ui.views.historybrowserview.HistoryBrowserView");

		assertNotNull(historyView);
	}
}
