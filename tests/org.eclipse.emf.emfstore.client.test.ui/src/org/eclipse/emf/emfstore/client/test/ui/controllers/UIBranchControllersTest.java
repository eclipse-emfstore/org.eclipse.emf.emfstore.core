/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.test.ui.controllers;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.ESWorkspaceProvider;
import org.eclipse.emf.emfstore.internal.client.ui.controller.UICheckoutController;
import org.eclipse.emf.emfstore.internal.client.ui.controller.UICreateBranchController;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Test;

public class UIBranchControllersTest extends AbstractUIControllerTestWithCommit {

	@Override
	@Test
	public void testController() throws ESException {
		final NullProgressMonitor monitor = new NullProgressMonitor();
		final int branchesSize = getLocalProject().getBranches(monitor).size();
		UIThreadRunnable.asyncExec(new VoidResult() {
			public void run() {
				final UICreateBranchController createBranchController = new UICreateBranchController(getBot()
					.getDisplay()
					.getActiveShell(),
					getLocalProject());
				createBranchController.execute();
			}
		});
		final SWTBotShell shell = getBot().shell("Create Branch");

		shell.bot().text(0).setText("foo");
		shell.bot().button("OK").click();
		final SWTBotShell commitDialogShell = getBot().shell("Commit");
		commitDialogShell.bot().button("OK").click();

		getBot().waitUntil(new DefaultCondition() {

			// BEGIN SUPRESS CATCH EXCEPTION
			public boolean test() throws Exception {
				return branchesSize + 1 == getLocalProject().getBranches(monitor).size();
			}

			// END SUPRESS CATCH EXCEPTION

			public String getFailureMessage() {
				return "Create branch did not succeed.";
			}
		}, timeout());
		assertEquals(branchesSize + 1, getLocalProject().getBranches(monitor).size());

		UIThreadRunnable.asyncExec(new VoidResult() {
			public void run() {
				try {
					final UICheckoutController checkoutController = new UICheckoutController(getBot().getDisplay()
						.getActiveShell(),
						getLocalProject().getRemoteProject(), true);
					checkoutController.execute();
				} catch (final ESException e) {
					fail(e.getMessage());
				}
			}
		});

		getBot().text(0).setText("branch-checkout");
		getBot().button("OK").click();

		getBot().table().select(0);
		getBot().button("OK").click();

		getBot().waitUntil(new DefaultCondition() {
			// BEGIN SUPRESS CATCH EXCEPTION
			public boolean test() throws Exception {
				for (final ESLocalProject localProject : ESWorkspaceProvider.INSTANCE.getWorkspace()
					.getLocalProjects()) {
					if (localProject.getProjectName().equals("branch-checkout")) {
						return true;
					}
				}
				return false;
			}

			// END SUPRESS CATCH EXCEPTION

			public String getFailureMessage() {
				return "Branch checkout did not succeed";
			}
		});

		assertEquals(2, ESWorkspaceProvider.INSTANCE.getWorkspace().getLocalProjects().size());

	}

}
