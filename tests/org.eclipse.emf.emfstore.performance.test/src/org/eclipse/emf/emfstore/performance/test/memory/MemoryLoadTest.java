/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * TobiasVerhoeven
 ******************************************************************************/
package org.eclipse.emf.emfstore.performance.test.memory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.ESRemoteProject;
import org.eclipse.emf.emfstore.common.ESSystemOutProgressMonitor;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.ESDefaultModelMutator;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESLocalProjectImpl;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESWorkspaceImpl;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommandWithResult;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.server.exceptions.FatalESException;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.versionspec.ESPrimaryVersionSpecImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersionSpec;
import org.eclipse.emf.emfstore.modelmutator.ESModelMutatorConfiguration;
import org.eclipse.emf.emfstore.modelmutator.ESModelMutatorUtil;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.model.versionspec.ESPrimaryVersionSpec;
import org.eclipse.emf.emfstore.server.model.versionspec.ESVersionSpec;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests for creating big amounts of data in order to test memory management.
 * 
 * @author Tobias Verhoeven
 */
@SuppressWarnings("restriction")
public class MemoryLoadTest {

	private final String modelKey = "http://org/eclipse/example/bowling";
	private final long seed = 47209572905723L;
	private final boolean keepLocalData = false; // weather client data should
													// be kept

	private static final Logger LOGGER = Logger
			.getLogger("org.eclipse.emf.emfstore.client.test");
	private long currentProjectCount; // The current project count.
	private ESModelMutatorConfiguration currentProjectConfiguration;
	private static final ESSystemOutProgressMonitor MONITOR = new ESSystemOutProgressMonitor();

	/** Class Rule for starting an EMFStore-Server. */
	// @ClassRule
	private static RunningEMFStoreRule runningEMFStoreRule = new RunningEMFStoreRule();

	/** Rule for deleting all remote projects. */
	@Rule
	public NoRemoteProjectRule noRemoteProjectRule = new NoRemoteProjectRule(
			runningEMFStoreRule);

	/**
	 * Starts the EMFstore.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws FatalESException
	 *             the fatal es exception
	 * @throws ESException
	 *             the eS exception
	 */
	@BeforeClass
	public static void before() throws IOException, FatalESException,
			ESException {
		runningEMFStoreRule.before();
	}

	/**
	 * Stops the EMFStore.
	 */
	@AfterClass
	public static void after() {
		runningEMFStoreRule.after();
	}

	/**
	 * Test for solely sharing projects.
	 * 
	 * @throws ESException
	 *             in case of an error
	 */
	@Test
	public void shareProjectLoadTest() throws ESException {
		shareProjectsLoadTest(100, 1000);
		// shareProjectsLoadTest(1000, 10);
		// shareProjectsLoadTest(10000, 1000);
	}

	private void shareProjectsLoadTest(int minProjectSize, int projectCount)
			throws ESException {
		for (int i = 0; i < projectCount; i++) {
			final ESLocalProject project = generateRandomProject(minProjectSize);
			long time = System.nanoTime();
			project.shareProject(runningEMFStoreRule.defaultSession(), null);
			time = System.nanoTime() - time;
			log("Shared Project: " + project.getProjectName() + " ,Memory: "
					+ usedMemoryInMib() + " MiB");

			deleteLocallyIfNeeded(project);
			log("Shared Project: " + project.getProjectName() + " ,Memory: "
					+ usedMemoryInMib() + " MiB");
		}
	}

	/**
	 * Test for sharing and checking out projects.
	 * 
	 * @throws ESException
	 *             in case of an error
	 */
	@Test
	public void shareCheckoutProjectLoadTest() throws ESException {
		shareCheckoutProjectsLoadTest(1000, 90);
		// shareCheckoutProjectsLoadTest(10000, 1000);
	}

	private void shareCheckoutProjectsLoadTest(int minProjectSize,
			int projectCount) throws ESException {
		final long start = currentProjectCount;
		final int[] sizes = new int[projectCount];

		for (int i = 0; i < projectCount; i++) {
			final ESLocalProject project = generateRandomProject(minProjectSize);

			project.shareProject(runningEMFStoreRule.defaultSession(), null);
			deleteLocallyIfNeeded(project);
			sizes[i] = project.getAllModelElements().size();

			log("Shared Project: " + project.getProjectName() + " ,Memory: "
					+ usedMemoryInMib() + " MiB");
		}

		int i = 0;
		for (final ESRemoteProject remoteProject : runningEMFStoreRule.server()
				.getRemoteProjects(runningEMFStoreRule.defaultSession())) {
			final ESLocalProject project = remoteProject.checkout(
					"Generated project_" + (start + i), MONITOR);
			assertEquals("Generated project_" + (start + i),
					project.getProjectName());
			// Assert.assertEquals(sizes[i],
			// project.getAllModelElements().size());

			deleteLocallyIfNeeded(project);
			log("Checked out Project: " + project.getProjectName() + "Memory: "
					+ usedMemoryInMib() + " MiB");
			i++;
		}

	}

	/**
	 * Test for committing changes.
	 * 
	 * @throws ESException
	 *             in case of an error
	 */
	@Test
	public void commitLoadTest() throws ESException {
		commitLoadTest(1000, 1, 200, 1000);
		// commitLoadTest(1000, 1, 2500, 1000);
	}

	private void commitLoadTest(int minProjectSize, int projectCount,
			int historySize, int minChangeSize) throws ESException {
		for (int i = 0; i < projectCount; i++) {

			final ESLocalProject project = generateRandomProject(minProjectSize);
			project.shareProject(runningEMFStoreRule.defaultSession(), null);

			for (int z = 0; z < historySize; z++) {
				mutateProject(project, minChangeSize);
				commitProject(project);

				log("Committed Change: " + z + " of Project "
						+ project.getProjectName() + " ,Memory: "
						+ usedMemoryInMib() + " MiB");
			}
			deleteLocallyIfNeeded(project);
		}
	}

	/**
	 * Test for committing changes and checking out resulting projectstates.
	 * 
	 * @throws ESException
	 *             in case of an error
	 */
	@Test
	public void commitCheckoutLoadTest() throws ESException {
		// commitCheckoutLoadTest(1000, 1, 1000, 100);
		commitCheckoutLoadTest(1000, 1, 50, 1000, 2);
		// commitCheckoutLoadTest(1000, 1, 1000, 1000,333);
	}

	private void commitCheckoutLoadTest(int minProjectSize, int projectCount,
			int historySize, int minChangeSize, int checkoutStep)
			throws ESException {

		for (int i = 0; i < projectCount; i++) {

			final ESLocalProject project = generateRandomProject(minProjectSize);
			project.shareProject(runningEMFStoreRule.defaultSession(), null);

			final List<ESPrimaryVersionSpec> versions = new ArrayList<ESPrimaryVersionSpec>();

			for (int z = 0; z < historySize; z++) {
				mutateProject(project, minChangeSize);
				versions.add(commitProject(project));
				log("Committed Change: " + z + " of Project "
						+ project.getProjectName() + " ,Memory: "
						+ usedMemoryInMib() + " MiB");

			}
			deleteLocallyIfNeeded(project);

			for (int x = 0; x < versions.size(); x += checkoutStep) {
				log("Checking out version: " + versions.get(x).getIdentifier());

				final ESLocalProject projectCopy = project.getRemoteProject()
						.checkout(project.getProjectName() + "_Copy" + x,
								runningEMFStoreRule.defaultSession(),
								versions.get(x), MONITOR);

				log("Checked out version: " + versions.get(x).getIdentifier()
						+ " ,Memory: " + usedMemoryInMib() + " MiB");
				deleteLocallyIfNeeded(projectCopy);
			}
		}
	}

	/**
	 * Shares projects, commits changes and updates local versions.
	 * 
	 * @throws ESException
	 *             in case of an error
	 */
	@Test
	public void updateLoadTest() throws ESException {
		updateLoadTest(1000, 1, 10, 10);
	}

	private void updateLoadTest(int minProjectSize, int projectCount,
			int historySize, int minChangeSize) throws ESException {

		for (int i = 0; i < projectCount; i++) {

			final ESLocalProject project = generateRandomProject(minProjectSize);
			project.shareProject(runningEMFStoreRule.defaultSession(), null);

			final ESLocalProject projectSecondCheckout = project
					.getRemoteProject().checkout(
							project.getProjectName() + "_SecondCheckout_" + i,
							runningEMFStoreRule.defaultSession(),
							project.getBaseVersion(), null);

			final List<ESVersionSpec> versions = new ArrayList<ESVersionSpec>();

			for (int z = 0; z < historySize; z++) {
				mutateProject(project, minChangeSize);
				versions.add(commitProject(project));
				log("Memory: " + usedMemoryInMib() + " MiB");

			}
			deleteLocallyIfNeeded(project);

			projectSecondCheckout.update(
					versions.get((versions.size() - 1) / 2), null, null);
			projectSecondCheckout.update(versions.get(1), null, null);
			projectSecondCheckout.update(null);
			deleteLocallyIfNeeded(projectSecondCheckout);

			log("Memory: " + usedMemoryInMib() + " MiB");

		}
	}

	/**
	 * Shares projects, commits changes and retrieves specific changePackages.
	 * 
	 * @throws ESException
	 *             in case of an error
	 */
	@Test
	public void getChangePackagesLoadTest() throws ESException {
		commitGetChangesLoadTest(1000, 1, 50, 100);
	}

	private void commitGetChangesLoadTest(int minProjectSize, int projectCount,
			int historySize, int minChangeSize) throws ESException {

		for (int i = 0; i < projectCount; i++) {

			final ESLocalProject project = generateRandomProject(minProjectSize);
			project.shareProject(runningEMFStoreRule.defaultSession(), null);

			final List<VersionSpec> versions = new ArrayList<VersionSpec>();

			for (int z = 0; z < historySize; z++) {
				mutateProject(project, minChangeSize);
				versions.add(((ESPrimaryVersionSpecImpl) commitProject(project))
						.toInternalAPI());

			}
			((ESLocalProjectImpl) project).toInternalAPI().getChanges(
					versions.get(0), versions.get(versions.size() - 1));

			deleteLocallyIfNeeded(project);
			log("Memory: " + usedMemoryInMib() + " MiB");

		}
	}

	private long usedMemoryInMib() {
		return Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory() >> 20;
	}

	private ESLocalProject generateRandomProject(int minProjectSize) {
		final String projectName = "Generated project_" + currentProjectCount;
		final Project project = org.eclipse.emf.emfstore.internal.common.model.ModelFactory.eINSTANCE
				.createProject();

		final ESModelMutatorConfiguration config = createModelMutatorConfigurationRandom(
				modelKey, project, minProjectSize, seed);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				ESDefaultModelMutator.generateModel(config);
			}
		}.run(false);

		final ProjectSpace projectSpace = ((ESWorkspaceImpl) runningEMFStoreRule
				.connectedWorkspace()).toInternalAPI().importProject(project,
				projectName, "");

		currentProjectCount++;
		return projectSpace.toAPI();
	}

	private void mutateProject(final ESLocalProject project, int minChangeSize) {

		if (currentProjectConfiguration == null
				|| currentProjectConfiguration.getRootEObject() != ((ESLocalProjectImpl) project)
						.toInternalAPI().getProject()) {

			currentProjectConfiguration = createModelMutatorConfigurationRandom(
					modelKey, ((ESLocalProjectImpl) project).toInternalAPI()
							.getProject(), minChangeSize, seed);
		}
		currentProjectConfiguration.setMinObjectsCount(minChangeSize);

		final ESModelMutatorConfiguration mmc = currentProjectConfiguration;

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				final long time = System.currentTimeMillis();
				ESDefaultModelMutator.changeModel(mmc);
				System.out.println("Changed model: "
						+ (System.currentTimeMillis() - time) / 1000.0 + "sec");
			}
		}.run(false);
	}

	private ESModelMutatorConfiguration createModelMutatorConfigurationRandom(
			String modelKey, EObject rootObject, int minObjectsCount, long seed) {

		final ESModelMutatorConfiguration config = new ESModelMutatorConfiguration(
				ESModelMutatorUtil.getEPackage(modelKey), rootObject, seed);

		config.setIgnoreAndLog(false);
		config.setMinObjectsCount(minObjectsCount);

		final List<EStructuralFeature> eStructuralFeaturesToIgnore = new ArrayList<EStructuralFeature>();

		config.setEditingDomain(((ESWorkspaceImpl) runningEMFStoreRule
				.connectedWorkspace()).toInternalAPI().getEditingDomain());
		config.seteStructuralFeaturesToIgnore(eStructuralFeaturesToIgnore);
		return config;
	}

	private ESPrimaryVersionSpec commitProject(final ESLocalProject project) {
		return new EMFStoreCommandWithResult<ESPrimaryVersionSpec>() {
			@Override
			protected ESPrimaryVersionSpec doRun() {
				try {
					return project.commit(null);
				} catch (final ESException e) {
					fail();
					return null;
				}
			}
		}.run(false);
	}

	private void deleteLocallyIfNeeded(final ESLocalProject project) {
		if (!keepLocalData) {
			try {
				project.delete(null);
			} catch (final ESException e) {
				fail();
			} catch (final IOException e) {
				fail();
			}
		}
	}

	private static void log(String s) {
		LOGGER.info(s);
	}
}
