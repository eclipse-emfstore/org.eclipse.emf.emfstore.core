/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * chodnick
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.changetracking.test;

import org.eclipse.emf.emfstore.client.changetracking.test.canonization.AllCanonizationTests;
import org.eclipse.emf.emfstore.client.changetracking.test.command.AllCommandTests;
import org.eclipse.emf.emfstore.client.changetracking.test.notification.AllNotificationTests;
import org.eclipse.emf.emfstore.client.changetracking.test.toplogy.AllTopologyTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test Suite for running all tests of workspace.
 *
 * @author chodnick
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	AllNotificationTests.class,
	AllTopologyTests.class,
	AllCanonizationTests.class,
	AllCommandTests.class
})
public class AllChangeTrackingTests {

}
