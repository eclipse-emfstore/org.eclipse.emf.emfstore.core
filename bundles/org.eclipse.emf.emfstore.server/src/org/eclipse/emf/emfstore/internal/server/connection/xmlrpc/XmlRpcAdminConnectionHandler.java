/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto von Wesendonk - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.connection.xmlrpc;

import org.eclipse.emf.emfstore.internal.server.AdminEmfStore;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.AccessControl;
import org.eclipse.emf.emfstore.internal.server.connection.ConnectionHandler;
import org.eclipse.emf.emfstore.server.ESXmlRpcWebServerProvider;
import org.eclipse.emf.emfstore.server.exceptions.ESServerInitException;

/**
 * Connection Handler for XML RPC AdminEmfstore interface.
 *
 * @author wesendon
 */
public class XmlRpcAdminConnectionHandler implements ConnectionHandler<AdminEmfStore> {

	/**
	 * String interface identifier.
	 */
	public static final String ADMINEMFSTORE = "AdminEmfStore"; //$NON-NLS-1$

	private static final String NAME = "XML RPC Admin Connection Handler"; //$NON-NLS-1$

	private static AdminEmfStore adminEmfStore;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void init(AdminEmfStore adminEmfStore, AccessControl accessControl)
		throws ESServerInitException {
		XmlRpcAdminConnectionHandler.adminEmfStore = adminEmfStore;
		final ESXmlRpcWebServerProvider webServer = XmlRpcWebserverManager.getInstance();
		webServer.initServer();
		webServer.addHandler(ADMINEMFSTORE, XmlRpcAdminEmfStoreImpl.class);
	}

	/**
	 * Returns the admin interface for EMFStore.
	 *
	 * @return the admin interface for EMFStore
	 */
	public static AdminEmfStore getAdminEmfStore() {
		return adminEmfStore;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.connection.ConnectionHandler#stop()
	 */
	@Override
	public void stop() {
		final ESXmlRpcWebServerProvider webserverManager = XmlRpcWebserverManager.getInstance();
		if (!webserverManager.removeHandler(ADMINEMFSTORE)) {
			webserverManager.stopServer();
		}
	}

}
