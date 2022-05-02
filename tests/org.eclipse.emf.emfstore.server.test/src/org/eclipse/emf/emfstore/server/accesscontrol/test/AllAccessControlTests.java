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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Suite for all access control tests.
 *
 * @author emueller
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	AdminEMFStoreTests.class,
	AdminEMFStoreProjectAdminTests.class,
	AssignRoleToOrgUnitTests.class,
	AssignRoleToOrgUnitMissingPrivilegTests.class,
	AssignOrgUnitToGroupTests.class,
	AssignOrgUnitToGroupMissingPrivilegeTests.class,
	DeleteOrgUnitTest.class,
	DeleteOrgUnitMissingPrivilegeTest.class,
	DeleteProjectTest.class,
	CreateGroupTest.class,
	CreateGroupMissingPrivilegeTest.class,
	ChangePasswordTests.class,
	CreateProjectTest.class,
	CreateProjectMissingPrivilegTest.class,
	CreateUserTests.class,
	CreateUserMissingPrivilegTests.class,
	CreateUserWithPasswordTests.class,
	CreateUserWithPasswordMissingPrivilegTests.class,
	AdminEMFStoreTests.class,
	AddInitialParticipantTest.class,
	AutoCreateACUserTestTest.class,
	DefaultESPasswordHashGeneratorTests.class,
	DefaultESAuthorizationServiceTests.class

})
public class AllAccessControlTests {

}
