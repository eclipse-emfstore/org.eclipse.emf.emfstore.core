/*******************************************************************************
 * Copyright (c) 2008-2015 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto von Wesendonk - initial API and implementation
 * Edgar Mueller - Bug 482407
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.connectionmanager;

import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.emfstore.client.ESUsersession;
import org.eclipse.emf.emfstore.client.sessionprovider.ESAbstractSessionProvider;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionElement;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.Usersession;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESServerCallImpl;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESUsersessionImpl;
import org.eclipse.emf.emfstore.internal.client.model.util.WorkspaceUtil;
import org.eclipse.emf.emfstore.internal.common.ESRunnableWrapperProvider;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.exceptions.SessionTimedOutException;
import org.eclipse.emf.emfstore.internal.server.exceptions.UnknownSessionException;
import org.eclipse.emf.emfstore.server.exceptions.ESException;

/**
 * Handles session management during the execution of a {@link ServerCall}.
 *
 * @author wesendon
 */
public class SessionManager {

	private static final String CLASS = "class"; //$NON-NLS-1$
	private static final String PRIORITY = "priority"; //$NON-NLS-1$
	private static final String ORG_ECLIPSE_EMF_EMFSTORE_CLIENT_USERSESSION_PROVIDER = "org.eclipse.emf.emfstore.client.usersessionProvider"; //$NON-NLS-1$
	private static final String USERSESSION_MUST_NOT_BE_NULL = "Usersession must not be null."; //$NON-NLS-1$
	private ESAbstractSessionProvider provider;

	/**
	 * Constructor.
	 */
	public SessionManager() {
		initSessionProvider();
	}

	/**
	 * Executes the given {@link ServerCall}.
	 *
	 * @param <T>
	 *            type of the result the server call is providing
	 *
	 * @param serverCall
	 *            the server call to be executed
	 * @throws ESException
	 *             If an error occurs during execution of the server call
	 */
	public <T> void execute(ServerCall<T> serverCall) throws ESException {
		final ESUsersessionImpl session = (ESUsersessionImpl) getSessionProvider().provideUsersession(
			new ESServerCallImpl<T>(serverCall));
		serverCall.setUsersession(session.toInternalAPI());
		final Usersession loggedInSession = loginUsersession(session.toInternalAPI(), false);
		executeCall(serverCall, loggedInSession, true);
	}

	/**
	 * Tries to login the given {@link Usersession}.<br/>
	 * If the given session is not logged in or the <code>forceLogin</code> parameter is set
	 * to true, the session object first tries to login itself. If the login fails,
	 * the {@link SessionProvider} retrieved by {@link #getSessionProvider()} is asked to
	 * login the session by calling {@link SessionProvider#loginSession(Usersession)}.
	 *
	 * @param usersession
	 *            The user session to be logged
	 * @param forceLogin
	 *            Whether the login should be forced, i.e. the login is performed even in case the
	 *            given user session is already logged in.
	 * @throws ESException
	 *             In case
	 */
	private Usersession loginUsersession(final Usersession usersession, boolean forceLogin)
		throws ESException {
		if (usersession == null) {
			// TODO create exception
			throw new RuntimeException(USERSESSION_MUST_NOT_BE_NULL);
		}
		if (!isLoggedIn(usersession) || forceLogin) {
			if (!(usersession.getUsername() == null
				|| usersession.getUsername().equals(StringUtils.EMPTY))
				&& usersession.getPassword() != null) {
				try {
					// if login fails, let the session provider handle the rest
					RunESCommand.WithException.run(ESException.class, new Callable<Void>() {
						public Void call() throws Exception {
							usersession.logIn();
							return null;
						}
					});
					return usersession;
				} catch (final ESException e) {
					// ignore, session provider should try to login
				}
			}
			// TODO: ugly
			final ESUsersession session = RunESCommand.WithException.runWithResult(ESException.class,
				new Callable<ESUsersession>() {
					public ESUsersession call() throws Exception {
						return getSessionProvider().login(usersession.toAPI());
					}
				});
			return ((ESUsersessionImpl) session).toInternalAPI();
		}

		// having isLoggedIn return true does not necessarily mean we have a
		// session representation on the server side, which always must be the case
		ESRunnableWrapperProvider.getInstance().embedInContext(new Runnable() {

			public void run() {
				RunESCommand.run(new Callable<Void>() {
					public Void call() throws Exception {
						try {
							usersession.logIn();
						} catch (final AccessControlException ex) {
							WorkspaceUtil.logException(ex.getMessage(), ex);
						} catch (final ESException ex) {
							WorkspaceUtil.logException(ex.getMessage(), ex);
						}
						return null;
					}
				});
			}
		}).run();

		return usersession;
	}

	private boolean isLoggedIn(Usersession usersession) {
		final ConnectionManager connectionManager = ESWorkspaceProviderImpl.getInstance().getConnectionManager();
		return usersession.isLoggedIn() && connectionManager.isLoggedIn(usersession.getSessionId());
	}

	private void executeCall(ServerCall<?> serverCall, Usersession usersession, boolean retry) throws ESException {
		try {
			serverCall.run(usersession.getSessionId());
		} catch (final ESException e) {
			if (retry && (e instanceof SessionTimedOutException || e instanceof UnknownSessionException)) {
				// login & retry
				final Usersession loginUsersession = loginUsersession(usersession, true);
				executeCall(serverCall, loginUsersession, false);
			} else {
				throw e;
			}
		}
	}

	/**
	 * Sets the {@link ESAbstractSessionProvider} to be used by this session manager.
	 *
	 * @param sessionProvider
	 *            the session provider to be used
	 */
	public void setSessionProvider(ESAbstractSessionProvider sessionProvider) {
		provider = sessionProvider;
	}

	/**
	 * Returns the {@link ESAbstractSessionProvider} in use by this session manager.
	 *
	 * @return the session provider in use
	 */
	public ESAbstractSessionProvider getSessionProvider() {
		return provider;
	}

	private void initSessionProvider() {
		final ESExtensionPoint extensionPoint = new ESExtensionPoint(
			ORG_ECLIPSE_EMF_EMFSTORE_CLIENT_USERSESSION_PROVIDER);

		if (extensionPoint.getExtensionElements().size() > 0) {
			final ESExtensionElement highestExtension = analyseUsersessionExtensionPoint(extensionPoint);
			final ESAbstractSessionProvider sessionProvider = highestExtension.getClass(CLASS,
				ESAbstractSessionProvider.class);
			if (sessionProvider != null) {
				provider = sessionProvider;
			}
		} else {
			provider = new BasicSessionProvider();
		}
	}

	/**
	 * Parses the given extension point and returns the highest ranked extension element.
	 *
	 * @param extensionPoint the extension point
	 * @return the element or <code>null</code> if no extension elements.
	 */
	public static ESExtensionElement analyseUsersessionExtensionPoint(final ESExtensionPoint extensionPoint) {
		int highestPrio = -1;
		ESExtensionElement highestExtension = null;
		for (final ESExtensionElement esExtensionElement : extensionPoint.getExtensionElements()) {
			Integer prio = esExtensionElement.getInteger(PRIORITY);
			if (prio == null) {
				prio = 0;
			}
			if (prio > highestPrio) {
				highestPrio = prio;
				highestExtension = esExtensionElement;
			}
		}
		return highestExtension;
	}
}
