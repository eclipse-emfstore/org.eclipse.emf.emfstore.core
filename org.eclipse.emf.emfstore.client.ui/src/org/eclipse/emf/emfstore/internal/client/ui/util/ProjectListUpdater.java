/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.util;

import java.util.concurrent.Callable;

import org.eclipse.emf.emfstore.client.ESUsersession;
import org.eclipse.emf.emfstore.client.IWorkspace;
import org.eclipse.emf.emfstore.client.model.observer.ESLoginObserver;
import org.eclipse.emf.emfstore.client.model.observer.ESLogoutObserver;
import org.eclipse.emf.emfstore.client.model.observer.ESShareObserver;
import org.eclipse.emf.emfstore.client.model.observer.ESWorkspaceInitObserver;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.ServerInfo;
import org.eclipse.emf.emfstore.internal.client.model.Usersession;
import org.eclipse.emf.emfstore.internal.client.model.WorkspaceProvider;
import org.eclipse.emf.emfstore.internal.client.model.impl.WorkspaceBase;
import org.eclipse.emf.emfstore.internal.client.model.util.WorkspaceUtil;
import org.eclipse.emf.emfstore.internal.client.ui.common.RunInUI;
import org.eclipse.emf.emfstore.internal.server.exceptions.EMFStoreException;

/**
 * This class is responsible for keeping the workspace's project infos update to date.
 */
public class ProjectListUpdater implements ESWorkspaceInitObserver, ESShareObserver, ESLoginObserver, ESLogoutObserver {

	private IWorkspace workspace;

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.observer.ESWorkspaceInitObserver#workspaceInitComplete(org.eclipse.emf.emfstore.client.IWorkspace)
	 */
	public void workspaceInitComplete(IWorkspace currentWorkspace) {
		this.workspace = currentWorkspace;
		WorkspaceProvider.getObserverBus().register(this);
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.observer.ESLoginObserver#loginCompleted(org.eclipse.emf.emfstore.client.ESUsersession)
	 */
	public void loginCompleted(ESUsersession session) {
		try {
			update(session);
		} catch (EMFStoreException e) {
			// fail silently
			WorkspaceUtil.logException("Couldn't project infos upon loginCompleted.", e);
		}
		updateACUser(session);
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.observer.ESShareObserver#shareDone(org.eclipse.emf.emfstore.internal.client.model.ProjectSpace)
	 */
	public void shareDone(ProjectSpace projectSpace) {
		try {
			update(projectSpace.getUsersession());
		} catch (EMFStoreException e) {
			// fail silently
			WorkspaceUtil.logException("Couldn't project infos upon shareDone.", e);
		}
	}

	private void updateACUser(ESUsersession session) {
		try {
			((WorkspaceBase) workspace).updateACUser((Usersession) session);
		} catch (EMFStoreException e) {
			// fail silently
			WorkspaceUtil.logException("Couldn't update ACUser.", e);
		}
	}

	private void update(final ESUsersession session) throws EMFStoreException {
		RunInUI.WithException.run(new Callable<Void>() {
			public Void call() throws Exception {
				// throw new NotImplementedException("TODO OTS");
				return null;
			}
		});
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.observer.ESLogoutObserver#logoutCompleted(org.eclipse.emf.emfstore.client.ESUsersession)
	 */
	public void logoutCompleted(ESUsersession session) {
		// TODO OTS cast
		ServerInfo server = (ServerInfo) session.getServer();
		if (server != null) {
			server.getProjectInfos().clear();
		}
	}

}