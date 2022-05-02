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
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.eclipse.emf.emfstore.client.test.common.builders.BOOL.TRUE;
import org.eclipse.emf.emfstore.client.test.common.builders.UserBuilder;
import org.eclipse.emf.emfstore.client.test.common.cases.ESTestWithLoggedInUserMock;
import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.client.test.common.mocks.ConnectionMock;
import org.eclipse.emf.emfstore.internal.client.configuration.VersioningInfo;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.connectionmanager.KeyStoreManager;
import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.LoginService;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.model.ESUser;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author emueller
 *
 */
public class LoginServiceTest extends ESTestWithLoggedInUserMock {

	private static final String USER_HANS = "UserHans"; //$NON-NLS-1$
	private static final String GOOD_PASSWORD = "GoodPassword"; //$NON-NLS-1$
	private static final String BAD_PASSWORD = "BadPassword"; //$NON-NLS-1$

	private LoginService loginService;

	@BeforeClass
	public static void beforeClass() {
		startEMFStore(Collections.singletonMap(ServerConfiguration.AUTHENTICATION_LOGIN_DELAY_FAILED_REQUESTS, "200")); //$NON-NLS-1$
	}

	@AfterClass
	public static void afterClass() {
		stopEMFStore();
	}

	@Before
	public void createUserIfNotExisting() throws ESException {
		super.before();

		final ConnectionMock mock = (ConnectionMock) ESWorkspaceProviderImpl.getInstance().getConnectionManager();
		loginService = mock.getAccessControl().getLoginService();
		for (final ESUser esUser : mock.getAccessControl().getOrgUnitProviderService().getUsers()) {
			if (USER_HANS.equals(esUser.getName())) {
				return;
			}
		}

		final UserBuilder<TRUE, TRUE, TRUE> user = UserBuilder.create()
			.withName(USER_HANS)
			.withPassword(GOOD_PASSWORD)
			.onServer(getServer());
		Create.user(user);

	}

	@Test
	public void verifyUser() {
		final String password = KeyStoreManager.getInstance().encrypt("super", getServerInfo()); //$NON-NLS-1$
		final boolean isValid = loginService.verifyUser("super", password, //$NON-NLS-1$
			new VersioningInfo().getClientVersion().toAPI());
		assertTrue(isValid);
	}

	@Test
	public void verifyUserDelayReached() throws ESException, InterruptedException {
		/* setup */
		final String badPassword = KeyStoreManager.getInstance().encrypt(BAD_PASSWORD, getServerInfo());
		final String goodPassword = KeyStoreManager.getInstance().encrypt(GOOD_PASSWORD, getServerInfo());

		/* act */
		assertFalse(loginService.verifyUser(USER_HANS, badPassword, new VersioningInfo().getClientVersion().toAPI()));
		assertFalse(loginService.verifyUser(USER_HANS, badPassword, new VersioningInfo().getClientVersion().toAPI()));
		assertFalse(loginService.verifyUser(USER_HANS, badPassword, new VersioningInfo().getClientVersion().toAPI()));
		assertFalse(loginService.verifyUser(USER_HANS, badPassword, new VersioningInfo().getClientVersion().toAPI()));
		assertFalse(loginService.verifyUser(USER_HANS, badPassword, new VersioningInfo().getClientVersion().toAPI()));

		/* five bad attempts, good should not work with waiting */
		assertFalse(loginService.verifyUser(USER_HANS, goodPassword, new VersioningInfo().getClientVersion().toAPI()));

		/* works again after delay */
		Thread.sleep(200);
		assertTrue(loginService.verifyUser(USER_HANS, goodPassword, new VersioningInfo().getClientVersion().toAPI()));
	}

	@Test
	public void verifyUserDelayNotReached() throws ESException, InterruptedException {
		/* setup */
		final String badPassword = KeyStoreManager.getInstance().encrypt(BAD_PASSWORD, getServerInfo());
		final String goodPassword = KeyStoreManager.getInstance().encrypt(GOOD_PASSWORD, getServerInfo());

		/* act */
		assertFalse(loginService.verifyUser(USER_HANS, badPassword, new VersioningInfo().getClientVersion().toAPI()));
		assertFalse(loginService.verifyUser(USER_HANS, badPassword, new VersioningInfo().getClientVersion().toAPI()));
		assertFalse(loginService.verifyUser(USER_HANS, badPassword, new VersioningInfo().getClientVersion().toAPI()));
		assertFalse(loginService.verifyUser(USER_HANS, badPassword, new VersioningInfo().getClientVersion().toAPI()));
		assertTrue(loginService.verifyUser(USER_HANS, goodPassword, new VersioningInfo().getClientVersion().toAPI()));
	}

	private boolean login(final String badPassword) {
		try {
			loginService.logIn(USER_HANS, badPassword, new VersioningInfo().getClientVersion().toAPI());
		} catch (final AccessControlException ex) {
			return false;
		}
		return true;
	}

	@Test
	public void loginUserDelayReached() throws InterruptedException {
		/* setup */
		final String badPassword = KeyStoreManager.getInstance().encrypt(BAD_PASSWORD, getServerInfo());
		final String goodPassword = KeyStoreManager.getInstance().encrypt(GOOD_PASSWORD, getServerInfo());

		/* act */
		assertFalse(login(badPassword));
		assertFalse(login(badPassword));
		assertFalse(login(badPassword));
		assertFalse(login(badPassword));
		assertFalse(login(badPassword));

		/* five bad attempts, good should not work with waiting */
		assertFalse(login(goodPassword));

		/* works again after delay */
		Thread.sleep(200);
		assertTrue(login(goodPassword));
	}

	@Test
	public void loginUserDelayNotReached() throws InterruptedException {
		/* setup */
		final String badPassword = KeyStoreManager.getInstance().encrypt(BAD_PASSWORD, getServerInfo());
		final String goodPassword = KeyStoreManager.getInstance().encrypt(GOOD_PASSWORD, getServerInfo());

		/* act */
		assertFalse(login(badPassword));
		assertFalse(login(badPassword));
		assertFalse(login(badPassword));
		assertFalse(login(badPassword));
		assertTrue(login(goodPassword));
	}

	@Test
	public void loginUserDelayNotReachedTimeoutBetweenBadRequests() throws InterruptedException {
		/* setup */
		final String badPassword = KeyStoreManager.getInstance().encrypt(BAD_PASSWORD, getServerInfo());
		final String goodPassword = KeyStoreManager.getInstance().encrypt(GOOD_PASSWORD, getServerInfo());

		/* act */
		assertFalse(login(badPassword));
		Thread.sleep(200);
		assertFalse(login(badPassword));
		assertFalse(login(badPassword));
		assertFalse(login(badPassword));
		assertFalse(login(badPassword));
		assertTrue(login(goodPassword));
	}
}
