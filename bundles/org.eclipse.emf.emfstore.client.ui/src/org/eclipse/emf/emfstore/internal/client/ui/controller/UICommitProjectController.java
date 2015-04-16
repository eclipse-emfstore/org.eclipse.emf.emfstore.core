/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto von Wesendonk, Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.controller;

import java.text.MessageFormat;
import java.util.concurrent.Callable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.callbacks.ESCommitCallback;
import org.eclipse.emf.emfstore.client.util.ESVoidCallable;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.common.model.ESModelElementIdToEObjectMapping;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESLocalProjectImpl;
import org.eclipse.emf.emfstore.internal.client.model.util.WorkspaceUtil;
import org.eclipse.emf.emfstore.internal.client.ui.common.RunInUI;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.CommitDialog;
import org.eclipse.emf.emfstore.internal.common.model.impl.ESModelElementIdToEObjectMappingImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESChangePackageImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessageFactory;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.exceptions.ESUpdateRequiredException;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;
import org.eclipse.emf.emfstore.server.model.versionspec.ESPrimaryVersionSpec;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

/**
 * UI-dependent commit controller for committing pending changes on a {@link ESLocalProject}.<br/>
 * The controller presents the user a dialog will all changes made before he is
 * able to confirm the commit. If no changes have been made by the user a
 * information dialog is presented that states that there are no pending changes
 * to be committed.
 *
 * @author ovonwesen
 * @author emueller
 *
 */
public class UICommitProjectController extends
	AbstractEMFStoreUIController<ESPrimaryVersionSpec> implements
	ESCommitCallback {

	private final ESLocalProject localProject;
	private int dialogReturnValue;

	/**
	 * Constructor.
	 *
	 * @param shell
	 *            the parent shell that will be used during commit
	 * @param localProject
	 *            the {@link ESLocalProject} that contains the pending changes
	 *            that should get committed
	 */
	public UICommitProjectController(Shell shell, ESLocalProject localProject) {
		super(shell, false, true);
		this.localProject = localProject;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.callbacks.ESCommitCallback#noLocalChanges(org.eclipse.emf.emfstore.client.ESLocalProject)
	 */
	public void noLocalChanges(ESLocalProject localProject) {
		RunInUI.run(new Callable<Void>() {
			public Void call() throws Exception {
				MessageDialog.openInformation(getShell(),
					Messages.UICommitProjectController_NoLocalChanges_Title,
					Messages.UICommitProjectController_NoLocalChanges_Message);
				return null;
			}
		});
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.callbacks.ESCommitCallback#baseVersionOutOfDate(ESLocalProject,
	 *      IProgressMonitor)
	 */
	public boolean baseVersionOutOfDate(final ESLocalProject projectSpace,
		IProgressMonitor progressMonitor) {

		final String message = Messages.UICommitProjectController_ProjectOutdated;

		final boolean shouldUpdate = RunInUI.runWithResult(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return MessageDialog.openConfirm(getShell(), Messages.UICommitProjectController_Confirmation,
					message);
			}
		});

		if (shouldUpdate) {
			final ESPrimaryVersionSpec baseVersion = UICommitProjectController.this.localProject.getBaseVersion();
			final int baseVersionIdentifier = baseVersion.getIdentifier();
			final ESPrimaryVersionSpec version = new UIUpdateProjectController(getShell(), projectSpace)
				.executeSub(progressMonitor);

			// base version identifer may change due to update's recovery
			if (version.equals(baseVersion) || version.getIdentifier() == baseVersionIdentifier) {
				return false;
			}
		}

		return shouldUpdate;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.callbacks.ESCommitCallback#inspectChanges(org.eclipse.emf.emfstore.client.ESLocalProject,
	 *      org.eclipse.emf.emfstore.server.model.ESChangePackage,
	 *      org.eclipse.emf.emfstore.common.model.ESModelElementIdToEObjectMapping)
	 */
	public boolean inspectChanges(
		ESLocalProject localProject,
		ESChangePackage changePackage,
		ESModelElementIdToEObjectMapping idToEObjectMapping) {

		final ChangePackage internalChangePackage = ((ESChangePackageImpl) changePackage).toInternalAPI();
		final ProjectSpace projectSpace = ((ESLocalProjectImpl) localProject).toInternalAPI();

		if (internalChangePackage.getOperations().isEmpty()) {
			RunInUI.run(new Callable<Void>() {
				public Void call() throws Exception {
					MessageDialog
						.openInformation(
							getShell(), "No local changes", //$NON-NLS-1$
							Messages.UICommitProjectController_NoPendingChanges_0
								+ Messages.UICommitProjectController_NoPendingChanges_1
								+ Messages.UICommitProjectController_NoPendingChanges_2);
					return null;
				}
			});

			return false;
		}

		final CommitDialog commitDialog = new CommitDialog(
			getShell(),
			internalChangePackage,
			projectSpace,
			((ESModelElementIdToEObjectMappingImpl) idToEObjectMapping).toInternalAPI());

		dialogReturnValue = RunInUI.runWithResult(new Callable<Integer>() {
			public Integer call() throws Exception {
				return commitDialog.open();
			}
		});

		if (dialogReturnValue == Window.OK) {
			RunESCommand.run(new Callable<Void>() {
				public Void call() throws Exception {

					final String commitText = commitDialog.getLogText();
					final EList<String> oldLogMessages = projectSpace.getOldLogMessages();
					if (oldLogMessages.size() == 0 || !oldLogMessages.contains(commitText)) {
						oldLogMessages.add(commitText);
					} else if (oldLogMessages.contains(commitText)) {
						oldLogMessages.move(oldLogMessages.size() - 1, oldLogMessages.indexOf(commitText));
					}

					// remove older messages
					if (projectSpace.getOldLogMessages().size() > 10) {
						// the list can only grow one element at a time,
						// so only one element should be deleted
						projectSpace.getOldLogMessages().remove(0);
					}

					internalChangePackage.setLogMessage(
						LogMessageFactory.INSTANCE.createLogMessage(commitDialog.getLogText(),
							projectSpace.getUsersession().getUsername()));
					return null;
				}
			});

			return true;
		}

		return false;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.common.MonitoredEMFStoreAction#doRun(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public ESPrimaryVersionSpec doRun(final IProgressMonitor progressMonitor)
		throws ESException {
		try {

			final ESPrimaryVersionSpec primaryVersionSpec = localProject.commit(
				null,
				UICommitProjectController.this,
				progressMonitor);
			return primaryVersionSpec;

		} catch (final ESUpdateRequiredException e) {
			// project is out of date and user canceled update
			// ignore
		} catch (final ESException e) {
			WorkspaceUtil.logException(e.getMessage(), e);
			if (e.getCause() instanceof Error) {
				RunInUI.run(new ESVoidCallable() {
					@Override
					public void run() {
						MessageDialog.openError(
							getShell(),
							Messages.UICommitProjectController_CommitFailed,
							MessageFormat
								.format(
									Messages.UICommitProjectController_ErrorDuringCommit,
									e.getCause().getMessage(),
									e.getCause().getClass().getSimpleName()));
					}
				});
			} else {
				RunInUI.run(new Callable<Void>() {
					public Void call() throws Exception {
						MessageDialog.openError(getShell(),
							Messages.UICommitProjectController_CommitFailed,
							e.getMessage());
						return null;
					}
				});
			}
		}

		return null;
	}
}