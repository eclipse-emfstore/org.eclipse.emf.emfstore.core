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
package org.eclipse.emf.emfstore.client.test;

import org.eclipse.emf.emfstore.client.test.api.AllAPITests;
import org.eclipse.emf.emfstore.client.test.caching.AllCachingTests;
import org.eclipse.emf.emfstore.client.test.changeTracking.AllChangeTrackingTests;
import org.eclipse.emf.emfstore.client.test.common.AllCommonTests;
import org.eclipse.emf.emfstore.client.test.conflictDetection.AllConflictDetectionTests;
import org.eclipse.emf.emfstore.client.test.memory.AllMemoryTests;
import org.eclipse.emf.emfstore.client.test.persistence.AllPersistenceTests;
import org.eclipse.emf.emfstore.client.test.server.AllServerTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Runs all tests.
 * 
 * @author koegel
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	AllAPITests.class,
	AllChangeTrackingTests.class,
	AllConflictDetectionTests.class,
	AllCachingTests.class,
	AllServerTests.class,
	// AllIntegrationTests.class,
	AllCommonTests.class,
	AllPersistenceTests.class,
	AllMemoryTests.class })
public class AllTests {

}
