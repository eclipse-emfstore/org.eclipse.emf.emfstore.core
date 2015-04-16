/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * wesendon
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.handlers;

import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.ui.controller.UICreateBranchController;

/**
 * Triggers Branch creation.
 *
 * @author wesendon
 */
public class CreateBranchHandler extends AbstractEMFStoreHandler {

	@Override
	public void handle() {
		new UICreateBranchController(getShell(), requireSelection(
			ProjectSpace.class).toAPI()).execute();
	}

}
