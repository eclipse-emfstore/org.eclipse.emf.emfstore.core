/*******************************************************************************
 * Copyright (c) 2011-2015 EclipseSource Muenchen GmbH and others.
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.emfstore.client.ESRemoteProject;
import org.eclipse.emf.emfstore.client.test.common.dsl.Roles;
import org.eclipse.emf.emfstore.client.test.common.util.ServerUtil;
import org.eclipse.emf.emfstore.internal.client.model.Usersession;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESUsersessionImpl;
import org.eclipse.emf.emfstore.internal.server.core.Messages;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.ProjectAdminRole;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.Role;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESGlobalProjectIdImpl;
import org.eclipse.emf.emfstore.server.auth.ESProjectAdminPrivileges;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Specification for testing the {@link org.eclipse.emf.emfstore.internal.server.AdminEmfStore#addInitialParticipant
 * AdminEmfStore#addInitialParticipant} server call.
 *
 * @author emueller
 *
 */
public class AddInitialParticipantTest extends ProjectAdminTest {

	// BEGIN COMPLEX CODE
	// Checkstyle complains about public modifier..
	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	// END COMPLEX CODE

	@BeforeClass
	public static void beforeClass() {
		startEMFStoreWithPAProperties(
			ESProjectAdminPrivileges.ShareProject,
			ESProjectAdminPrivileges.AssignRoleToOrgUnit // needed for share
			);
	}

	@Test(expected = ESException.class)
	public void shouldThrowESExceptionWhenRequestingUserHasNoProjectAdminRole() throws ESException {
		getLocalProject().shareProject(getSuperUsersession(), new NullProgressMonitor());
		final Usersession session = ESUsersessionImpl.class.cast(getUsersession()).toInternalAPI();
		getAdminBroker().addInitialParticipant(getProjectSpace().getProjectId(),
			session.getACUser().getId(),
			Roles.projectAdmin());
	}

	@Test
	public void shouldThrowESExceptionWhenRequestingUserTriesToAssignAServerAdminRole()
		throws ESException {
		expectedException.expect(ESException.class);
		expectedException.expectMessage(Messages.AdminEmfStoreImpl_Not_Allowed_To_Assign_ServerAdminRole);

		getLocalProject().shareProject(getSuperUsersession(), new NullProgressMonitor());
		final Usersession session = ESUsersessionImpl.class.cast(getUsersession()).toInternalAPI();
		final ACOrgUnitId userId = session.getACUser().getId();
		getSuperAdminBroker().changeRole(
			getProjectSpace().getProjectId(),
			userId,
			Roles.projectAdmin());
		getAdminBroker().addInitialParticipant(getProjectSpace().getProjectId(),
			userId,
			Roles.serverAdmin());
	}

	@Test
	public void shouldThrowESExceptionIfRequestingUserDidNotShareProjectBefore()
		throws ESException {
		expectedException.expect(ESException.class);
		expectedException.expectMessage(Messages.AdminEmfStoreImpl_IllegalRequestToAddInitialRole);

		getLocalProject().shareProject(getSuperUsersession(), new NullProgressMonitor());

		final Usersession session = ESUsersessionImpl.class.cast(getUsersession()).toInternalAPI();
		final ACOrgUnitId userId = session.getACUser().getId();
		getSuperAdminBroker().changeRole(
			getProjectSpace().getProjectId(),
			userId,
			Roles.projectAdmin());
		getAdminBroker().addInitialParticipant(getProjectSpace().getProjectId(),
			userId,
			Roles.projectAdmin());
	}

	@Test
	public void shouldThrowESExceptionIfRequestingUserDidShareAnotherProjectBefore()
		throws ESException {
		expectedException.expect(ESException.class);
		expectedException.expectMessage(Messages.AdminEmfStoreImpl_IllegalRequestToAddInitialRole);
		final Usersession session = ESUsersessionImpl.class.cast(getUsersession()).toInternalAPI();
		final ACOrgUnitId userId = session.getACUser().getId();

		getSuperAdminBroker().assignRole(
			userId,
			Roles.projectAdmin());

		final ESRemoteProject sharedProject = getLocalProject().shareProject(getUsersession(),
			new NullProgressMonitor());
		final ProjectId projectId = ESGlobalProjectIdImpl.class.cast(sharedProject.getGlobalProjectId())
			.toInternalAPI();

		getLocalProject().shareProject(getUsersession(), new NullProgressMonitor());

		getAdminBroker().addInitialParticipant(
			projectId,
			userId,
			Roles.projectAdmin());
	}

	@Test(expected = ESException.class)
	public void shouldThrowESExceptionIfRequestingUserDidShareButHasNoProjectAdminRoleAnymore() throws ESException {
		final Usersession session = ESUsersessionImpl.class.cast(getUsersession()).toInternalAPI();
		final ACOrgUnitId userId = session.getACUser().getId();

		getSuperAdminBroker().assignRole(
			userId,
			Roles.projectAdmin());

		final ESRemoteProject shareProject = getLocalProject()
			.shareProject(getUsersession(), new NullProgressMonitor());
		final ProjectId projectId = ESGlobalProjectIdImpl.class.cast(shareProject.getGlobalProjectId()).toInternalAPI();

		getSuperAdminBroker().changeRole(
			getProjectSpace().getProjectId(),
			userId,
			Roles.writer());

		getAdminBroker().addInitialParticipant(
			projectId,
			userId,
			Roles.projectAdmin());
	}

	@Test
	public void shouldThrowESExceptionIfRequestingUserDiffersFromUserTheRoleShouldBeAssignedTo() throws ESException {
		expectedException.expect(ESException.class);
		expectedException.expectMessage(Messages.AdminEmfStoreImpl_OnlyAllowedForRequstingUser);
		final Usersession session = ESUsersessionImpl.class.cast(getUsersession()).toInternalAPI();
		final ACOrgUnitId userId = session.getACUser().getId();

		getSuperAdminBroker().assignRole(
			userId,
			Roles.projectAdmin());

		final ESRemoteProject sharedProject = getLocalProject().shareProject(
			getUsersession(),
			new NullProgressMonitor());
		final ProjectId projectId = ESGlobalProjectIdImpl.class.cast(
			sharedProject.getGlobalProjectId())
			.toInternalAPI();
		final ACOrgUnitId dummyUserId = ServerUtil.createUser(
			getSuperUsersession(), "dummyUser"); //$NON-NLS-1$

		getAdminBroker().addInitialParticipant(
			projectId,
			dummyUserId,
			Roles.projectAdmin());
	}

	@Test
	public void shouldThrowExceptionIfTryingToCallMethodExplicitely() throws ESException {
		expectedException.expect(ESException.class);
		expectedException.expectMessage(Messages.AdminEmfStoreImpl_IllegalRequestToAddInitialRole);
		final Usersession session = ESUsersessionImpl.class.cast(getUsersession()).toInternalAPI();
		final ACOrgUnitId userId = session.getACUser().getId();

		getSuperAdminBroker().assignRole(
			userId,
			Roles.projectAdmin());

		final ESRemoteProject sharedProject = getLocalProject().shareProject(getUsersession(),
			new NullProgressMonitor());
		final ProjectId projectId = ESGlobalProjectIdImpl.class.cast(sharedProject.getGlobalProjectId())
			.toInternalAPI();

		getAdminBroker().addInitialParticipant(
			projectId,
			userId,
			Roles.projectAdmin());
	}

	@Test
	public void shouldSucceedIfAllPrerequisitesAreMet() throws ESException {
		final Usersession session = ESUsersessionImpl.class.cast(getUsersession()).toInternalAPI();
		final ACOrgUnitId userId = session.getACUser().getId();

		getSuperAdminBroker().assignRole(
			userId,
			Roles.projectAdmin());

		final ESRemoteProject sharedProject = getLocalProject().shareProject(
			getUsersession(),
			new NullProgressMonitor());
		final ProjectId projectId = ESGlobalProjectIdImpl.class.cast(
			sharedProject.getGlobalProjectId())
			.toInternalAPI();

		final Role role = getSuperAdminBroker().getRole(projectId, userId);
		assertThat(role, instanceOf(ProjectAdminRole.class));
	}
}
