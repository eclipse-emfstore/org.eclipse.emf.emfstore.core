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

import static org.junit.Assert.assertFalse;

import org.eclipse.emf.emfstore.client.test.common.dsl.Roles;
import org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil;
import org.eclipse.emf.emfstore.client.test.common.util.ServerUtil;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.server.auth.ESProjectAdminPrivileges;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test the {@link ESProjectAdminPrivileges#CreateUser} and {@link ESProjectAdminPrivileges#ChangeUserPassword}
 * privileges of a
 * {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.ProjectAdminRole ProjectAdminRole}.
 *
 * @author emueller
 *
 */
public class CreateUserWithPasswordTests extends ProjectAdminTest {

	private static final String PASSWORD = "foo"; //$NON-NLS-1$

	@BeforeClass
	public static void beforeClass() {
		startEMFStoreWithPAProperties(
			ESProjectAdminPrivileges.CreateUser,
			ESProjectAdminPrivileges.ChangeUserPassword);
	}

	public static void afterClass() {
		stopEMFStore();
	}

	@Override
	@After
	public void after() {
		super.after();
	}

	@Override
	@Before
	public void before() {
		super.before();
	}

	@Test
	public void createUserWithPassword() throws ESException {
		makeUserPA();
		final ACOrgUnitId userId = ServerUtil.createUser(getUsersession(), getNewUsername());
		final String oldEncryptedPassword = getUsersession().getPassword();
		ServerUtil.changePassword(getUsersession(), userId, getNewUsername(), PASSWORD);
		final ACUser updatedUser = ACUser.class.cast(getAdminBroker().getOrgUnit(userId));
		assertFalse(oldEncryptedPassword.equals(updatedUser.getPassword()));
	}

	@Test(expected = AccessControlException.class)
	public void createUserWithPasswordNotAtomic() throws ESException {
		makeUserPA();
		final ACOrgUnitId userId = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		ProjectUtil.share(getSuperUsersession(), getLocalProject());
		getSuperAdminBroker().addParticipant(getProjectSpace().getProjectId(), userId, Roles.writer());
		ServerUtil.changePassword(getUsersession(), userId, getNewUsername(), PASSWORD);
	}

	@Test(expected = AccessControlException.class)
	public void createUserWithPasswordNotPA() throws ESException {
		final ACOrgUnitId createUser = ServerUtil.createUser(getSuperUsersession(), getNewUsername());
		ServerUtil.changePassword(getUsersession(), createUser, getNewUsername(), PASSWORD);
	}
}