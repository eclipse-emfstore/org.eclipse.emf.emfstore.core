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

import static org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil.share;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.emfstore.client.test.common.cases.ESTestWithMockServer;
import org.eclipse.emf.emfstore.client.test.common.dsl.Roles;
import org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil;
import org.eclipse.emf.emfstore.client.test.common.util.ServerUtil;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.model.ProjectHistory;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.ProjectAdminRole;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESGlobalProjectIdImpl;
import org.eclipse.emf.emfstore.server.auth.ESProjectAdminPrivileges;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test the {@link ESProjectAdminPrivileges#DeleteOrgUnit} privilege of a
 * {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.ProjectAdminRole ProjectAdminRole}.
 *
 * @author emueller
 *
 */
public class DeleteOrgUnitTest extends ProjectAdminTest {

	@BeforeClass
	public static void beforeClass() {
		startEMFStoreWithPAProperties(
			ESProjectAdminPrivileges.CreateGroup,
			ESProjectAdminPrivileges.ChangeAssignmentsOfOrgUnits,
			ESProjectAdminPrivileges.AssignRoleToOrgUnit,
			ESProjectAdminPrivileges.ShareProject,
			ESProjectAdminPrivileges.DeleteOrgUnit);
	}

	@AfterClass
	public static void afterClass() {
		stopEMFStore();
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

	@Test
	public void createGroup() throws ESException {
		makeUserPA();
		getAdminBroker().createGroup(getNewGroupName());
	}

	@Test(expected = AccessControlException.class)
	public void deleteUserAsPAWithUserBeingMemberOfOtherProject() throws ESException {
		share(getSuperUsersession(), getLocalProject());
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		makeUserPA();
		final ProjectId projectId = ESGlobalProjectIdImpl.class.cast(
			getLocalProject().getGlobalProjectId()).toInternalAPI();

		final ProjectId secondProjectId = ESGlobalProjectIdImpl.class.cast(
			share(getSuperUsersession(), getLocalProject())).toInternalAPI();

		getAdminBroker().addParticipant(projectId, newUser, Roles.writer());
		getSuperAdminBroker().addParticipant(secondProjectId, newUser, Roles.writer());

		getAdminBroker().deleteUser(newUser);
	}

	@Test(expected = AccessControlException.class)
	public void deleteUserFailsIsPartOfNonProjectGroup() throws ESException {
		makeUserPA();
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		final ACOrgUnitId group = ServerUtil.createGroup(getSuperUsersession(), getNewGroupName());
		final ACOrgUnitId otherGroup = ServerUtil.createGroup(getSuperUsersession(), getNewOtherGroupName());
		getAdminBroker().addMember(group, otherGroup);
		getAdminBroker().addMember(otherGroup, newUser);

		ProjectUtil.share(getUsersession(), getLocalProject());
		final ProjectSpace clonedProjectSpace = cloneProjectSpace(getProjectSpace());
		ProjectUtil.share(getSuperUsersession(), clonedProjectSpace.toAPI());

		getAdminBroker().changeRole(clonedProjectSpace.getProjectId(), group, Roles.writer());

		getAdminBroker().deleteUser(newUser);
	}

	@Test(expected = AccessControlException.class)
	public void deleteUserWithPAFailsSinceStillHasProject() throws ESException {
		makeUserPA();

		final ACOrgUnitId newUserId = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		final ProjectId secondProjectId = ESGlobalProjectIdImpl.class.cast(
			ProjectUtil.share(getUsersession(), getLocalProject())).toInternalAPI();
		final ProjectId thirdProjectId = ESGlobalProjectIdImpl.class.cast(
			ProjectUtil.share(getUsersession(), getLocalProject())).toInternalAPI();

		getAdminBroker().changeRole(
			secondProjectId,
			newUserId,
			Roles.projectAdmin());

		getAdminBroker().changeRole(
			thirdProjectId,
			newUserId,
			Roles.projectAdmin());

		final ProjectAdminRole paRole = (ProjectAdminRole) getSuperAdminBroker().getRole(secondProjectId, newUserId);
		paRole.getProjects().remove(0);

		// must fail since newUser is still PA
		getAdminBroker().deleteUser(newUserId);
	}

	@Test
	public void deleteCleanupOfOrphanProject() throws ESException {
		makeUserPA();

		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		final ProjectId secondProjectId = ESGlobalProjectIdImpl.class.cast(
			ProjectUtil.share(getUsersession(), getLocalProject())).toInternalAPI();

		getAdminBroker().changeRole(
			secondProjectId,
			newUser,
			Roles.projectAdmin());

		int removeIndex = -1;
		// TODO: not transparent, using mock directly
		final EList<ProjectHistory> projectHistories = ESTestWithMockServer.getServerMock().getServerSpace()
			.getProjects();
		for (int i = 0; i < projectHistories.size(); i++) {
			if (projectHistories.get(i).getProjectId().equals(secondProjectId)) {
				removeIndex = i;
			}
		}
		projectHistories.remove(removeIndex);

		// must fail since newUser is still PA
		getAdminBroker().deleteUser(newUser);
	}

	@Test(expected = AccessControlException.class)
	public void deleteUserWithPAFails() throws ESException {
		makeUserPA();

		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		final ProjectId secondProjectId = ESGlobalProjectIdImpl.class.cast(
			ProjectUtil.share(getUsersession(), getLocalProject())).toInternalAPI();

		getAdminBroker().changeRole(
			secondProjectId,
			newUser,
			Roles.projectAdmin());

		// must fail since newUser is PA
		getAdminBroker().deleteUser(newUser);
	}

	@Test
	public void deleteUserWithExPARoleSucceeds() throws ESException {
		makeUserPA();

		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		final ProjectId secondProjectId = ESGlobalProjectIdImpl.class.cast(
			ProjectUtil.share(getUsersession(), getLocalProject())).toInternalAPI();
		getAdminBroker().changeRole(
			secondProjectId,
			newUser,
			Roles.projectAdmin());

		final ProjectAdminRole paRole = (ProjectAdminRole) getSuperAdminBroker().getRole(secondProjectId, newUser);
		paRole.getProjects().remove(0);

		// must succeed since newUser has PARole without a project id
		getAdminBroker().deleteUser(newUser);
	}

	@Test
	public void deleteGroup() throws ESException {
		makeUserPA();
		final ACOrgUnitId newUser = getSuperAdminBroker().createUser(getNewUsername());
		final ACOrgUnitId group = getSuperAdminBroker().createGroup(getNewGroupName());
		final ACOrgUnitId otherGroup = getSuperAdminBroker().createGroup(getNewOtherGroupName());
		getSuperAdminBroker().addMember(group, otherGroup);
		getSuperAdminBroker().addMember(otherGroup, newUser);

		ProjectUtil.share(getUsersession(), getLocalProject());

		getSuperAdminBroker().changeRole(getProjectSpace().getProjectId(), group, Roles.writer());

		final int oldSize = getSuperAdminBroker().getGroups().size();
		getAdminBroker().deleteGroup(group);
		assertEquals(oldSize - 1, getSuperAdminBroker().getGroups().size());
		assertEquals(oldSize - 2, getAdminBroker().getGroups().size());// got invisible
	}

	/**
	 * @throws ESException
	 */
	@Test
	public void deleteUser() throws ESException {
		makeUserPA();
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		final ACOrgUnitId group = ServerUtil.createGroup(getSuperUsersession(), getNewGroupName());
		final ACOrgUnitId otherGroup = ServerUtil.createGroup(getSuperUsersession(), getNewOtherGroupName());
		getAdminBroker().addMember(group, otherGroup);
		getAdminBroker().addMember(otherGroup, newUser);

		ProjectUtil.share(getUsersession(), getLocalProject());
		final ProjectSpace clonedProjectSpace = cloneProjectSpace(getProjectSpace());
		ProjectUtil.share(getSuperUsersession(), clonedProjectSpace.toAPI());

		getAdminBroker().changeRole(getProjectSpace().getProjectId(), group, Roles.writer());
		final int oldSize = getAdminBroker().getUsers().size();
		getAdminBroker().deleteUser(newUser);
		assertEquals(oldSize - 1, getAdminBroker().getUsers().size());
	}

	@Test
	public void deleteGroupBothGroupArePartOfProject() throws ESException {
		makeUserPA();
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		final ACOrgUnitId group = ServerUtil.createGroup(getSuperUsersession(), getNewGroupName());
		final ACOrgUnitId otherGroup = ServerUtil.createGroup(getSuperUsersession(), getNewOtherGroupName());
		getAdminBroker().addMember(group, otherGroup);
		getAdminBroker().addMember(otherGroup, newUser);

		ProjectUtil.share(getUsersession(), getLocalProject());
		final ProjectSpace clonedProjectSpace = cloneProjectSpace(getProjectSpace());
		ProjectUtil.share(getSuperUsersession(), clonedProjectSpace.toAPI());

		getAdminBroker().changeRole(getProjectSpace().getProjectId(), group, Roles.writer());
		getAdminBroker().changeRole(getProjectSpace().getProjectId(), otherGroup, Roles.reader());

		final int oldSize = getAdminBroker().getGroups().size();
		getAdminBroker().deleteGroup(group);
		assertEquals(oldSize - 1, getAdminBroker().getGroups().size());
	}

	@Test(expected = AccessControlException.class)
	public void deleteUserFailsUserIsInTransitiveGroup() throws ESException {
		makeUserPA();
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		final ACOrgUnitId group = ServerUtil.createGroup(getSuperUsersession(), getNewGroupName());
		final ACOrgUnitId otherGroup = ServerUtil.createGroup(getSuperUsersession(), getNewOtherGroupName());
		getAdminBroker().addMember(group, otherGroup);
		getAdminBroker().addMember(otherGroup, newUser);

		ProjectUtil.share(getUsersession(), getLocalProject());
		final ProjectSpace clonedProjectSpace = cloneProjectSpace(getProjectSpace());
		ProjectUtil.share(getSuperUsersession(), clonedProjectSpace.toAPI());

		getSuperAdminBroker().changeRole(clonedProjectSpace.getProjectId(), group, Roles.writer());
		getAdminBroker().deleteUser(newUser);
	}

	@Test
	public void deleteUserSucceedsUserIsInTransitiveGroup() throws ESException {
		makeUserSA();
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		final ACOrgUnitId group = ServerUtil.createGroup(getSuperUsersession(), getNewGroupName());
		final ACOrgUnitId otherGroup = ServerUtil.createGroup(getSuperUsersession(), getNewOtherGroupName());
		getAdminBroker().addMember(group, otherGroup);
		getAdminBroker().addMember(otherGroup, newUser);

		ProjectUtil.share(getUsersession(), getLocalProject());
		final ProjectSpace clonedProjectSpace = cloneProjectSpace(getProjectSpace());
		ProjectUtil.share(getSuperUsersession(), clonedProjectSpace.toAPI());

		getSuperAdminBroker().changeRole(clonedProjectSpace.getProjectId(), group, Roles.writer());
		getAdminBroker().deleteUser(newUser);
	}

	@Test(expected = AccessControlException.class)
	public void deleteGroupFailsIsInTransitiveGroup() throws ESException {
		makeUserPA();
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		final ACOrgUnitId group = ServerUtil.createGroup(getSuperUsersession(), getNewGroupName());
		final ACOrgUnitId otherGroup = ServerUtil.createGroup(getSuperUsersession(), getNewOtherGroupName());
		getAdminBroker().addMember(group, otherGroup);
		getAdminBroker().addMember(otherGroup, newUser);

		ProjectUtil.share(getUsersession(), getLocalProject());
		final ProjectSpace clonedProjectSpace = cloneProjectSpace(getProjectSpace());
		ProjectUtil.share(getSuperUsersession(), clonedProjectSpace.toAPI());

		getSuperAdminBroker().changeRole(clonedProjectSpace.getProjectId(), group, Roles.writer());

		getAdminBroker().deleteGroup(otherGroup);
	}

	@Test
	public void deleteGroupSucceedsIsInTransitiveGroup() throws ESException {
		makeUserSA();
		final ACOrgUnitId newUser = ServerUtil.createUser(getUsersession(), getNewUsername());
		final ACOrgUnitId group = ServerUtil.createGroup(getUsersession(), getNewGroupName());
		final ACOrgUnitId otherGroup = ServerUtil.createGroup(getUsersession(), getNewOtherGroupName());
		getAdminBroker().addMember(group, otherGroup);
		getAdminBroker().addMember(otherGroup, newUser);

		ProjectUtil.share(getUsersession(), getLocalProject());
		final ProjectSpace clonedProjectSpace = cloneProjectSpace(getProjectSpace());
		ProjectUtil.share(getSuperUsersession(), clonedProjectSpace.toAPI());

		getSuperAdminBroker().changeRole(clonedProjectSpace.getProjectId(), group, Roles.writer());

		getAdminBroker().deleteGroup(otherGroup);
	}
}
