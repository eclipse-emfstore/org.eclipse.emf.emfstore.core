/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * emueller
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.handlers;

import org.eclipse.emf.emfstore.internal.client.model.ServerInfo;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESRemoteProjectImpl;
import org.eclipse.emf.emfstore.internal.client.ui.controller.UICheckoutController;
import org.eclipse.emf.emfstore.internal.server.model.ProjectInfo;

/**
 * Handler for checking out a project.
 *
 * @author emueller
 *
 */
public class CheckoutHandler extends AbstractEMFStoreHandler {

	@Override
	public void handle() {

		final ProjectInfo projectInfo = requireSelection(ProjectInfo.class);

		if (projectInfo == null || projectInfo.eContainer() == null) {
			return;
		}

		// FIXME: eContainer call
		final ServerInfo serverInfo = (ServerInfo) projectInfo.eContainer();

		new UICheckoutController(getShell(),
			new ESRemoteProjectImpl(serverInfo, projectInfo)).execute();
	}

}
