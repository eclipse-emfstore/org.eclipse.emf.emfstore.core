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
 * Edgar Mueller - refactorings
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.accesscontrol.authentication.verifiers;

import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.AccessControl;
import org.eclipse.emf.emfstore.internal.server.connection.ServerKeyStoreManager;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.exceptions.ClientVersionOutOfDateException;
import org.eclipse.emf.emfstore.internal.server.exceptions.ServerKeyStoreException;
import org.eclipse.emf.emfstore.internal.server.model.AuthenticationInformation;
import org.eclipse.emf.emfstore.internal.server.model.ModelFactory;
import org.eclipse.emf.emfstore.server.auth.ESPasswordHashGenerator;
import org.eclipse.emf.emfstore.server.auth.ESUserVerifier;
import org.eclipse.emf.emfstore.server.model.ESClientVersionInfo;

/**
 * Abstract class for authentication.
 *
 * @author wesendonk
 */
public abstract class PasswordVerifier implements ESUserVerifier {

	private final String superuser;
	private final String hash;
	private final String salt;

	/**
	 * Default constructor.
	 */
	public PasswordVerifier() {
		superuser = ServerConfiguration.getProperties().getProperty(ServerConfiguration.SUPER_USER,
			ServerConfiguration.SUPER_USER_DEFAULT);
		hash = ServerConfiguration.getProperties().getProperty(ServerConfiguration.SUPER_USER_PASSWORD_HASH);
		salt = ServerConfiguration.getProperties().getProperty(ServerConfiguration.SUPER_USER_PASSWORD_SALT);
	}

	/**
	 * Creates a new {@link AuthenticationInformation} with a session ID set.
	 *
	 * @return a new instance of an {@link AuthenticationInformation} with an already
	 *         set session ID
	 */
	protected AuthenticationInformation createAuthenticationInfo() {
		final AuthenticationInformation authenticationInformation = ModelFactory.eINSTANCE
			.createAuthenticationInformation();
		authenticationInformation.setSessionId(ModelFactory.eINSTANCE.createSessionId());
		return authenticationInformation;
	}

	/**
	 * Prepares password before it is used for authentication. Normally this includes decrypting the password
	 *
	 * @param password password
	 * @return prepared password
	 * @throws ServerKeyStoreException in case of an exception
	 */
	protected String preparePassword(String password) throws ServerKeyStoreException {
		return ServerKeyStoreManager.getInstance().decrypt(password);

	}

	/**
	 * Check user name and password against superuser.
	 *
	 * @param username user name
	 * @param password password
	 * @return true if super user
	 */
	protected boolean verifySuperUser(String username, String password) {
		final ESPasswordHashGenerator passwordHashGenerator = AccessControl.getESPasswordHashGenerator();
		if (hash == null && salt == null) {
			return false;
		}
		return username.equals(superuser) && passwordHashGenerator.verifyPassword(password, hash, salt);
	}

	/**
	 * This method must be implemented by subclasses in order to verify a pair of username and password.
	 * When using authentication you should use {@link ESUserVerifier#logIn(String, String, ESClientVersionInfo)} in
	 * order to gain a session id.
	 *
	 * @param username
	 *            the user name as entered by the client; may differ from the user name of the {@code resolvedUser}
	 * @param password
	 *            the password as entered by the client
	 * @return boolean {@code true} if authentication was successful, {@code false} if not
	 * @throws AccessControlException
	 *             if an exception occurs during the verification process
	 */
	protected abstract boolean verifyPassword(String username, String password) throws AccessControlException;

	/**
	 * Checks whether the given client version is valid.
	 * If not, throws an exception
	 *
	 * @param clientVersionInfo
	 *            the client version to be checked
	 * @throws ClientVersionOutOfDateException
	 *             in case the given client version is not valid
	 */
	protected void checkClientVersion(ESClientVersionInfo clientVersionInfo) throws ClientVersionOutOfDateException {
		VersionVerifier.verify(
			ServerConfiguration.getSplittedProperty(ServerConfiguration.ACCEPTED_VERSIONS),
			org.eclipse.emf.emfstore.internal.server.model.impl.api.ESClientVersionInfoImpl.class.cast(
				clientVersionInfo).toInternalAPI());
	}
}
