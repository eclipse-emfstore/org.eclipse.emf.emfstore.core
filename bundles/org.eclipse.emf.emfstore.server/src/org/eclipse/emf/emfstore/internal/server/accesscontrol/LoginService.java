/*******************************************************************************
 * Copyright (c) 2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 * Johannes Faltermeier - add delay on multiple failed login attempts for user
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.accesscontrol;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionElement;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPointException;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.Activator;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.authentication.ESUserVerifierFactory;
import org.eclipse.emf.emfstore.internal.server.core.MonitorProvider;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.exceptions.FatalESException;
import org.eclipse.emf.emfstore.internal.server.model.AuthenticationInformation;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESAuthenticationInformationImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESSessionIdImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESUserImpl;
import org.eclipse.emf.emfstore.server.auth.ESAuthenticationControlType;
import org.eclipse.emf.emfstore.server.auth.ESOrgUnitResolver;
import org.eclipse.emf.emfstore.server.auth.ESSessions;
import org.eclipse.emf.emfstore.server.auth.ESUserVerifier;
import org.eclipse.emf.emfstore.server.model.ESAuthenticationInformation;
import org.eclipse.emf.emfstore.server.model.ESClientVersionInfo;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider;
import org.eclipse.emf.emfstore.server.model.ESSessionId;

import com.google.common.base.Optional;

/**
 * Service for logging users out and in. The user verification step is customizable via an extension point.
 *
 * @author emueller
 *
 */
public class LoginService {

	private static final String USER_VERIFIER_SERVICE_CLASS = "userVerifierServiceClass"; //$NON-NLS-1$
	private static final String MONITOR_NAME = "authentication"; //$NON-NLS-1$
	private static final String ACCESSCONTROL_EXTENSION_ID = "org.eclipse.emf.emfstore.server.accessControl"; //$NON-NLS-1$

	private final EMFStoreSessions sessions;
	private final ESOrgUnitResolver orgUnitResolver;
	private final ESOrgUnitProvider orgUnitProvider;
	private final ESAuthenticationControlType authenticationControlType;
	private ESUserVerifier userVerifier;
	private final VerifyRequestManager verifyRequestManager;
	private final int delay;

	/**
	 * Constructor.
	 *
	 * @param authenticationControlType
	 *            the desired type authentication control type
	 * @param sessions
	 *            a {@link ESSessions} map that will be updated in case an user successfully logins
	 * @param orgUnitProvider
	 *            an {@link ESOrgUnitProvider} that provides access to all organizational units known by EMFStore.
	 *            This may be consumed by an {@link ESUserVerifier} but boesn't necessarily needs to happen.
	 *            It's absolutely fine if {@link ESUserVerifier} ignore the provider.
	 * @param orgUnitResolver
	 *            an {@link ESOrgUnitResolver} for resolving any roles and groups on a given organizational unit
	 * @param delay
	 *            if multiple failed requests have occurred in intervals shorter than this delay, any further requests
	 *            will be immediately declined until the delay is reached. If <code>-1</code> is passed, this feature
	 *            will be disabled.
	 */
	public LoginService(
		ESAuthenticationControlType authenticationControlType,
		EMFStoreSessions sessions,
		ESOrgUnitProvider orgUnitProvider,
		ESOrgUnitResolver orgUnitResolver,
		int delay) {

		this.authenticationControlType = authenticationControlType;
		this.sessions = sessions;
		this.orgUnitProvider = orgUnitProvider;
		this.orgUnitResolver = orgUnitResolver;
		this.delay = delay;
		verifyRequestManager = new VerifyRequestManager(delay);
	}

	private ESUserVerifier initUserVerifierService() {

		try {
			final ESExtensionPoint ext = new ESExtensionPoint(ACCESSCONTROL_EXTENSION_ID, false);
			final List<ESExtensionElement> elements = ext.getExtensionElements();
			ESUserVerifier verifier = null;
			for (final ESExtensionElement el : elements) {
				verifier = el.getClass(
					USER_VERIFIER_SERVICE_CLASS, ESUserVerifier.class);
				if (verifier != null) {
					verifier.init(orgUnitProvider);
					return verifier;
				}
			}

			return ESUserVerifierFactory.getInstance().createUserVerifier(
				authenticationControlType,
				orgUnitProvider);
		} catch (final ESExtensionPointException ex) {
			ModelUtil.logException(Messages.LoginService_CustomLoginServiceInitFailed, ex);
			return null;
		} catch (final FatalESException ex) {
			ModelUtil.logException(Messages.LoginService_CustomLoginServiceInitFailed, ex);
			return null;
		}
	}

	/**
	 * Login the given user with the provided password and client version.
	 *
	 * @param username
	 *            the name of the user to be logged in
	 * @param password
	 *            the password of the user
	 * @param clientVersionInfo
	 *            the client version of the user
	 * @return an {@link ESAuthenticationInformation} in case the login was successfull
	 * @throws AccessControlException in case user verification fails
	 */
	public ESAuthenticationInformation logIn(String username, String password,
		ESClientVersionInfo clientVersionInfo)
		throws AccessControlException {

		synchronized (MonitorProvider.getInstance().getMonitor(MONITOR_NAME)) {
			final Optional<ESAuthenticationInformation> information = doVerifyUser(username, password,
				clientVersionInfo);
			if (!information.isPresent()) {
				throw new AccessControlException(
					MessageFormat.format(Messages.LoginService_VerifyUserTooManyFailedRequests, username, delay));
			}

			final ESAuthenticationInformation authInfo = information.get();

			final AuthenticationInformation authenticationInformation = ESAuthenticationInformationImpl.class.cast(
				authInfo).toInternalAPI();

			final ESSessionId existingSession = sessions.resolveByUser(authInfo.getUser());

			if (existingSession == null) {
				sessions.add(authInfo);
			} else {
				authenticationInformation.setSessionId(
					ESSessionIdImpl.class.cast(existingSession).toInternalAPI());
			}

			final ACUser resolvedUser = (ACUser) ESUserImpl.class.cast(
				orgUnitResolver.resolveRoles(authInfo))
				.toInternalAPI();

			authenticationInformation.setResolvedACUser(resolvedUser);
			return authInfo;
		}
	}

	private ESUserVerifier getUserVerifierService() {
		if (userVerifier == null) {
			userVerifier = initUserVerifierService();
		}
		return userVerifier;
	}

	/**
	 * Logout the given session.
	 *
	 * @param sessionId
	 *            the ID of the session that should be logged out
	 * @throws AccessControlException in case the the session ID is null
	 */
	public void logout(ESSessionId sessionId) throws AccessControlException {
		synchronized (MonitorProvider.getInstance().getMonitor(MONITOR_NAME)) {
			if (sessionId == null) {
				throw new AccessControlException(Messages.AccessControlImpl_SessionID_Is_Null);
			}
			sessions.remove(sessionId);
		}
	}

	/**
	 * Checks whether the given credentials are valid without logging the client in.
	 *
	 * @param username
	 *            the user name
	 * @param password
	 *            the encrypted password of the user
	 * @param clientVersionInfo
	 *            the client's version
	 * @return {@code true}, if the user's credentials are valid, {@code false} otherwise
	 */
	public boolean verifyUser(String username, String password, ESClientVersionInfo clientVersionInfo) {
		try {
			final Optional<ESAuthenticationInformation> information = doVerifyUser(username, password,
				clientVersionInfo);
			if (!information.isPresent()) {
				/* too many bad attempts, otherwise we get an exception */
				logWarning(
					MessageFormat.format(Messages.LoginService_VerifyUserTooManyFailedRequests, username, delay));
				return false;
			}
			return true;
		} catch (final AccessControlException ex) {
			/* regular bad attempt */
			return false;
		}
	}

	/**
	 * @return if the information is absent, the verify request was not attempted, because there are too many failed
	 *         requests recorded.
	 * @throws in case of a failed attempt
	 */
	private Optional<ESAuthenticationInformation> doVerifyUser(String username, String password,
		ESClientVersionInfo clientVersionInfo) throws AccessControlException {
		try {
			if (verifyRequestManager.checkTooManyFailedRequests(username)) {
				return Optional.absent();
			}
			final ESAuthenticationInformation information = getUserVerifierService().verifyUser(
				username,
				password,
				clientVersionInfo);
			verifyRequestManager.cleanupFailedAttempts(username);
			return Optional.of(information);
		} catch (final AccessControlException e) {
			verifyRequestManager.recordFailedVerifyUserAttempt(username);
			throw e;
		}
	}

	private void logWarning(String message) {
		Activator.getDefault().logWarning(message);
	}
}
