/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.startup;

import java.util.Set;

import org.eclipse.emf.emfstore.internal.server.EMFStoreInterface;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.AccessControlImpl;
import org.eclipse.emf.emfstore.internal.server.connection.ConnectionHandler;
import org.eclipse.emf.emfstore.internal.server.model.ServerSpace;

/**
 * Interface for post startup listener. Can be used for server plugins.
 *
 * @author Otto
 */
// TODO: replace AccessControlImpl with AccesControl iface
public interface PostStartupListener {

	/**
	 * Is called post startup.
	 *
	 * @param serverspace serverspace
	 * @param accessControl accesscontrol
	 * @param connectionHandlers set of connection handler
	 */
	void postStartUp(ServerSpace serverspace, AccessControlImpl accessControl,
		Set<ConnectionHandler<? extends EMFStoreInterface>> connectionHandlers);

}
