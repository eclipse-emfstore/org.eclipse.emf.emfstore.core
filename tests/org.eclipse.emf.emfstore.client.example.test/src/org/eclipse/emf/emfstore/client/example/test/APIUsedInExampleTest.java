/*******************************************************************************
 * Copyright 2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Maximilian Koegel
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.example.test;

import java.util.concurrent.Callable;

import org.eclipse.emf.emfstore.client.test.common.cases.ESTestWithLoggedInUser;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the API used in the example plugins.
 * 
 * @author mkoegel
 * 
 */
public class APIUsedInExampleTest extends ESTestWithLoggedInUser {

	@BeforeClass
	public static void beforeClass() {
		startEMFStore();
	}

	@AfterClass
	public static void afterClass() {
		stopEMFStore();
	}

	@Test
	public void testHelloWorldExample() throws ESException {
		RunESCommand.WithException.run(ESException.class, new Callable<Void>() {

			public Void call() throws Exception {
				org.eclipse.emf.emfstore.example.helloworld.Application.runClient(getServer());
				return null;
			}
		}, getProjectSpace().getContentEditingDomain());
	}

	@Test
	public void testMergeExample() throws ESException {
		RunESCommand.WithException.run(ESException.class, new Callable<Void>() {
			public Void call() throws Exception {
				org.eclipse.emf.emfstore.example.helloworld.Application.runClient(getServer());
				org.eclipse.emf.emfstore.example.merging.Application.runClient(getServer());
				return null;
			}
		}, getProjectSpace().getContentEditingDomain());
	}

}
