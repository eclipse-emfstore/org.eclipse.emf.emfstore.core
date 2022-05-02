/*******************************************************************************
 * Copyright (c) 2011-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.emf.emfstore.client.ESUsersession;
import org.eclipse.emf.emfstore.client.test.common.cases.ESTestWithLoggedInUserMock;
import org.eclipse.emf.emfstore.client.test.common.dsl.Roles;
import org.eclipse.emf.emfstore.client.test.common.util.ServerUtil;
import org.eclipse.emf.emfstore.internal.client.model.AdminBroker;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.impl.AdminBrokerImpl;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESServerImpl;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESUsersessionImpl;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.exceptions.ConnectionException;
import org.eclipse.emf.emfstore.internal.server.exceptions.InvalidInputException;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public class AdminBrokerTests extends ESTestWithLoggedInUserMock {

	private static final String SUPER_USER_NAME = "super"; //$NON-NLS-1$
	private static final String GROUP_NAME = "grp"; //$NON-NLS-1$
	private static final String GROUP_NAME_2 = "grp2"; //$NON-NLS-1$
	private static final String USER_NAME = "quux"; //$NON-NLS-1$
	private static final String USER_NAME_2 = "hoof"; //$NON-NLS-1$

	private static AdminBroker adminBroker;

	@BeforeClass
	public static void beforeClass() {
		startEMFStore();
	}

	public static void afterClass() {
		stopEMFStore();
	}

	@Override
	@Before
	public void before() {
		super.before();
		try {
			adminBroker = new AdminBrokerImpl(
				((ESServerImpl) getServer()).toInternalAPI(),
				((ESUsersessionImpl) getUsersession()).toInternalAPI().getSessionId());
		} catch (final ConnectionException ex) {
			fail(ex.getMessage());
		}
	}

	@Override
	@After
	public void after() {
		ACUser user;
		try {
			user = ServerUtil.getUser(getUsersession(), USER_NAME);
			if (user != null) {
				ESWorkspaceProviderImpl.getInstance().getAdminConnectionManager().deleteUser(
					((ESUsersessionImpl) getUsersession()).toInternalAPI().getSessionId(),
					user.getId());
			}
		} catch (final ESException ex) {
			fail(ex.getMessage());
		}
		super.after();
	}

	@Test
	public void testCreateUser() throws ESException {
		final int initialSize = adminBroker.getUsers().size();
		adminBroker.createUser(USER_NAME);
		assertEquals(initialSize + 1, adminBroker.getUsers().size());
		assertEquals(findUser(SUPER_USER_NAME).getId().getId(), findUser(USER_NAME).getCreatedBy());
	}

	@Test
	public void testDeleteUser() throws ESException {
		adminBroker.createUser(USER_NAME);
		final int initialSize = adminBroker.getUsers().size();
		final ACUser userToDelete = findUser(USER_NAME);
		adminBroker.deleteUser(userToDelete.getId());
		assertEquals(initialSize - 1, adminBroker.getUsers().size());
	}

	@Test
	public void testDeleteUserWithCreatedByUser() throws ESException {
		/* setup */
		adminBroker.createUser(USER_NAME);
		final ACUser createdUser1 = findUser(USER_NAME);
		adminBroker.assignRole(createdUser1.getId(), Roles.serverAdmin());
		adminBroker.changeUser(createdUser1.getId(), createdUser1.getName(), createdUser1.getName());

		final ESUsersession login = getServer().login(createdUser1.getName(), createdUser1.getName());
		final AdminBroker adminBroker2 = new AdminBrokerImpl(
			((ESServerImpl) getServer()).toInternalAPI(),
			((ESUsersessionImpl) login).toInternalAPI().getSessionId());

		adminBroker2.createUser(USER_NAME_2);
		final int initialSize = adminBroker.getUsers().size();
		assertEquals(createdUser1.getId().getId(), findUser(USER_NAME_2).getCreatedBy());

		/* act */
		adminBroker2.deleteUser(createdUser1.getId());

		/* assert */
		assertEquals(initialSize - 1, adminBroker.getUsers().size());
		assertNull(findUser(USER_NAME_2).getCreatedBy());
	}

	private ACUser findUser(String name) throws ESException {
		for (final ACUser user : adminBroker.getUsers()) {
			if (user.getName().equals(name)) {
				return user;
			}
		}
		return null;
	}

	private ACGroup findGroup(String name) throws ESException {
		for (final ACGroup group : adminBroker.getGroups()) {
			if (group.getName().equals(name)) {
				return group;
			}
		}
		return null;
	}

	@Test(expected = AccessControlException.class)
	public void testLoginOfCreatedUserWithNoPasswordSet() throws ESException {
		adminBroker.createUser(USER_NAME);
		ACUser user = null;
		for (final ACUser u : adminBroker.getUsers()) {
			if (u.getName().equals(USER_NAME)) {
				user = u;
			}
		}
		final ESUsersession login2 = getServer().login(user.getName(), user.getPassword());
		assertTrue(login2.isLoggedIn());
	}

	// @Test
	// public void testLoginOfCreatedUserWithSetPassword() throws ESException {
	// final ACOrgUnitId createdUserId = adminBroker.createUser(USER_NAME);
	// ACUser user = null;
	// for (final ACUser u : adminBroker.getUsers()) {
	// if (u.getName().equals(USER_NAME)) {
	// user = u;
	// }
	// }
	// user.setPassword(USER_PW);
	// ESWorkspaceProviderImpl.getInstance().getAdminConnectionManager().changeUser(
	// ((ESSessionIdImpl) login.getSessionId()).toInternalAPI(),
	// createdUserId, USER_NAME, USER_PW);
	// final ESUsersession login2 = server.login(user.getName(), user.getPassword());
	// assertTrue(login2.isLoggedIn());
	// }

	@Test
	public void testCreateGroup() throws ESException {
		adminBroker.createGroup(GROUP_NAME);
		assertEquals(1, adminBroker.getGroups().size());
		assertEquals(findUser(SUPER_USER_NAME).getId().getId(), findGroup(GROUP_NAME).getCreatedBy());
	}

	@Test
	public void testDeleteGroup() throws ESException {
		final ACOrgUnitId groupId = adminBroker.createGroup(GROUP_NAME);
		adminBroker.deleteGroup(groupId);
		assertEquals(0, adminBroker.getGroups().size());
	}

	@Test
	public void testDeleteUserWithCreatedByGroup() throws ESException {
		/* setup */
		adminBroker.createUser(USER_NAME);
		final ACUser createdUser1 = findUser(USER_NAME);
		adminBroker.assignRole(createdUser1.getId(), Roles.serverAdmin());
		adminBroker.changeUser(createdUser1.getId(), createdUser1.getName(), createdUser1.getName());

		final ESUsersession login = getServer().login(createdUser1.getName(), createdUser1.getName());
		final AdminBroker adminBroker2 = new AdminBrokerImpl(
			((ESServerImpl) getServer()).toInternalAPI(),
			((ESUsersessionImpl) login).toInternalAPI().getSessionId());

		adminBroker2.createGroup(GROUP_NAME_2);
		assertEquals(createdUser1.getId().getId(), findGroup(GROUP_NAME_2).getCreatedBy());

		/* act */
		adminBroker2.deleteUser(createdUser1.getId());

		/* assert */
		assertNull(findGroup(GROUP_NAME_2).getCreatedBy());
	}

	@Test(expected = InvalidInputException.class)
	public void testCreateGroupWithAlreadyExistingName() throws ESException {
		adminBroker.createGroup(GROUP_NAME);
		adminBroker.createGroup(GROUP_NAME);
	}
}
