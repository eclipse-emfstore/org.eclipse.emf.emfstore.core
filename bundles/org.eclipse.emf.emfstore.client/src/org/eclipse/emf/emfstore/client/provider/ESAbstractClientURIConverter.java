/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.provider;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.emf.emfstore.client.util.ESClientURIUtil;
import org.eclipse.emf.emfstore.internal.common.EMFStoreURIHandler;

/**
 * Abstract URIConverter for normalizing EMFStore URIs on client side. Delegates normalizing to specialized methods
 * which have to be implemented by extenders.
 * 
 * @author jfaltermeier
 * @since 1.1
 * 
 */
public abstract class ESAbstractClientURIConverter extends ExtensibleURIConverterImpl {

	/**
	 * Default constructor.
	 */
	public ESAbstractClientURIConverter() {
		final int index = getURIHandlers().size() - 1;
		getURIHandlers().add(index, new EMFStoreURIHandler());
	}

	@Override
	public URI normalize(URI uri) {
		// emfstore:
		if (uri.scheme() != null && uri.scheme().equals(ESClientURIUtil.SCHEME)) {

			// emfstore://workspaces/0
			if (uri.authority().equals(ESClientURIUtil.CLIENT_SEGMENT)) {

				// emfstore://workspaces/0/workspace
				if (uri.segment(1).equals(ESClientURIUtil.WORKSPACE_SEGMENT)) {
					return normalizeWorkspaceURI(uri.segment(0));
				}

				// emfstore://workspaces/0/projectspaces/<identifier>
				else if (uri.segment(1).equals(ESClientURIUtil.PROJECTSPACES_SEGMENT)) {
					return normalizeProjectSpaces(uri);
				}
			}
		}

		// unexpected
		return super.normalize(uri);
	}

	private URI normalizeProjectSpaces(URI uri) {
		// emfstore://workspaces/0/projectspaces/<identifier>/project
		if (uri.segment(3).equals(ESClientURIUtil.PROJECT_SEGMENT)) {
			return normalizeProjectURI(uri.segment(0), uri.segment(2));
		}

		// emfstore://workspaces/0/projectspaces/<identifier>/operations
		else if (uri.segment(3).equals(ESClientURIUtil.OPERATIONS_SEGMENT)) {
			return normalizeOperationsURI(uri.segment(0), uri.segment(2));
		}

		// emfstore://workspaces/0/projectspaces/<identifier>/projectspace
		else if (uri.segment(3).equals(ESClientURIUtil.PROJECTSPACE_SEGMENT)) {
			return normalizeProjectSpaceURI(uri.segment(0), uri.segment(2));
		}

		// unexpected
		else {
			return super.normalize(uri);
		}
	}

	/**
	 * Normalizes an EMFStore workspace URI.
	 * 
	 * @param profile the selected profile
	 * @return the normalized URI
	 */
	protected abstract URI normalizeWorkspaceURI(String profile);

	/**
	 * Normalizes an EMFStore project URI.
	 * 
	 * @param profile the selected profile
	 * @param projectId the project's id
	 * @return the normalized URI
	 */
	protected abstract URI normalizeProjectURI(String profile, String projectId);

	/**
	 * Normalizes an EMFStore operations URI.
	 * 
	 * @param profile the selected profile
	 * @param projectId the project's id
	 * @return the normalized URI
	 */
	protected abstract URI normalizeOperationsURI(String profile, String projectId);

	/**
	 * Normalizes an EMFStore projectspace URI.
	 * 
	 * @param profile the selected profile
	 * @param projectId the project's id
	 * @return the normalized URI
	 */
	protected abstract URI normalizeProjectSpaceURI(String profile, String projectId);

}
