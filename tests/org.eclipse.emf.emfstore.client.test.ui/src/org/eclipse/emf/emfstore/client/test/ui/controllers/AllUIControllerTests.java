/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.test.ui.controllers;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test Suite for running all UI controllers tests.
 *
 * @author emueller
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	NoLocalChangesCommitControllerTest.class,
	LocalProjectNeedsToBeUpdatedCommitControllerTest.class,
	UIAddTagControllerTest.class,
	UIAskForBranchCheckoutControllerTest.class,
	UIBranchControllersTest.class,
	UICheckoutControllerTest.class,
	UICreateRemoteProjectControllerTest.class,
	UIDeleteRemoteProjectControllerTest.class,
	UIMergeControllerTest.class,
	UIRevertCommitControllerTest.class,
	UIRemoveServerControllerTest.class,
	UIRevertCommitControllerTest2.class,
	UIServerControllerTest.class,
	UISessionControllerTest.class,
	UIShareProjectControllerTest.class,
	UIUpdateProjectControllerTest.class,
	UIUpdateProjectToVersionControllerTest.class,
	UIPagedUpdateProjectControllerTest.class,
	UIShowHistoryControllerTest.class,
	UIShowHistoryControllerForElementTest.class,
	UIUndoLastOperationControllerTest.class,
	UIHistoryViewCloseTest.class,
})

public class AllUIControllerTests {

}
