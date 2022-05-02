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

import org.eclipse.emf.emfstore.client.test.common.dsl.Roles;
import org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil;
import org.eclipse.emf.emfstore.client.test.common.util.ServerUtil;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESUsersessionImpl;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.AccessControl;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESGlobalProjectIdImpl;
import org.eclipse.emf.emfstore.server.auth.ESPasswordHashGenerator.ESHashAndSalt;
import org.eclipse.emf.emfstore.server.auth.ESProjectAdminPrivileges;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test the {@link ESProjectAdminPrivileges#CreateUser} and {@link ESProjectAdminPrivileges#ChangeUserPassword}
 * privileges of a
 * {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.ProjectAdminRole ProjectAdminRole} in a
 * more complex scenarios.
 *
 * @author emueller
 *
 */
public class ChangePasswordTests extends ProjectAdminTest {

	private static final String NEW_USER_PASSWORD = "foo"; //$NON-NLS-1$

	@BeforeClass
	public static void beforeClass() {
		startEMFStoreWithPAProperties(
			ESProjectAdminPrivileges.ShareProject,
			ESProjectAdminPrivileges.AssignRoleToOrgUnit,
			ESProjectAdminPrivileges.ChangeUserPassword,
			ESProjectAdminPrivileges.ChangeAssignmentsOfOrgUnits);
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
	public void changePasswordOfUser() throws ESException {
		makeUserPA();
		final ACOrgUnitId createUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		ProjectUtil.share(getUsersession(), getLocalProject());
		getAdminBroker().changeRole(getProjectSpace().getProjectId(), createUser, Roles.writer());
		ServerUtil.changePassword(getUsersession(), createUser, getNewUsername(), NEW_USER_PASSWORD);
		final ACUser user = ServerUtil.getUser(getSuperUsersession(), createUser);
		final String userPass = user.getPassword();
		final int separatorIndex = userPass.indexOf(ESHashAndSalt.SEPARATOR);
		final String hash = userPass.substring(0, separatorIndex);
		final String salt = userPass.substring(separatorIndex + 1);
		assertTrue(AccessControl.getESPasswordHashGenerator().verifyPassword(NEW_USER_PASSWORD, hash, salt));
	}

	@Test
	public void letUserChangeHisPassword() throws ESException {
		makeUserPA();
		ProjectUtil.share(getUsersession(), getLocalProject());
		ServerUtil.changePassword(getUsersession(),
			ESUsersessionImpl.class.cast(getUsersession()).toInternalAPI().getACUser().getId(),
			getUser(), "new-password"); //$NON-NLS-1$
		final ACUser user = ServerUtil.getUser(getSuperUsersession(), getUser());
		final String userPass = user.getPassword();
		final int separatorIndex = userPass.indexOf(ESHashAndSalt.SEPARATOR);
		final String hash = userPass.substring(0, separatorIndex);
		final String salt = userPass.substring(separatorIndex + 1);
		assertTrue(AccessControl.getESPasswordHashGenerator().verifyPassword("new-password", hash, salt)); //$NON-NLS-1$
	}

	@Test(expected = AccessControlException.class)
	public void letUserNotChangePasswordOfOtherUser() throws ESException {
		final ACOrgUnitId createdUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		ProjectUtil.share(getUsersession(), getLocalProject());
		ServerUtil.changePassword(getUsersession(),
			createdUser,
			getUser(), "new-password"); //$NON-NLS-1$
	}

	@Test(expected = AccessControlException.class)
	public void changePasswordOfOtherPASameProject() throws ESException {
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		ProjectUtil.share(getUsersession(), getLocalProject());
		getAdminBroker().changeRole(getProjectSpace().getProjectId(), newUser, Roles.projectAdmin());
		// try to change the password of the other project admin
		ServerUtil.changePassword(getUsersession(), newUser, getNewUsername(), NEW_USER_PASSWORD);
		final ACUser user = ServerUtil.getUser(getSuperUsersession(), newUser);
		assertEquals(NEW_USER_PASSWORD, user.getPassword());
	}

	@Test(expected = AccessControlException.class)
	public void changePasswordOfOtherPADifferentProjects() throws ESException {
		makeUserPA();
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		ProjectUtil.share(getUsersession(), getLocalProject());
		// share a second project
		final ProjectId projectId = ESGlobalProjectIdImpl.class.cast(
			ProjectUtil.share(getSuperUsersession(), getLocalProject())).toInternalAPI();
		getAdminBroker().changeRole(
			projectId,
			newUser,
			Roles.projectAdmin());
		// try to change the password of the other project admin
		ServerUtil.changePassword(getUsersession(), newUser, getNewUsername(), NEW_USER_PASSWORD);
		final ACUser user = ServerUtil.getUser(getSuperUsersession(), newUser);
		assertEquals(NEW_USER_PASSWORD, user.getPassword());
	}

	@Test(expected = AccessControlException.class)
	public void changePasswordUserHasNoProject() throws ESException {
		makeUserPA();
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		// shared with super user, project does not belong to project admin
		ProjectUtil.share(getSuperUsersession(), getLocalProject());
		getAdminBroker().changeRole(getProjectSpace().getProjectId(), newUser, Roles.writer());
		ServerUtil.changePassword(getUsersession(), newUser, getNewUsername(), NEW_USER_PASSWORD);
	}

	@Test(expected = AccessControlException.class)
	public void changePasswordGroupOfGroupHasNoProject() throws ESException {
		makeUserPA();
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		final ACOrgUnitId group = ServerUtil.createGroup(getSuperUsersession(), getNewGroupName());
		final ACOrgUnitId otherGroup = ServerUtil.createGroup(getSuperUsersession(), getNewOtherGroupName());
		getAdminBroker().addMember(group, otherGroup);
		getAdminBroker().addMember(otherGroup, newUser);

		ProjectUtil.share(getSuperUsersession(), getLocalProject());
		getSuperAdminBroker().changeRole(getProjectSpace().getProjectId(), group, Roles.writer());

		getAdminBroker().changeUser(newUser, getNewUsername(), NEW_USER_PASSWORD);
	}

	@Test(expected = AccessControlException.class)
	public void changePasswordGroupOfGroupHasMissingProject() throws ESException {
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

		getAdminBroker().changeUser(newUser, getNewUsername(), NEW_USER_PASSWORD);
	}

	@Test
	public void changePasswordUserIsPartOfGroupOfGroup() throws ESException {
		makeUserPA();
		final ACOrgUnitId newUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		final ACOrgUnitId group = ServerUtil.createGroup(getSuperUsersession(), getNewGroupName());
		final ACOrgUnitId otherGroup = ServerUtil.createGroup(getSuperUsersession(), getNewOtherGroupName());
		getAdminBroker().addMember(group, otherGroup);
		getAdminBroker().addMember(otherGroup, newUser);

		ProjectUtil.share(getUsersession(), getLocalProject());
		getSuperAdminBroker().changeRole(getProjectSpace().getProjectId(), group, Roles.writer());

		getAdminBroker().changeUser(newUser, getNewUsername(), NEW_USER_PASSWORD);
		final ACUser user = ServerUtil.getUser(getSuperUsersession(), newUser);

		final String userPass = user.getPassword();
		final int separatorIndex = userPass.indexOf(ESHashAndSalt.SEPARATOR);
		final String hash = userPass.substring(0, separatorIndex);
		final String salt = userPass.substring(separatorIndex + 1);
		assertTrue(AccessControl.getESPasswordHashGenerator().verifyPassword(NEW_USER_PASSWORD, hash, salt));
	}

	@Test /* do not expect exception on annotation */
	public void samePasswordForUser() throws ESException {
		/* setup */
		makeUserPA();
		final ACOrgUnitId createUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		ProjectUtil.share(getUsersession(), getLocalProject());
		getAdminBroker().changeRole(getProjectSpace().getProjectId(), createUser, Roles.writer());
		ServerUtil.changePassword(getUsersession(), createUser, getNewUsername(), getPassword());

		try {
			/* act */
			ServerUtil.changePassword(getUsersession(), createUser, getNewUsername(), getPassword());
		} catch (final ESException ex) {
			/* assert */
			assertEquals("Same password provided as existing.", ex.getMessage()); //$NON-NLS-1$
		}
	}
}