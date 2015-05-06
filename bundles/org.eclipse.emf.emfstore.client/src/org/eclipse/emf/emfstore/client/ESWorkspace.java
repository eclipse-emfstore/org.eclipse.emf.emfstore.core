/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto von Wesendonk, Edgar Mueller - initial API and implementation
 * Edgar Mueller - API annotations
 ******************************************************************************/
package org.eclipse.emf.emfstore.client;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.client.exceptions.ESServerNotFoundException;

/**
 * Container for all local projects and available servers.
 *
 * @author emueller
 * @author wesendon
 *
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ESWorkspace {

	/**
	 * Returns all local projects.
	 *
	 * @return list of local projects
	 */
	List<ESLocalProject> getLocalProjects();

	/**
	 * Creates a new local project that is not shared with the server yet.
	 *
	 * @param projectName
	 *            the project's name
	 * @return the unshared local project
	 */
	ESLocalProject createLocalProject(String projectName);

	/**
	 * Returns the {@link ESLocalProject} the given model element is contained in.
	 *
	 * @param modelElement
	 *            the model element whose project should be returned
	 * @return the local project the given model element is contained in
	 */
	ESLocalProject getLocalProject(EObject modelElement);

	/**
	 * Returns all projects whose name matches the supplied project name ignoring
	 * any case. This method returns a set since project names do not have to be unique
	 * in EMFStore. Furthermore, a project may have been checked-out multiple times, possibly
	 * in different versions.
	 *
	 * @param projectName
	 *            the project name
	 * @return a set of {@link ESLocalProject ESLocalProjects} that match the given name. If no such
	 *         project has been found, an empty set is returned
	 * @since 1.5
	 *
	 */
	Set<ESLocalProject> getLocalProjectByName(String projectName);

	/**
	 * Returns all available servers.
	 *
	 * @return a list of servers
	 */
	List<ESServer> getServers();

	/**
	 * Adds a server to the workspace. If the server is already contained in the workspace, i.e.
	 * a server with the given URL and port exists, the latter will be returned instead of the passed one.
	 *
	 *
	 * @param server
	 *            the server to be added to the workspace
	 * @return the added server instance, or, if a server with the given URL and port already exists in
	 *         the workspace, the existing server instance
	 */
	ESServer addServer(ESServer server);

	/**
	 * Removes a server from the workspace.
	 *
	 * @param server
	 *            the server to be removed from the workspace
	 *
	 * @throws ESServerNotFoundException
	 *             in case the server couldn't be found in the workspace
	 */
	void removeServer(ESServer server) throws ESServerNotFoundException;
}