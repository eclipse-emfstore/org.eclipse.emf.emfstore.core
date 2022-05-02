/*******************************************************************************
 * Copyright (c) 2011-2019 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.accesscontrol.test;

import static org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil.share;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.emfstore.client.ESUsersession;
import org.eclipse.emf.emfstore.client.test.common.dsl.Roles;
import org.eclipse.emf.emfstore.client.test.common.util.ServerUtil;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.RolesPackage;
import org.eclipse.emf.emfstore.server.auth.ESProjectAdminPrivileges;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AdminEMFStoreProjectAdminTests extends ProjectAdminTest {

	private static final String UNRELATED_GROUP = "Unrelated Group"; //$NON-NLS-1$
	private static final String READ_GROUP = "Read Group"; //$NON-NLS-1$
	private static final String WRITE_GROUP = "Write Group"; //$NON-NLS-1$
	private static final String CREATED_GROUP = "Created Group"; //$NON-NLS-1$

	private static final String SERVER_ADMIN = "Server Admin"; //$NON-NLS-1$
	private static final String UNRELATED_GROUP_USER = "Unrelated Group User"; //$NON-NLS-1$
	private static final String READ_GROUP_USER = "Read Group User"; //$NON-NLS-1$
	private static final String WRITE_GROUP_USER = "Write Group User"; //$NON-NLS-1$
	private static final String CREATED_GROUP_USER = "Created Group User"; //$NON-NLS-1$

	private static final String UNRELATED_USER = "Unrelated User"; //$NON-NLS-1$
	private static final String READ_USER = "Read User"; //$NON-NLS-1$
	private static final String WRITE_USER = "Write User"; //$NON-NLS-1$
	private static final String CREATED_USER = "Created User"; //$NON-NLS-1$

	private ACOrgUnitId unrelatedGroupId;
	private ACOrgUnitId readGroupId;
	private ACOrgUnitId writeGroupId;
	private ACOrgUnitId createdGroupId;

	private ACOrgUnitId unrelatedGroupUserId;
	private ACOrgUnitId readGroupUserId;
	private ACOrgUnitId writeGroupUserId;
	private ACOrgUnitId createdGroupUserId;

	private ACOrgUnitId unrelatedUserId;
	private ACOrgUnitId readUserId;
	private ACOrgUnitId writeUserId;
	private ACOrgUnitId createdUserId;

	private ACOrgUnitId serverAdminId;

	@BeforeClass
	public static void beforeClass() {
		startEMFStoreWithPAProperties(ESProjectAdminPrivileges.ShareProject,
			ESProjectAdminPrivileges.AssignRoleToOrgUnit, // needed for share
			ESProjectAdminPrivileges.ChangeAssignmentsOfOrgUnits,
			ESProjectAdminPrivileges.CreateGroup,
			ESProjectAdminPrivileges.CreateUser,
			ESProjectAdminPrivileges.DeleteOrgUnit);
	}

	@Override
	@Before
	public void before() {
		super.before();
		try {
			makeUserPA();
			share(getSuperUsersession(), getLocalProject());
			getSuperAdminBroker().changeRole(getProjectSpace().getProjectId(),
				getId(getSuperUsersession(), getUser()),
				RolesPackage.eINSTANCE.getProjectAdminRole());

			unrelatedGroupId = getSuperAdminBroker().createGroup(UNRELATED_GROUP);
			readGroupId = getSuperAdminBroker().createGroup(READ_GROUP);
			writeGroupId = getSuperAdminBroker().createGroup(WRITE_GROUP);
			createdGroupId = getAdminBroker().createGroup(CREATED_GROUP); // admin broker

			serverAdminId = getSuperAdminBroker().createUser(SERVER_ADMIN);
			unrelatedGroupUserId = getSuperAdminBroker().createUser(UNRELATED_GROUP_USER);
			readGroupUserId = getSuperAdminBroker().createUser(READ_GROUP_USER);
			writeGroupUserId = getSuperAdminBroker().createUser(WRITE_GROUP_USER);
			createdGroupUserId = getSuperAdminBroker().createUser(CREATED_GROUP_USER);

			unrelatedUserId = getSuperAdminBroker().createUser(UNRELATED_USER);
			readUserId = getSuperAdminBroker().createUser(READ_USER);
			writeUserId = getSuperAdminBroker().createUser(WRITE_USER);
			createdUserId = getAdminBroker().createUser(CREATED_USER); // admin broker

			getSuperAdminBroker().addMember(unrelatedGroupId, unrelatedGroupUserId);
			getSuperAdminBroker().addMember(readGroupId, readGroupUserId);
			getSuperAdminBroker().addMember(writeGroupId, writeGroupUserId);
			getSuperAdminBroker().addMember(createdGroupId, createdGroupUserId);

			getSuperAdminBroker().assignRole(serverAdminId, Roles.serverAdmin());
			getSuperAdminBroker().changeRole(getProjectSpace().getProjectId(), readGroupId, Roles.reader());
			getSuperAdminBroker().changeRole(getProjectSpace().getProjectId(), writeGroupId, Roles.writer());
			getSuperAdminBroker().changeRole(getProjectSpace().getProjectId(), readUserId, Roles.reader());
			getSuperAdminBroker().changeRole(getProjectSpace().getProjectId(), writeUserId, Roles.writer());
			// TODO what about assign role?
		} catch (final ESException ex) {
			fail(ex.getMessage());
		}
	}

	private static ACOrgUnitId getId(ESUsersession esUsersession, String user) throws ESException {
		return ServerUtil.getUser(esUsersession, user).getId();
	}

	@Override
	@After
	public void after() {
		try {
			getSuperAdminBroker().deleteUser(unrelatedGroupUserId);
			getSuperAdminBroker().deleteUser(readGroupUserId);
			getSuperAdminBroker().deleteUser(writeGroupUserId);
			getSuperAdminBroker().deleteUser(createdGroupUserId);

			getSuperAdminBroker().deleteUser(serverAdminId);
			getSuperAdminBroker().deleteUser(unrelatedUserId);
			getSuperAdminBroker().deleteUser(readUserId);
			getSuperAdminBroker().deleteUser(writeUserId);
			getSuperAdminBroker().deleteUser(createdUserId);

			getSuperAdminBroker().deleteGroup(unrelatedGroupId);
			getSuperAdminBroker().deleteGroup(readGroupId);
			getSuperAdminBroker().deleteGroup(writeGroupId);
			getSuperAdminBroker().deleteGroup(createdGroupId);
		} catch (final ESException ex) {
			fail(ex.getMessage());
		}
		super.after();
	}

	@Test
	public void testGetUsersPA() throws ESException {
		final List<ACUser> users = getAdminBroker().getUsers();
		// self + 2 groups + 2 users + 1 created
		assertEquals(1 + 2 + 2 + 1, users.size());
		final Set<ACOrgUnitId> expectedIds = new LinkedHashSet<ACOrgUnitId>(Arrays.asList(
			getId(getSuperUsersession(), getUser()),
			readGroupUserId,
			writeGroupUserId,
			readUserId,
			writeUserId,
			createdUserId));
		for (final ACUser user : users) {
			expectedIds.remove(user.getId());
		}
		assertTrue(expectedIds.isEmpty());
	}

	@Test
	public void testGetUsersPAWithPotentialDuplicates() throws ESException {
		getSuperAdminBroker().addMember(readGroupId, readUserId);
		getSuperAdminBroker().addMember(writeGroupId, createdUserId);

		final List<ACUser> users = getAdminBroker().getUsers();
		// self + 2 groups + 2 users + 1 created
		assertEquals(1 + 2 + 2 + 1, users.size());
		final Set<ACOrgUnitId> expectedIds = new LinkedHashSet<ACOrgUnitId>(Arrays.asList(
			getId(getSuperUsersession(), getUser()),
			readGroupUserId,
			writeGroupUserId,
			readUserId,
			writeUserId,
			createdUserId));
		for (final ACUser user : users) {
			expectedIds.remove(user.getId());
		}
		assertTrue(expectedIds.isEmpty());
	}

	@Test
	public void testGetGroupsPA() throws ESException {
		final List<ACGroup> groups = getAdminBroker().getGroups();
		assertEquals(3, groups.size());
		final Set<ACOrgUnitId> expectedIds = new LinkedHashSet<ACOrgUnitId>(Arrays.asList(
			readGroupId,
			writeGroupId,
			createdGroupId));
		for (final ACGroup group : groups) {
			expectedIds.remove(group.getId());
		}
		assertTrue(expectedIds.isEmpty());
	}

	@Test
	public void testGetUnitsPA() throws ESException {
		@SuppressWarnings("rawtypes")
		final List<ACOrgUnit> orgUnits = getAdminBroker().getOrgUnits();
		// self + 2 group user + 2 users + 1 created + 3 groups
		assertEquals(1 + 2 + 2 + 1 + 3, orgUnits.size());
		final Set<ACOrgUnitId> expectedIds = new LinkedHashSet<ACOrgUnitId>(Arrays.asList(
			getId(getSuperUsersession(), getUser()),
			readGroupUserId,
			writeGroupUserId,
			readUserId,
			writeUserId,
			createdUserId,
			readGroupId,
			writeGroupId,
			createdGroupId));
		for (final ACOrgUnit<?> unit : orgUnits) {
			expectedIds.remove(unit.getId());
		}
		assertTrue(expectedIds.isEmpty());
	}

}