/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * koegel
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.test;

import org.eclipse.emf.emfstore.internal.client.test.caching.AllCachingTests;
import org.eclipse.emf.emfstore.internal.client.test.common.AllCommonTests;
import org.eclipse.emf.emfstore.internal.client.test.connectionmanager.SessionManagerTest;
import org.eclipse.emf.emfstore.internal.client.test.persistence.AllPersistenceTests;
import org.eclipse.emf.emfstore.internal.client.test.workspace.AllWorkspaceTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Runs all tests.
 *
 * @author koegel
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	AllCachingTests.class,
	AllCommonTests.class,
	AllPersistenceTests.class,
	AllWorkspaceTests.class,
	SessionManagerTest.class
})
public class AllTests {

}
