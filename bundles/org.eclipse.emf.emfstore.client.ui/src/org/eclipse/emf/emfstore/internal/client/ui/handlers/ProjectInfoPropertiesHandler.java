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

import org.eclipse.emf.emfstore.internal.client.ui.controller.UIShowProjectPropertiesController;
import org.eclipse.emf.emfstore.internal.server.model.ProjectInfo;

/**
 * Brings up the properties dialog for a selected project.<br/>
 * It is assumed that the user previously has selected a {@link ProjectInfo} instance.
 *
 * @author ovonwesen
 * @author emueller
 */
public class ProjectInfoPropertiesHandler extends AbstractEMFStoreHandler {

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.handlers.AbstractEMFStoreHandler#handle()
	 */
	@Override
	public void handle() {
		new UIShowProjectPropertiesController(getShell(), requireSelection(ProjectInfo.class)).execute();
	}

}
