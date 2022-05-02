/*******************************************************************************
 * Copyright (c) 2011-2019 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.test.ui.controllers;

import java.io.IOException;

import org.eclipse.emf.emfstore.common.ESObserver;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.ui.controller.UIShowHistoryController;
import org.eclipse.emf.emfstore.internal.common.observer.ObserverExceptionListener;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.junit.Test;

public class UIHistoryViewCloseTest extends AbstractUIControllerTestWithCommit {

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

		historyView.close();

		final ObserverExceptionListener exceptionListener = new ObserverExceptionListener() {

			public void onException(ESObserver observer, Throwable throwable) {
				if (observer.getClass().getName().contains("HistoryBrowserView")) {
					fail(throwable.getMessage());
				}
			}
		};
		ESWorkspaceProviderImpl.getObserverBus().registerExceptionListener(exceptionListener);

		try {
			getLocalProject().delete(null);
		} catch (final IOException ex) {
			fail(ex.getMessage());
		} finally {
			ESWorkspaceProviderImpl.getObserverBus().unregisterExceptionListener(exceptionListener);
		}
	}
}
