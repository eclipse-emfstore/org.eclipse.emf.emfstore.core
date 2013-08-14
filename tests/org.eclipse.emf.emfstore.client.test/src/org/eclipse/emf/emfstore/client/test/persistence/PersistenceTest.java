/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.test.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.emf.emfstore.client.test.WorkspaceTest;
import org.eclipse.emf.emfstore.client.test.testmodel.TestmodelFactory;
import org.eclipse.emf.emfstore.internal.client.model.Configuration;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.Workspace;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.SerializationException;
import org.junit.Before;
import org.junit.Test;

public class PersistenceTest extends WorkspaceTest {

	@Before
	public void before() {
		setCompareAtEnd(false);
	}

	@Test
	public void testReinitWorkspace() throws SerializationException {
		Configuration.getClientBehavior().setAutoSave(false);
		Project originalProject = ModelUtil.clone(ESWorkspaceProviderImpl.getInstance().getWorkspace().toInternalAPI()
			.getProjectSpaces().get(0).getProject());

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				getProject().addModelElement(TestmodelFactory.eINSTANCE.createTestElement());
			}
		}.run(getProject(), false);

		assertEquals(
			ESWorkspaceProviderImpl.getInstance().getWorkspace().getLocalProjects().get(0).getModelElements().size(), 1);
		ESWorkspaceProviderImpl.getInstance().dispose();
		ESWorkspaceProviderImpl.getInstance().load();
		Workspace internalWorkspace = ESWorkspaceProviderImpl.getInstance().getWorkspace().toInternalAPI();
		Project project = internalWorkspace.getProjectSpaces().get(0).getProject();
		assertTrue(ModelUtil.areEqual(project, originalProject));
		// set new projectspace since old one got disposed but WorkspaceTest still holds a reference and tries to save
		// it in teardown
		setProjectSpace(internalWorkspace.getProjectSpaces().get(0));
	}

}