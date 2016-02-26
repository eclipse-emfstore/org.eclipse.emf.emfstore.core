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
package org.eclipse.emf.emfstore.client.test.common.cases;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.ESRemoteProject;
import org.eclipse.emf.emfstore.client.ESWorkspaceProvider;
import org.eclipse.emf.emfstore.client.exceptions.ESServerStartFailedException;
import org.eclipse.emf.emfstore.client.test.common.util.ServerUtil;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESLocalProjectImpl;
import org.eclipse.emf.emfstore.internal.server.EMFStore;
import org.eclipse.emf.emfstore.internal.server.EMFStoreController;
import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.eclipse.emf.emfstore.internal.server.exceptions.FatalESException;
import org.eclipse.emf.emfstore.internal.server.model.ProjectHistory;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.server.exceptions.ESException;

/**
 * Common base class for all tests which need an actual EMFStore server started.
 *
 * @author emueller
 *
 */
public abstract class ESTestWithServer extends ESTest {

	protected static void startEMFStore() {
		startEMFStore(Collections.<String, String> emptyMap());
	}

	/**
	 * Start an EMFStore server instance.
	 */
	public static void startEMFStore(Map<String, String> properties) {
		ServerConfiguration.setProperties(ServerUtil.initProperties(properties));
		ServerConfiguration.setTesting(true);

		try {
			ServerUtil.startServer();
		} catch (final ESServerStartFailedException ex) {
			fail(ex.getMessage());
		}
	}

	public EMFStore getEMFStore() {
		return ESWorkspaceProviderImpl.getInstance().getConnectionManager();
	}

	public ProjectHistory getHistory(ESLocalProject localProject) {
		final ESLocalProjectImpl projectImpl = ESLocalProjectImpl.class.cast(localProject);
		final ProjectId id = projectImpl.toInternalAPI().getProjectId();
		for (final ProjectHistory history : EMFStoreController.getInstance().getServerSpace().getProjects()) {
			if (history.getProjectId().equals(id)) {
				return history;
			}
		}
		throw new RuntimeException("Project History not found"); //$NON-NLS-1$
	}

	public static void stopEMFStore() {
		ServerUtil.stopServer();
		// give the server some time to unbind from it's ips. Not the nicest solution ...
		try {
			Thread.sleep(5000);
		} catch (final InterruptedException ex) {
			fail(ex.getMessage());
		}
	}

	public static void restartEMFStore() {
		stopEMFStore();
		startEMFStore(Collections.<String, String> emptyMap());
	}

	protected static void deleteRemoteProjects() throws IOException, FatalESException, ESException {
		for (final ESRemoteProject project : ESWorkspaceProvider.INSTANCE.getWorkspace().getServers().get(0)
			.getRemoteProjects()) {
			project.delete(new NullProgressMonitor());
		}
	}

}
