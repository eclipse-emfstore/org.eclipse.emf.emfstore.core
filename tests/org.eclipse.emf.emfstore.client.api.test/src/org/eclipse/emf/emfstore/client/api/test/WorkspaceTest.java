/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.api.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.ESServer;
import org.eclipse.emf.emfstore.client.ESWorkspace;
import org.eclipse.emf.emfstore.client.ESWorkspaceProvider;
import org.eclipse.emf.emfstore.client.exceptions.ESServerNotFoundException;
import org.eclipse.emf.emfstore.internal.client.model.connectionmanager.KeyStoreManager;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESWorkspaceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WorkspaceTest {

	private static final String FAKE_URL = "foo.net"; //$NON-NLS-1$
	private static ESWorkspace workspace;
	private ESLocalProject localProject;

	@BeforeClass
	public static void setUpBeforeClass() {
		workspace = ESWorkspaceProvider.INSTANCE.getWorkspace();
	}

	@Before
	public void setUp() throws Exception {
		assertEquals(0, workspace.getLocalProjects().size());
		localProject = workspace.createLocalProject("TestProject"); //$NON-NLS-1$
	}

	@After
	public void tearDown() throws Exception {
		for (final ESLocalProject lp : workspace.getLocalProjects()) {
			lp.delete(new NullProgressMonitor());
		}
	}

	@Test
	public void testCreateLocalProject() {
		assertNotNull(localProject);
		assertEquals(1, workspace.getLocalProjects().size());
		workspace.createLocalProject("TestProject2"); //$NON-NLS-1$
		assertEquals(2, workspace.getLocalProjects().size());
	}

	@Test
	public void testAddServer() {
		final int servers = workspace.getServers().size();
		final ESServer server = ESServer.FACTORY.createServer(FAKE_URL, 1234, KeyStoreManager.DEFAULT_CERTIFICATE);
		workspace.addServer(server);
		assertEquals(servers + 1, workspace.getServers().size());
		try {
			workspace.removeServer(server);
		} catch (final ESServerNotFoundException e) {
			fail(e.getMessage());
		}
		assertEquals(servers, workspace.getServers().size());
	}

	@Test(expected = ESServerNotFoundException.class)
	public void testRemoveNotExistingServer() throws ESServerNotFoundException {
		workspace.removeServer(
			ESServer.FACTORY.createServer(FAKE_URL, 1234, KeyStoreManager.DEFAULT_CERTIFICATE));
	}

	@Test
	public void testRemoveServer() throws ESServerNotFoundException {
		final ESServer server = ESServer.FACTORY.createServer(FAKE_URL, 1234, KeyStoreManager.DEFAULT_CERTIFICATE);
		workspace.addServer(server);
		for (final ESServer s : workspace.getServers()) {
			workspace.removeServer(s);
		}
		assertEquals(0, workspace.getServers().size());
	}

	@Test
	public void testLocalGetProjectByName() {
		final ESLocalProject firstProject = workspace.createLocalProject("foo"); //$NON-NLS-1$
		final ESLocalProject secondProject = workspace.createLocalProject("foo"); //$NON-NLS-1$
		workspace.createLocalProject("bar"); //$NON-NLS-1$

		final Set<ESLocalProject> projects = ESWorkspaceImpl.class.cast(
			workspace).getLocalProjectByName("foo"); //$NON-NLS-1$
		assertThat(projects, hasSize(2));
		assertThat(projects, contains(firstProject, secondProject));
	}
}
