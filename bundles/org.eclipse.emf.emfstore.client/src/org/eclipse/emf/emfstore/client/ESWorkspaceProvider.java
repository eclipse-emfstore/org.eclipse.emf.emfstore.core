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

import org.eclipse.emf.emfstore.client.sessionprovider.ESAbstractSessionProvider;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;

/**
 * Central access point to receive the current workspace.
 * 
 * @author emueller
 * @author wesendon
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ESWorkspaceProvider {

	/**
	 * Singleton instance of the workspace provider.
	 */
	ESWorkspaceProvider INSTANCE = ESWorkspaceProviderImpl.getInstance();

	/**
	 * Returns the current workspace.
	 * 
	 * @return current workspace.
	 */
	ESWorkspace getWorkspace();

	/**
	 * Allows the user to set the {@link ESAbstractSessionProvider} to use.
	 * 
	 * @param sessionProvider
	 *            the {@link ESAbstractSessionProvider} to use
	 */
	void setSessionProvider(ESAbstractSessionProvider sessionProvider);

}
