/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * JulianSommerfeldt
 ******************************************************************************/
package org.eclipse.emf.emfstore.fuzzy.emf.test;

import org.eclipse.emf.emfstore.client.test.common.util.FilteredSuite;
import org.eclipse.emf.emfstore.client.test.common.util.FilteredSuite.FilteredSuiteParameter;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Suite for all fuzzy tests.
 * 
 * @author Julian Sommerfeldt
 * 
 */
@RunWith(FilteredSuite.class)
@FilteredSuiteParameter({ "runFuzzyTests" })
@Suite.SuiteClasses({
	CrossResourceReferencesMutatorTest.class,
	OperationApplyTest.class,
	// OperationReverseTest.class, // TODO: currently fails!
	FuzzyProjectConfigTest.class
})
public class AllFuzzyTests {

}
