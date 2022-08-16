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
package org.eclipse.emf.emfstore.client.test.common.config;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.emfstore.internal.server.DefaultServerWorkspaceLocationProvider;

public class ServerLocationProvider extends DefaultServerWorkspaceLocationProvider {

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.DefaultServerWorkspaceLocationProvider#getRootDirectory()
	 */
	@Override
	protected String getRootDirectory() {
		final String rootDir = System.getenv("EMFSTORE_TEST_SERVER_ROOT_DIR"); //$NON-NLS-1$
		if (rootDir != null && rootDir.length() > 0) {
			return rootDir;
		}
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
	}
}
