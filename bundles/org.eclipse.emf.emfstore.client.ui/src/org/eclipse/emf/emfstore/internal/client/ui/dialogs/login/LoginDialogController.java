/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto von Wesendonk
 * Edgar Mueller
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.dialogs.login;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.emfstore.client.ESServer;
import org.eclipse.emf.emfstore.client.ESUsersession;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.ServerInfo;
import org.eclipse.emf.emfstore.internal.client.model.Usersession;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESUsersessionImpl;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESWorkspaceImpl;
import org.eclipse.emf.emfstore.internal.client.ui.common.RunInUI;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

/**
 * The login dialog controller manages a given {@link ESUsersession} and/or a {@link ESServer} to determine when it is
 * necessary to open a {@link LoginDialog} in order to authenticate the user. It does not, however,
 * open a dialog, if the usersession is already logged in.
 *
 * @author ovonwesen
 * @author emueller
 */
public class LoginDialogController implements ILoginDialogController {

	private ESUsersession usersession;
	private ESServer server;
	private LoginDialog dialog;

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.dialogs.login.ILoginDialogController#getKnownUsersessions()
	 */
	public List<ESUsersession> getKnownUsersessions() {
		final EList<Usersession> usersessions = ESWorkspaceProviderImpl.getInstance().getWorkspace().toInternalAPI()
			.getUsersessions();
		final List<ESUsersession> knownSessions = new ArrayList<ESUsersession>();
		for (final Usersession session : usersessions) {
			final ServerInfo serverInfo = session.getServerInfo();
			// server info should never be null, but in case it is (whatever the reason may be)
			// make sure it does not kill the initialization of the login dialog
			if (serverInfo != null && serverInfo.toAPI() == server) {
				knownSessions.add(session.toAPI());
			}
		}

		return knownSessions;
	}

	private ESUsersession login(final boolean force) throws ESException {

		if (server != null
			&& server.getLastUsersession() != null
			&& server.getLastUsersession().isLoggedIn()
			&& !force) {
			// session seems to be valid, renew just in case the session timed out
			server.getLastUsersession().refresh();
			return server.getLastUsersession();
		}

		final Integer userInput = RunInUI.runWithResult(new Callable<Integer>() {
			public Integer call() throws Exception {
				dialog = new LoginDialog(Display
					.getCurrent().getActiveShell(),
					LoginDialogController.this);
				dialog.setBlockOnOpen(true);
				return dialog.open();
			}
		});

		if (userInput != Window.OK) {
			throw new AccessControlException(Messages.LoginDialogController_LoginFailed);
		}

		final Usersession session = dialog.getSelectedUsersession();

		if (session == null) {
			throw new AccessControlException(Messages.LoginDialogController_LoginFailed);
		}

		final String password = dialog.getPassword();
		final boolean savePassword = dialog.isSavePassword();
		final boolean passwordModified = dialog.isPasswordModified();

		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				session.setSavePassword(savePassword);
				if (passwordModified) {
					session.setPassword(password);
				}
				return null;
			}
		});

		validate(session.toAPI());

		// contract: #validate() sets the usersession;
		// TODO: validate can simply return the usersession..
		return usersession;
	}

	/**
	 * Returns the server name.
	 *
	 * @return the label
	 */
	public String getServerLabel() {
		return getServer().getName();
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.dialogs.login.ILoginDialogController#validate(org.eclipse.emf.emfstore.client.ESUsersession)
	 */
	public void validate(final ESUsersession session) throws ESException {

		final Usersession usersession = ((ESUsersessionImpl) session).toInternalAPI();
		final ESWorkspaceImpl workspace = ESWorkspaceProviderImpl.getInstance().getWorkspace();
		final EList<Usersession> usersessions = workspace.toInternalAPI().getUsersessions();

		RunESCommand.WithException.run(ESException.class, new Callable<Void>() {
			public Void call() throws Exception {
				// TODO login code
				usersession.logIn();
				// if successful, else exception is thrown prior reaching this code
				if (!usersessions.contains(usersession)) {
					usersessions.add(usersession);
				}
				return null;
			}
		});

		this.usersession = session;
		ESWorkspaceProviderImpl.getInstance().getWorkspace().toInternalAPI().save();
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.dialogs.login.ILoginDialogController#getUsersession()
	 */
	public ESUsersession getUsersession() {
		return usersession;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.dialogs.login.ILoginDialogController#getServer()
	 */
	public ESServer getServer() {
		if (server != null) {
			return server;
		}
		return usersession.getServer();
	}

	/**
	 * Perform a login using an {@link ESUsersession} that can be determined with
	 * the given {@link ESServer}.
	 *
	 *
	 * @param server
	 *            the server info to be used in order to determine a valid
	 *            usersession
	 * @param force
	 *            whether to force requesting the password
	 * @return a logged-in usersession
	 * @throws ESException
	 *             in case the login fails
	 */
	public ESUsersession login(ESServer server, boolean force)
		throws ESException {
		this.server = server;
		usersession = null;
		return login(force);
	}

	/**
	 * Perform a login using the given {@link ESUsersession}.
	 *
	 * @param usersession
	 *            the usersession to be used during login
	 * @param force
	 *            whether to force requesting the password
	 * @throws ESException
	 *             in case the login fails
	 */
	public void login(ESUsersession usersession, boolean force)
		throws ESException {
		server = null;
		this.usersession = usersession;
		login(force);
	}

	/**
	 * Perform a login using an {@link ESUsersession} that can be determined with
	 * the given {@link ESServer}.
	 *
	 *
	 * @param server
	 *            the server info to be used in order to determine a valid
	 *            usersession
	 * @return a logged-in usersession
	 * @throws ESException
	 *             in case the login fails
	 */
	public ESUsersession login(ESServer server) throws ESException {
		this.server = server;
		usersession = null;
		return login(false);
	}

	/**
	 * Perform a login using the given {@link ESUsersession}.
	 *
	 * @param usersession
	 *            the usersession to be used during login
	 * @return usersession
	 * @throws ESException
	 *             in case the login fails
	 */
	public ESUsersession login(ESUsersession usersession) throws ESException {
		this.usersession = usersession;
		server = usersession.getServer();
		return login(false);
	}
}