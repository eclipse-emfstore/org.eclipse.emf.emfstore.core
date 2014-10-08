/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * ovonwesen
 * emueller
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.handlers;

import org.eclipse.emf.emfstore.internal.client.model.ServerInfo;
import org.eclipse.emf.emfstore.internal.client.ui.controller.UIManageOrgUnitsController;
import org.eclipse.emf.emfstore.internal.client.ui.exceptions.RequiredSelectionException;

/**
 * Handler for bringing up the dialog that enables it to managed organizational
 * units. It is assumed that the user previously has selected a {@link ServerInfo} instance.
 * 
 * @author ovonwesen
 * @author emueller
 */
public class ManageOrgUnitsHandler extends AbstractEMFStoreHandler {

	@Override
	public void handle() {
		ServerInfo serverInfo = requireSelection(ServerInfo.class);

		if (serverInfo.getLastUsersession() == null) {
			throw new RequiredSelectionException(
				"Usersession not available in selected ServerInfo.");
		}

		new UIManageOrgUnitsController(getShell(), serverInfo
			.getLastUsersession().toAPI()).execute();
	}
}
