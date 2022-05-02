/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.accesscontrol.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil;
import org.eclipse.emf.emfstore.client.test.common.util.ServerUtil;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.model.ProjectInfo;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.server.auth.ESProjectAdminPrivileges;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests access control related functionality that is not
 * covered in any of the other tests.
 *
 * @author emueller
 *
 */
public class AdminEMFStoreTests extends ProjectAdminTest {

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
	@After
	public void after() {
		try {
			ServerUtil.deleteGroup(getSuperUsersession(), getNewGroupName());
			ServerUtil.deleteGroup(getSuperUsersession(), getNewOtherGroupName());
		} catch (final ESException ex) {
			fail(ex.getMessage());
		}
		super.after();
	}

	@Override
	@Before
	public void before() {
		super.before();
	}

	@Test(expected = AccessControlException.class)
	public void getGroupsNotPA() throws ESException {
		getAdminBroker().getGroups();
	}

	@Test
	public void getGroupsPA() throws ESException {
		makeUserPA();
		getAdminBroker().getGroups();
	}

	@Test(expected = AccessControlException.class)
	public void getOrgUnitsNotPA() throws ESException {
		getAdminBroker().getOrgUnits();
	}

	@Test
	public void getOrgUnitsPA() throws ESException {
		makeUserPA();
		@SuppressWarnings("rawtypes")
		final List<ACOrgUnit> orgUnits = getAdminBroker().getOrgUnits();
		assertTrue(orgUnits.size() == 0);
	}

	@Test
	public void getRoleNotPA() {

	}

	@Test
	public void getProjectsPA() throws ESException {
		makeUserPA();
		ProjectUtil.share(getUsersession(), getLocalProject());
		final List<ProjectInfo> projectInfos = getAdminBroker().getProjectInfos();
		assertEquals(1, projectInfos.size());
	}

	@Test
	public void getGroupsForOrgUnit() throws ESException {
		makeUserPA();
		final ACOrgUnitId group = getAdminBroker().createGroup(getNewGroupName());
		final ACOrgUnitId member = getAdminBroker().createUser(getNewUsername());
		getAdminBroker().addMember(group, member);
		final List<ACGroup> groups = getAdminBroker().getGroups(member);
		assertEquals(1, groups.size());
	}

	@Test
	public void addMember() throws ESException {
		makeUserPA();
		final ACOrgUnitId group = getAdminBroker().createGroup(getNewGroupName());
		final ACOrgUnitId member = getAdminBroker().createUser(getNewUsername());
		getAdminBroker().addMember(group, member);
		assertEquals(1,
			getAdminBroker().getMembers(group).size());
	}

	@Test
	public void removeMember() throws ESException {
		makeUserPA();
		final ACOrgUnitId group = getAdminBroker().createGroup(getNewGroupName());
		final ACOrgUnitId otherGroup = getAdminBroker().createGroup(getNewOtherGroupName());
		final ACOrgUnitId member = getAdminBroker().createUser(getNewUsername());
		getAdminBroker().addMember(group, member);
		getAdminBroker().addMember(group, otherGroup);
		getAdminBroker().removeMember(group, otherGroup);
		assertEquals(1,
			getAdminBroker().getMembers(group).size());
	}

	@Test
	public void removeGroup() throws ESException {
		makeUserPA();
		final ACOrgUnitId group = getAdminBroker().createGroup(getNewGroupName());
		final ACOrgUnitId member = getAdminBroker().createUser(getNewUsername());
		getAdminBroker().addMember(group, member);
		getAdminBroker().removeGroup(member, group);
		assertEquals(0,
			getAdminBroker().getMembers(group).size());
	}

	@Test
	public void removeGroupAsSA() throws ESException {
		makeUserSA();
		final ACOrgUnitId group = getAdminBroker().createGroup(getNewGroupName());
		final ACOrgUnitId member = getAdminBroker().createUser(getNewUsername());
		getAdminBroker().addMember(group, member);
		getAdminBroker().removeGroup(member, group);
		assertEquals(0,
			getAdminBroker().getMembers(group).size());
	}
}