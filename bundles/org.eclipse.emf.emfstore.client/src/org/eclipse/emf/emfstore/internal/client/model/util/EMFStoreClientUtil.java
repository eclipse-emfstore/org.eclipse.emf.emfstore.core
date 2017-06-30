/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Maximilian Koegel - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.util;

import java.text.MessageFormat;

import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.ModelFactory;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.ServerInfo;
import org.eclipse.emf.emfstore.internal.client.model.Usersession;
import org.eclipse.emf.emfstore.internal.client.model.connectionmanager.KeyStoreManager;
import org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceBase;
import org.eclipse.emf.emfstore.internal.client.model.impl.WorkspaceBase;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESLocalProjectImpl;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESWorkspaceImpl;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.model.versioning.BranchVersionSpec;
import org.eclipse.emf.emfstore.server.exceptions.ESException;

/**
 * Utility class for EMFStore clients to ease connecting to the server.
 *
 * @author koegel
 */
public final class EMFStoreClientUtil {

	private static final String MSG_FORMAT = "{0}: {1}"; //$NON-NLS-1$
	private static final String LOCALHOST_GENERATED_ENTRY_NAME = "EMFStore (generated entry)"; //$NON-NLS-1$

	/**
	 * Private constructor for utility class.
	 */
	private EMFStoreClientUtil() {
		// do nothing
	}

	/**
	 * Gives a server info for a given port and URL. Searches first for already existing ones. If the search fails, it
	 * creates a new one and registers it for later lookup.
	 *
	 * @param url the server URL (e.g. IP address or DNS name)
	 * @param port the server port
	 * @return a server info
	 */
	public static ServerInfo giveServerInfo(String url, int port) {

		final ESWorkspaceImpl workspace = ESWorkspaceProviderImpl.getInstance().getWorkspace();

		for (final ServerInfo existingServerInfo : workspace.toInternalAPI().getServerInfos()) {
			if (existingServerInfo.getName().equals(LOCALHOST_GENERATED_ENTRY_NAME)) {
				if (url.equals(existingServerInfo.getUrl()) && port == existingServerInfo.getPort()) {
					return existingServerInfo;
				}
			}
		}
		final ServerInfo serverInfo = createServerInfo(url, port, null);
		workspace.toInternalAPI().getServerInfos().add(serverInfo);
		// TODO: OTS
		((WorkspaceBase) workspace.toInternalAPI()).save();
		return serverInfo;
	}

	/**
	 * Create a server info for a given port and URL.
	 *
	 * @param url the server URL (e.g. IP address or DNS name)
	 * @param port the server port
	 * @param certificateAlias the certificateAlias
	 * @return a server info
	 */
	public static ServerInfo createServerInfo(String url, int port, String certificateAlias) {
		final ServerInfo serverInfo = ModelFactory.eINSTANCE.createServerInfo();
		serverInfo.setName(LOCALHOST_GENERATED_ENTRY_NAME);
		serverInfo.setUrl(url);
		serverInfo.setPort(port);
		if (certificateAlias == null) {
			serverInfo.setCertificateAlias(KeyStoreManager.DEFAULT_CERTIFICATE);
		} else {
			serverInfo.setCertificateAlias(certificateAlias);
		}
		return serverInfo;
	}

	/**
	 * Create a default user session with the default super user and password and a server on localhost on the default
	 * port.
	 *
	 * @return a user session
	 */
	public static Usersession createUsersession() {
		return createUsersession("super", "super", "localhost", 8080); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Create a {@link Usersession} for the given credentials and server info.
	 *
	 * @param username the user name
	 * @param password the password
	 * @param serverUrl server URL
	 * @param serverPort server port
	 * @return a user session
	 */
	public static Usersession createUsersession(String username, String password, String serverUrl, int serverPort) {
		final ESWorkspaceImpl workspace = ESWorkspaceProviderImpl.getInstance().getWorkspace();
		for (final Usersession usersession : workspace.toInternalAPI().getUsersessions()) {
			final ServerInfo existingServerInfo = usersession.getServerInfo();
			if (existingServerInfo != null && existingServerInfo.getName().equals(LOCALHOST_GENERATED_ENTRY_NAME)
				&& existingServerInfo.getUrl().equals(serverUrl) && existingServerInfo.getPort() == serverPort) {
				final String encPassword = KeyStoreManager.getInstance().encrypt(password, existingServerInfo);
				if (username.equals(usersession.getUsername()) && encPassword.equals(usersession.getPassword())) {
					return usersession;
				}
			}
		}
		final Usersession usersession = ModelFactory.eINSTANCE.createUsersession();
		usersession.setServerInfo(giveServerInfo(serverUrl, serverPort));
		usersession.setUsername(username);
		usersession.setPassword(password);
		workspace.toInternalAPI().getUsersessions().add(usersession);
		// TODO: OTS
		((WorkspaceBase) workspace.toInternalAPI()).save();
		return usersession;
	}

	/**
	 * Checks, if the given credentials can be authenticated at the given server.
	 *
	 * @param username the user name
	 * @param password the password
	 * @param serverUrl server url
	 * @param serverPort server port
	 * @param certificateAlias the certificateAlias
	 * @return true, if user name & password are right
	 * @throws ESException Problem with the EMFStore Server
	 */
	public static boolean dryLogin(String username, String password, String serverUrl, int serverPort,
		String certificateAlias) throws ESException {
		final Usersession usersession = ModelFactory.eINSTANCE.createUsersession();
		usersession.setServerInfo(createServerInfo(serverUrl, serverPort, certificateAlias));
		usersession.setUsername(username);
		usersession.setPassword(password);
		try {
			usersession.logIn();
		} catch (final AccessControlException e) {
			return false;
		}
		return true;
	}

	/**
	 * Determine if the contents of two projects are equal.
	 *
	 * @param projectA a project
	 * @param projectB another project
	 * @return true if the projects´ contents are identical
	 */
	public static boolean areEqual(ESLocalProject projectA, ESLocalProject projectB) {
		final ProjectSpace projectSpaceA = ((ESLocalProjectImpl) projectA).toInternalAPI();
		final ProjectSpace projectSpaceB = ((ESLocalProjectImpl) projectB).toInternalAPI();

		return ModelUtil.areEqual(projectSpaceA.getProject(), projectSpaceB.getProject());
	}

	/**
	 * Logs fine grained details about the state of a project action.
	 *
	 * @param loggingPrefix the logging prefix
	 * @param message the message describing the current state
	 * @param projectSpace the project space
	 * @param branchSpec the branch spec
	 * @param usersession the usersession
	 */
	public static void logProjectDetails(
		String loggingPrefix,
		String message,
		ProjectSpaceBase projectSpace,
		BranchVersionSpec branchSpec,
		Usersession usersession) {
		if (!Boolean.getBoolean("emfstore.logDetails")) { //$NON-NLS-1$
			return;
		}
		String branch = null;
		if (branchSpec != null) {
			branch = branchSpec.getBranch();
		}
		EMFStoreClientUtil.logProjectDetails(loggingPrefix, message, projectSpace, branch, usersession);
	}

	/**
	 * Logs fine grained details about the state of a project action.
	 *
	 * @param loggingPrefix the logging prefix
	 * @param message the message describing the current state
	 * @param projectSpace the project space
	 * @param branch the branch name
	 * @param usersession the usersession
	 */
	public static void logProjectDetails(
		String loggingPrefix,
		String message,
		ProjectSpaceBase projectSpace,
		String branch,
		Usersession usersession) {
		if (!Boolean.getBoolean("emfstore.logDetails")) { //$NON-NLS-1$
			return;
		}
		String projectName = null;
		String projectId = null;
		int revision = -1;
		String user = null;

		if (projectSpace != null) {
			projectName = projectSpace.getProjectName();
			if (projectSpace.getProjectId() != null) {
				projectId = projectSpace.getProjectId().getId();
			}
			if (projectSpace.getBaseVersion() != null) {
				revision = projectSpace.getBaseVersion().getIdentifier();
			}
		}

		if (usersession != null) {
			user = usersession.getUsername();
		}

		ModelUtil.logProjectDetails(
			MessageFormat.format(MSG_FORMAT, loggingPrefix, message),
			user,
			projectName,
			projectId,
			branch,
			revision);
	}

	/**
	 * Logs fine grained details about the state of a project action.
	 *
	 * @param loggingPrefix the logging prefix
	 * @param message the message describing the current state
	 * @param projectIdO the project id
	 * @param branchSpec the branch spec
	 */
	public static void logProjectDetails(
		String loggingPrefix,
		String message,
		ProjectId projectIdO,
		BranchVersionSpec branchSpec) {
		if (!Boolean.getBoolean("emfstore.logDetails")) { //$NON-NLS-1$
			return;
		}

		String branch = null;
		if (branchSpec != null) {
			branch = branchSpec.getBranch();
		}

		EMFStoreClientUtil.logProjectDetails(loggingPrefix, message, projectIdO, branch);
	}

	/**
	 * Logs fine grained details about the state of a project action.
	 *
	 * @param loggingPrefix the logging prefix
	 * @param message the message describing the current state
	 * @param projectIdO the project id
	 * @param branch the branch spec
	 */
	public static void logProjectDetails(
		String loggingPrefix,
		String message,
		ProjectId projectIdO,
		String branch) {

		if (!Boolean.getBoolean("emfstore.logDetails")) { //$NON-NLS-1$
			return;
		}
		String projectId = null;
		if (projectIdO != null) {
			projectId = projectIdO.getId();
		}

		ModelUtil.logProjectDetails(
			MessageFormat.format(MSG_FORMAT, loggingPrefix, message),
			null,
			null,
			projectId,
			branch,
			-1);
	}

	/**
	 * Logs fine grained details about the state of a project action.
	 *
	 * @param loggingPrefix the logging prefix
	 * @param message the message describing the current state
	 */
	public static void logProjectDetails(
		String loggingPrefix,
		String message) {

		if (!Boolean.getBoolean("emfstore.logDetails")) { //$NON-NLS-1$
			return;
		}

		ModelUtil.logProjectDetails(
			MessageFormat.format(MSG_FORMAT, loggingPrefix, message),
			null,
			null,
			null,
			null,
			-1);
	}
}
