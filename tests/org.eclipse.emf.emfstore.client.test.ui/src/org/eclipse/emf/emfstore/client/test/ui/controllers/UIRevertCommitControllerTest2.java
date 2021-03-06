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

import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.emfstore.bowling.BowlingFactory;
import org.eclipse.emf.emfstore.bowling.Player;
import org.eclipse.emf.emfstore.bowling.Tournament;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.ESWorkspaceProvider;
import org.eclipse.emf.emfstore.client.callbacks.ESUpdateCallback;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.common.model.ESModelElementIdToEObjectMapping;
import org.eclipse.emf.emfstore.internal.client.ui.controller.UIRevertCommitController;
import org.eclipse.emf.emfstore.server.ESConflictSet;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;
import org.eclipse.emf.emfstore.server.model.versionspec.ESPrimaryVersionSpec;
import org.eclipse.emf.emfstore.server.model.versionspec.ESVersionSpec;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Test;

public class UIRevertCommitControllerTest2 extends AbstractUIControllerTestWithCommit {

	@Override
	@Test
	public void testController() throws ESException {
		assertEquals(0, getLocalProject().getModelElements().size());
		final ESUpdateCallback updateCallback = new MyUpdateCallback();
		final IProgressMonitor monitor = new NullProgressMonitor();

		// create checkout
		checkout();
		createPlayerAndTournamentAndCommit();
		final ESPrimaryVersionSpec baseVersion = getLocalProject().getBaseVersion();

		// update checkout
		getCopy().update(getLocalProject().getBaseVersion(), updateCallback, monitor);

		// delete player
		deleteTournamentAndCommit();
		assertEquals(1, getLocalProject().getModelElements().size());

		// update checkout
		getCopy().update(getLocalProject().getBaseVersion(), updateCallback, monitor);
		assertEquals(1, getCopy().getModelElements().size());

		// revert to version where tournament has been created
		revertAndCommit(baseVersion);

		// update checkout
		getCopy().update(ESVersionSpec.FACTORY.createHEAD(), updateCallback, monitor);
		assertEquals(2, getCopy().getModelElements().size());
		final Tournament tournament = getCopy().getAllModelElementsByClass(Tournament.class).iterator().next();
		assertEquals(new Integer(32), tournament.getPlayerPoints().values().iterator().next());

		// revert again, should have no effect
		revertAndCommit(baseVersion);
		getCopy().update(ESVersionSpec.FACTORY.createHEAD(), updateCallback, monitor);

		assertEquals(2, getCopy().getModelElements().size());
	}

	private void createPlayerAndTournamentAndCommit() {
		final Player player = BowlingFactory.eINSTANCE.createPlayer();
		player.setName("player");
		final Tournament tournament = BowlingFactory.eINSTANCE.createTournament();
		tournament.getPlayerPoints().put(player, 32);

		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				getLocalProject().getModelElements().add(player);
				getLocalProject().getModelElements().add(tournament);
				return null;
			}
		});
		commit();
	}

	private void revertAndCommit(final ESPrimaryVersionSpec baseVersion) throws ESException {

		final int localProjectsSize = ESWorkspaceProvider.INSTANCE.getWorkspace().getLocalProjects().size();

		UIThreadRunnable.asyncExec(new VoidResult() {
			public void run() {
				final UIRevertCommitController revertCommitController = new UIRevertCommitController(
					getBot().getDisplay().getActiveShell(),
					baseVersion,
					getLocalProject());
				revertCommitController.execute();
			}
		});

		final SWTBotShell shell = getBot().shell("Confirmation");
		shell.bot().button("OK").click();

		getBot().waitUntil(new DefaultCondition() {
			// BEGIN SUPRESS CATCH EXCEPTION

			public boolean test() throws Exception {
				return localProjectsSize + 1 == ESWorkspaceProvider.INSTANCE.getWorkspace().getLocalProjects().size();
			}

			// END SUPRESS CATCH EXCEPTION

			public String getFailureMessage() {
				return "Revert did not succeed.";
			}
		}, timeout());

		final List<ESLocalProject> localProjects = ESWorkspaceProvider.INSTANCE.getWorkspace().getLocalProjects();
		final ESLocalProject localProject = localProjects.get(localProjects.size() - 1);
		localProject.commit(new NullProgressMonitor());

	}

	protected void deleteTournamentAndCommit() {
		assertEquals(2, getLocalProject().getModelElements().size());
		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				final Tournament tournament = getLocalProject().getAllModelElementsByClass(Tournament.class).iterator()
					.next();
				getLocalProject().getModelElements().remove(tournament);
				return null;
			}
		});
		commit();
		assertEquals(1, getLocalProject().getModelElements().size());
	}

	private class MyUpdateCallback implements ESUpdateCallback {

		public boolean inspectChanges(ESLocalProject projectSpace, List<ESChangePackage> changes,
			ESModelElementIdToEObjectMapping idToEObjectMapping) {
			return ESUpdateCallback.NOCALLBACK.inspectChanges(projectSpace, changes, idToEObjectMapping);
		}

		public void noChangesOnServer() {
			ESUpdateCallback.NOCALLBACK.noChangesOnServer();
		}

		public boolean conflictOccurred(ESConflictSet changeConflictException,
			IProgressMonitor progressMonitor) {
			return ESUpdateCallback.NOCALLBACK.conflictOccurred(changeConflictException, progressMonitor);
		}
	}

}
