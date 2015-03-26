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

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withText;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.waitForShell;

import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.emfstore.bowling.BowlingFactory;
import org.eclipse.emf.emfstore.bowling.League;
import org.eclipse.emf.emfstore.bowling.Player;
import org.eclipse.emf.emfstore.bowling.Tournament;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.observer.ESUpdateObserver;
import org.eclipse.emf.emfstore.client.test.ui.AllUITests;
import org.eclipse.emf.emfstore.client.ui.ESUIControllerFactory;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.ui.controller.UICheckoutController;
import org.eclipse.emf.emfstore.internal.client.ui.controller.UIUpdateProjectController;
import org.eclipse.emf.emfstore.internal.client.ui.controller.UIUpdateProjectToVersionController;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;
import org.eclipse.emf.emfstore.server.model.versionspec.ESPrimaryVersionSpec;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.hamcrest.Matcher;

public abstract class AbstractUIControllerTestWithCommit extends AbstractUIControllerTest {

	public static final String PLAYER_NAME = "A";
	public static final String LEAGUE_NAME = "L";
	private boolean didUpdate;

	protected void createTournamentAndCommit() {
		final Tournament tournament = BowlingFactory.eINSTANCE.createTournament();
		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				getLocalProject().getModelElements().add(tournament);
				return null;
			}
		});
		commit(getLocalProject());
	}

	protected void createLeagueAndCommit() {
		createLeagueAndCommit(getLocalProject());
	}

	protected Player createPlayerAndCommit() {
		return createPlayerAndCommit(getLocalProject());
	}

	protected Player createPlayerAndCommit(final ESLocalProject localProject) {
		final Player player = BowlingFactory.eINSTANCE.createPlayer();
		player.setName(PLAYER_NAME);
		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				localProject.getModelElements().add(player);
				return null;
			}
		});
		commit(localProject);
		return player;
	}

	protected void createLeagueAndCommit(final ESLocalProject localProject) {
		final League league = BowlingFactory.eINSTANCE.createLeague();
		league.setName("L");
		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				localProject.getModelElements().add(league);
				return null;
			}
		});
		commit(localProject);
	}

	protected void commit() {
		commit(getLocalProject());
	}

	protected void commit(final ESLocalProject localProject) {
		final ESPrimaryVersionSpec baseVersion = localProject.getBaseVersion();
		UIThreadRunnable.asyncExec(new VoidResult() {
			public void run() {
				ESUIControllerFactory.INSTANCE.commitProject(
					getBot().getDisplay().getActiveShell(),
					localProject);
			}
		});

		final SWTBotButton buttonWithLabel = getBot().button("OK");
		buttonWithLabel.click();

		getBot().waitUntil(new DefaultCondition() {
			// BEGIN SUPRESS CATCH EXCEPTION
			public boolean test() throws Exception {
				return baseVersion.getIdentifier() + 1 == localProject.getBaseVersion()
					.getIdentifier();
			}

			// END SUPRESS CATCH EXCEPTION

			public String getFailureMessage() {
				return "Commit did not succeed.";
			}
		}, AllUITests.TIMEOUT);

		assertEquals(baseVersion.getIdentifier() + 1,
			localProject.getBaseVersion().getIdentifier());
	}

	protected void checkout() {
		UIThreadRunnable.asyncExec(new VoidResult() {

			public void run() {
				UICheckoutController checkoutController;
				try {
					checkoutController = new UICheckoutController(
						getBot().getDisplay().getActiveShell(),
						getLocalProject().getRemoteProject());
					setCheckedoutCopy(checkoutController.execute());
				} catch (final ESException e) {
					fail(e.getMessage());
				}
			}
		});

		getBot().text().setText("checkout");
		getBot().button("OK").click();

		getBot().waitUntil(new DefaultCondition() {
			// BEGIN SUPRESS CATCH EXCEPTION
			public boolean test() throws Exception {
				return getCheckedoutCopy() != null;
			}

			// END SUPRESS CATCH EXCEPTION

			public String getFailureMessage() {
				return "Checkout did not succeed";
			}
		}, timeout());
	}

	protected ESLocalProject checkout(final ESPrimaryVersionSpec versionSpec, String checkoutName) {

		final ESLocalProject[] localProjectArr = new ESLocalProject[1];

		UIThreadRunnable.asyncExec(new VoidResult() {

			public void run() {
				UICheckoutController checkoutController;
				try {
					checkoutController = new UICheckoutController(
						getBot().getDisplay().getActiveShell(),
						versionSpec,
						getLocalProject().getRemoteProject());
					localProjectArr[0] = checkoutController.execute();
				} catch (final ESException e) {
					fail(e.getMessage());
				}
			}
		});

		getBot().text().setText(checkoutName);
		getBot().button("OK").click();

		getBot().waitUntil(new DefaultCondition() {

			// BEGIN SUPRESS CATCH EXCEPTION
			public boolean test() throws Exception {
				return localProjectArr[0] != null;
			}

			// END SUPRESS CATCH EXCEPTION

			public String getFailureMessage() {
				return "Checkout did not succeed";
			}
		}, timeout());

		return localProjectArr[0];
	}

	protected ESPrimaryVersionSpec updateCopy() {
		SWTBotPreferences.PLAYBACK_DELAY = 100;
		didUpdate = false;

		final ESUpdateObserver updateObserver = createUpdateObserver();
		ESWorkspaceProviderImpl.getInstance();
		ESWorkspaceProviderImpl.getObserverBus().register(updateObserver);

		UIThreadRunnable.asyncExec(new VoidResult() {
			public void run() {
				final UIUpdateProjectController updateProjectController = new UIUpdateProjectController(
					getBot().getDisplay().getActiveShell(),
					getCheckedoutCopy());
				updateProjectController.execute();
			}
		});

		final Matcher<Shell> matcher = withText("Update");
		getBot().waitUntil(waitForShell(matcher));
		getBot().button("OK").click();

		getBot().waitUntil(new DefaultCondition() {
			// BEGIN SUPRESS CATCH EXCEPTION
			public boolean test() throws Exception {
				return didUpdate;
			}

			// END SUPRESS CATCH EXCEPTION

			public String getFailureMessage() {
				return "Update did not succeed.";
			}
		}, timeout());

		ESWorkspaceProviderImpl.getInstance();
		ESWorkspaceProviderImpl.getObserverBus().unregister(updateObserver);

		return getCheckedoutCopy().getBaseVersion();
	}

	protected ESPrimaryVersionSpec updateToVersion() {
		SWTBotPreferences.PLAYBACK_DELAY = 100;
		didUpdate = false;

		final ESUpdateObserver updateObserver = createUpdateObserver();
		ESWorkspaceProviderImpl.getInstance();
		ESWorkspaceProviderImpl.getObserverBus().register(updateObserver);

		UIThreadRunnable.asyncExec(new VoidResult() {
			public void run() {
				final UIUpdateProjectToVersionController updateProjectController = new UIUpdateProjectToVersionController(
					getBot().getDisplay().getActiveShell(),
					getCheckedoutCopy());
				updateProjectController.execute();
			}
		});

		Matcher<Shell> matcher = withText("Select a Version to update to");
		getBot().waitUntil(waitForShell(matcher));
		getBot().button("OK").click();

		matcher = withText("Update");
		getBot().waitUntil(waitForShell(matcher));
		getBot().button("OK").click();

		getBot().waitUntil(new DefaultCondition() {
			// BEGIN SUPRESS CATCH EXCEPTION
			public boolean test() throws Exception {
				return didUpdate;
			}

			// END SUPRESS CATCH EXCEPTION

			public String getFailureMessage() {
				return "Update to version did not succeed.";
			}
		}, 600000);

		ESWorkspaceProviderImpl.getInstance();
		ESWorkspaceProviderImpl.getObserverBus().unregister(updateObserver);

		return getCheckedoutCopy().getBaseVersion();
	}

	private ESUpdateObserver createUpdateObserver() {
		return new ESUpdateObserver() {

			public void updateCompleted(ESLocalProject project, IProgressMonitor monitor) {
				didUpdate = true;
			}

			public boolean inspectChanges(ESLocalProject project, List<ESChangePackage> changePackages,
				IProgressMonitor monitor) {
				return true;
			}
		};
	}

	protected ESPrimaryVersionSpec pagedUpdate() {

		UIThreadRunnable.asyncExec(new VoidResult() {
			public void run() {
				final UIUpdateProjectController updateProjectController = new UIUpdateProjectController(
					getBot().getDisplay().getActiveShell(),
					getCheckedoutCopy());
				updateProjectController.execute();
			}
		});

		final SWTBotButton buttonWithLabel = getBot().button("OK");
		buttonWithLabel.click();

		getBot().waitUntil(new DefaultCondition() {
			// BEGIN SUPRESS CATCH EXCEPTION
			public boolean test() throws Exception {
				return getCheckedoutCopy().getBaseVersion().getIdentifier() ==
					getLocalProject().getBaseVersion().getIdentifier() - 1;
			}

			// END SUPRESS CATCH EXCEPTION

			public String getFailureMessage() {
				return "Paged Update did not succeed.";
			}
		}, timeout());

		final Matcher<Shell> matcher = withText("More updates available");
		getBot().waitUntil(waitForShell(matcher));
		getBot().button("OK").click(); // update notification hint
		getBot().button("OK").click(); // inspect changes on update

		getBot().waitUntil(new DefaultCondition() {
			// BEGIN SUPRESS CATCH EXCEPTION
			public boolean test() throws Exception {
				return getCheckedoutCopy().getBaseVersion().getIdentifier() ==
					getLocalProject().getBaseVersion().getIdentifier();
			}

			// END SUPRESS CATCH EXCEPTION

			public String getFailureMessage() {
				return "Paged Update did not succeed.";
			}
		}, timeout());

		return getCheckedoutCopy().getBaseVersion();
	}
}
