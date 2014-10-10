/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * emueller
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.test.persistence;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test Suite for running all tests of workspace.
 * 
 * @author emueller
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ ImportExportTest.class, PersistenceTest.class, FeatureMapPersistenceTest.class })
public class AllPersistenceTests {

}
