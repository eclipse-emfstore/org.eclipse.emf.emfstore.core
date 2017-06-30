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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.emfstore.client.ESWorkspaceProvider;
import org.eclipse.emf.emfstore.client.util.ESVoidCallable;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.fuzzy.emf.ESEMFDataProvider;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.Annotations.DataProvider;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.ESFuzzyRunner;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.impl.WorkspaceImpl;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.modelmutator.ESModelMutatorConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Class to test serialization functionality of the {@link ESWorkspaceProvider}.
 *
 * @author Julian Sommerfeldt
 *
 */
@RunWith(ESFuzzyRunner.class)
@DataProvider(ESEMFDataProvider.class)
public class SerializationTest extends FuzzyProjectTest {

	/**
	 * Load and save a {@link ProjectSpace} via {@link ESWorkspaceProvider} and
	 * compares them.
	 */
	@Test
	public void loadAndSaveToResource() {

		// mutate already saved (through importing) projectSpace and save it
		// again
		final ProjectSpace projectSpace = getProjectSpace();
		final ESModelMutatorConfiguration mmc = getModelMutatorConfiguration(projectSpace
			.getProject());

		RunESCommand.run(new ESVoidCallable() {
			@Override
			public void run() {
				getUtil().mutate(mmc);
			}
		});

		projectSpace.save();

		// dispose and reinit WorkspaceManager
		((ESWorkspaceProviderImpl) ESWorkspaceProvider.INSTANCE).dispose();
		ESWorkspaceProviderImpl.init();

		// reload projectSpaces and check for valid state
		final EList<ProjectSpace> projectSpaces = ((WorkspaceImpl) ESWorkspaceProvider.INSTANCE
			.getWorkspace()).getProjectSpaces();
		if (projectSpaces.size() != 1) {
			throw new IllegalStateException(
				"There must be exactly one projectSpace in the workspace! Current value: "
					+ projectSpaces.size());
		}

		// compare
		final ProjectSpace reloadedProjectSpace = projectSpaces.get(0);
		try {
			if (!ModelUtil.areEqual(reloadedProjectSpace.getProject(),
				projectSpace.getProject())) {
				fail(reloadedProjectSpace.getProject(),
					projectSpace.getProject());
			}
		} finally {
			// set projectSpace to the reloaded one to ensure correct cleanup
			setProjectSpace(reloadedProjectSpace);
		}
	}

	@Override
	public boolean projectSpaceCopyNeeded() {
		return false;
	}
}
