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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.emfstore.client.ESWorkspaceProvider;
import org.eclipse.emf.emfstore.client.util.ESVoidCallable;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.fuzzy.emf.ESEMFDataProvider;
import org.eclipse.emf.emfstore.fuzzy.emf.ESMutateUtil;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.Annotations.Data;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.Annotations.DataProvider;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.Annotations.Options;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.Annotations.Util;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.ESFuzzyRunner;
import org.eclipse.emf.emfstore.internal.client.model.Configuration;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.impl.WorkspaceBase;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESWorkspaceImpl;
import org.eclipse.emf.emfstore.internal.common.CommonUtil;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.modelmutator.ESModelMutatorConfiguration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

/**
 * Abstract super class for tests handling EMFStore projects.
 *
 * @author Julian Sommerfeldt
 *
 */
@RunWith(ESFuzzyRunner.class)
@DataProvider(ESEMFDataProvider.class)
public abstract class FuzzyProjectTest {

	@Data
	private Project project;

	@Util
	private ESMutateUtil util;

	@SuppressWarnings({ "serial" })
	@Options
	private final Map<String, Object> options = new HashMap<String, Object>() {
		{
			put(ESEMFDataProvider.MUTATOR_EDITINGDOMAIN,
				((ESWorkspaceProviderImpl) ESWorkspaceProvider.INSTANCE)
				.getEditingDomain());
		}
	};

	private ProjectSpace projectSpace;

	private ProjectSpace copyProjectSpace;

	/**
	 * Init the testclasses.
	 */
	@BeforeClass
	public static void init() {
		// call once to avoid wrong time in first run
		CommonUtil.setTesting(true);
	}

	@Before
	public void setup() {
		// set testing to true so it uses test profile
		CommonUtil.setTesting(true);

		// necessary to avoid saving of each projectspace
		// which leads to a very slow performance
		Configuration.getClientBehavior().setAutoSave(false);

		// import projects
		RunESCommand.run(new ESVoidCallable() {
			@Override
			public void run() {
				final ESWorkspaceImpl esWorkspaceImpl = (ESWorkspaceImpl) ESWorkspaceProvider.INSTANCE
					.getWorkspace();
				final WorkspaceBase workspace = (WorkspaceBase) esWorkspaceImpl.toInternalAPI();
				projectSpace = workspace.createLocalProject("testProject", project, false);

				if (projectSpaceCopyNeeded()) {
					copyProjectSpace = ((WorkspaceBase) esWorkspaceImpl
						.toInternalAPI()).cloneProject("", projectSpace.getProject());
				}
			}
		});

	}

	public void clearOperations() {
		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				getProjectSpace().getLocalChangePackage().clear();
				getProjectSpace().getOperationManager().clearOperations();
				return null;
			}
		});
	}

	@After
	public void tearDown() {
		RunESCommand.run(new Callable<Void>() {
			public Void call() throws Exception {
				try {
					final ESWorkspaceImpl currentWorkspace = (ESWorkspaceImpl) ESWorkspaceProvider.INSTANCE
						.getWorkspace();
					final EList<ProjectSpace> projectSpaces = currentWorkspace.toInternalAPI().getProjectSpaces();
					if (projectSpace != null
						&& projectSpaces.contains(projectSpace)) {
						projectSpace.delete(new NullProgressMonitor());
					}
					if (copyProjectSpace != null
						&& projectSpaces.contains(copyProjectSpace)) {
						copyProjectSpace.delete(new NullProgressMonitor());
					}
				} catch (final FileNotFoundException e) {
					// do nothing
				} catch (final IOException e) {
					// do nothing
				}

				return null;
			}
		});
	}

	/**
	 * Constructs a new {@link ESModelMutatorConfiguration} based on the existing
	 * one and the given project.
	 *
	 * @param project
	 *            The root object of the {@link ESModelMutatorConfiguration}.
	 * @return The new {@link ESModelMutatorConfiguration}.
	 */
	public ESModelMutatorConfiguration getModelMutatorConfiguration(
		Project project) {
		return getModelMutatorConfiguration(project, util);
	}

	/**
	 * Constructs a new {@link ESModelMutatorConfiguration} based on the given {@link ESMutateUtil} and the given
	 * project.
	 *
	 * @param project
	 *            The root object of the {@link ESModelMutatorConfiguration}.
	 * @param util
	 *            The {@link ESMutateUtil} connected to the {@link ESEMFDataProvider}.
	 * @return The new {@link ESModelMutatorConfiguration}.
	 */
	public static ESModelMutatorConfiguration getModelMutatorConfiguration(
		Project project, ESMutateUtil util) {
		final ESModelMutatorConfiguration mmc = new ESModelMutatorConfiguration(
			util.getEPackages(), project, 1L);
		mmc.seteStructuralFeaturesToIgnore(util
			.getEStructuralFeaturesToIgnore());
		mmc.seteClassesToIgnore(util.getEClassesToIgnore());
		mmc.setEditingDomain(((ESWorkspaceProviderImpl) ESWorkspaceProvider.INSTANCE)
			.getEditingDomain());
		mmc.setMinObjectsCount(util.getMinObjectsCount());
		return mmc;
	}

	/**
	 * Can be overridden by subclasses to modify behavior. Default is <code>true</code>.
	 *
	 * @return Should this class handle/provide also a copy of the projectSpace?
	 */
	public boolean projectSpaceCopyNeeded() {
		return true;
	}

	/**
	 * Notify that a test has failed. Saves the projects for later comparison.
	 *
	 * @param project1
	 *            The first {@link Project}.
	 * @param project2
	 *            The second {@link Project}.
	 */
	public void fail(Project project1, Project project2) {
		fail(project1, project2, util);
	}

	/**
	 * Notify that a test has failed. Saves the projects for later comparison.
	 *
	 * @param project1
	 *            The first {@link Project}.
	 * @param project2
	 *            The second {@link Project}.
	 * @param util
	 *            The {@link ESMutateUtil} connected to the {@link ESEMFDataProvider}.
	 */
	public static void fail(Project project1, Project project2, ESMutateUtil util) {
		save(project1, project2, util);
		Assert.fail("Projects are not equal");
	}

	/**
	 * Saves the projects for e.g. later comparison.
	 *
	 * @param project1
	 *            The first {@link Project}.
	 * @param project2
	 *            The second {@link Project}.
	 * @param util
	 *            The {@link ESMutateUtil} connected to the {@link ESEMFDataProvider}.
	 */
	public static void save(final Project project1, final Project project2,
		final ESMutateUtil util) {
		final Map<Object, Object> options = new HashMap<Object, Object>();
		options.put(XMLResource.OPTION_PROCESS_DANGLING_HREF,
			XMLResource.OPTION_PROCESS_DANGLING_HREF_DISCARD);

		RunESCommand.run(new ESVoidCallable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					ModelUtil.saveEObjectToResource(Arrays.asList(project1),
						util.getRunResourceURI("original"), options);
					ModelUtil.saveEObjectToResource(Arrays.asList(project2),
						util.getRunResourceURI("copy"), options);
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			}
		});

	}

	/**
	 * Compare two projects but ignore the order of the elements.
	 *
	 * @param project1
	 *            The first {@link Project}.
	 * @param project2
	 *            The second {@link Project}.
	 */
	public void compareIgnoreOrder(Project project1, Project project2) {
		compareIgnoreOrder(project1, project2, util);
	}

	/**
	 * Compare two projects but ignore the order of the elements.
	 *
	 * @param project1
	 *            The first {@link Project}.
	 * @param project2
	 *            The second {@link Project}.
	 * @param util
	 *            The {@link ESMutateUtil} connected to the {@link ESEMFDataProvider}.
	 */
	public static void compareIgnoreOrder(Project project1, Project project2,
		ESMutateUtil util) {
		if (project1.getModelElements().size() != project2.getModelElements()
			.size()) {
			fail(project1, project2, util);
		}

		int index = 0;
		// sort elements in modelElement reference of projects to avoid
		// differences only in indices.
		for (final EObject eObject1 : project1.getModelElements()) {
			final ModelElementId modelElementId1 = project1
				.getModelElementId(eObject1);
			final EObject eObject2 = project2.getModelElement(modelElementId1);
			if (eObject2 == null
				|| !project2.getModelElements().contains(eObject2)) {
				fail(project1, project2, util);
			}
			project2.getModelElements().move(index, eObject2);
			index++;
		}

		if (!ModelUtil.areEqual(project1, project2)) {
			fail(project1, project2, util);
		}
	}

	/**
	 * @return The {@link ESMutateUtil} connected with the {@link ESEMFDataProvider} .
	 */
	public ESMutateUtil getUtil() {
		return util;
	}

	/**
	 * @return The {@link ProjectSpace} imported into the WorkSpace.
	 */
	public ProjectSpace getProjectSpace() {
		return projectSpace;
	}

	/**
	 * @param projectSpace
	 *            The {@link ProjectSpace} of the test.
	 */
	public void setProjectSpace(ProjectSpace projectSpace) {
		this.projectSpace = projectSpace;
	}

	/**
	 * @return The {@link ProjectSpace} copied from the original one.
	 * @throws IllegalStateException
	 *             When {@link #projectSpaceCopyNeeded()} returns false.
	 */
	public ProjectSpace getCopyProjectSpace() throws IllegalStateException {
		if (!projectSpaceCopyNeeded()) {
			throw new IllegalStateException(
				"This test does not have a copied projectSpace!");
		}
		return copyProjectSpace;
	}

	/**
	 * @param copyProjectSpace
	 *            The {@link ProjectSpace} copied from the original one.
	 * @throws IllegalStateException
	 *             When {@link #projectSpaceCopyNeeded()} returns false.
	 */
	public void setCopyProjectSpace(ProjectSpace copyProjectSpace)
		throws IllegalStateException {
		if (!projectSpaceCopyNeeded()) {
			throw new IllegalStateException(
				"This test does not have a copied projectSpace!");
		}
		this.copyProjectSpace = copyProjectSpace;
	}
}
