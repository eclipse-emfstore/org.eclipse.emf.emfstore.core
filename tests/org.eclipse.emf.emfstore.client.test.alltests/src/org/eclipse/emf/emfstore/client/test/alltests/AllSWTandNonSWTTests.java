/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.test.alltests;

import org.eclipse.emf.emfstore.client.test.ui.AllUITests;
import org.eclipse.emf.emfstore.internal.client.test.AllTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test suite for all SWT and non-SWT EMFStore client tests.
 * 
 * @author jfaltermeier
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ AllTests.class, AllUITests.class
// , AllPerformanceTests.class
})
public class AllSWTandNonSWTTests {

}
// @RunWith(Suite.class)
// @SuiteClasses({ AllUITests.class, AllTests.class, AllPerformanceTests.class })
// public class AllSWTandNonSWTTests {
//
// }