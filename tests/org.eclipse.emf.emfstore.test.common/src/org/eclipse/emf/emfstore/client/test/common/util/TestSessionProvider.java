/*******************************************************************************
 * Copyright (c) 2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.test.common.util;

import org.eclipse.emf.emfstore.client.ESServer;
import org.eclipse.emf.emfstore.client.ESUsersession;
import org.eclipse.emf.emfstore.client.sessionprovider.ESAbstractSessionProvider;
import org.eclipse.emf.emfstore.client.util.ESVoidCallable;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.ModelFactory;
import org.eclipse.emf.emfstore.internal.client.model.Usersession;
import org.eclipse.emf.emfstore.internal.client.model.Workspace;
import org.eclipse.emf.emfstore.internal.client.model.connectionmanager.KeyStoreManager;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESServerImpl;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESWorkspaceImpl;
import org.eclipse.emf.emfstore.server.exceptions.ESException;

public class TestSessionProvider extends ESAbstractSessionProvider {

	private Usersession session;

	public TestSessionProvider() {
	}

	private void initSession(ESServer server) {

		if (server == null) {
			server = ESServer.FACTORY.createServer("localhost", 8080, KeyStoreManager.DEFAULT_CERTIFICATE); //$NON-NLS-1$
		}

		final ESWorkspaceImpl workspace = ESWorkspaceProviderImpl.getInstance().getWorkspace();
		final Workspace internalWorkspace = workspace.toInternalAPI();
		// TODO: contains check for server infos
		if (!internalWorkspace.getServerInfos().contains(server)) {
			workspace.addServer(server);
		}

		// ServerInfo serverInfo = SetupHelper.getServerInfo();
		session = ModelFactory.eINSTANCE.createUsersession();
		// session.setServerInfo(serverInfo);
		session.setUsername("super"); //$NON-NLS-1$
		session.setPassword("super"); //$NON-NLS-1$
		session.setSavePassword(true);
		session.setServerInfo(((ESServerImpl) server).toInternalAPI());
		RunESCommand.run(new ESVoidCallable() {
			@Override
			public void run() {
				internalWorkspace.getUsersessions().add(session);
			}
		});
		internalWorkspace.save();
	}

	@Override
	public ESUsersession provideUsersession(ESServer serverInfo) throws ESException {
		final Workspace internalWorkspace = ESWorkspaceProviderImpl.getInstance().getWorkspace().toInternalAPI();
		if (session != null && internalWorkspace.getUsersessions().contains(session)) {
			return session.toAPI();
		}

		if (serverInfo != null && serverInfo.getLastUsersession() != null) {
			return serverInfo.getLastUsersession();

		}

		if (session == null || !internalWorkspace.getUsersessions().contains(session)) {
			initSession(serverInfo);
		}

		return session.toAPI();
	}

	@Override
	public ESUsersession login(ESUsersession usersession) throws ESException {
		session.logIn();
		return session.toAPI();
	}

	public void clearSession() {
		final Workspace internalWorkspace = ESWorkspaceProviderImpl.getInstance().getWorkspace().toInternalAPI();
		if (session != null && internalWorkspace.getUsersessions().contains(session)) {
			internalWorkspace.getUsersessions().remove(session);
			session = null;
		}
	}
}