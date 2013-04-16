/*******************************************************************************
 * Copyright (c) 2012 EclipseSource Muenchen GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.handlers;

import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.ui.controller.UIRevertOperationController;

/**
 * Handler for reverting changes.
 * 
 * 
 * @author emueller
 * @author eneufeld
 * 
 */
public class RevertHandler extends AbstractEMFStoreHandler {

	@Override
	public void handle() {
		new UIRevertOperationController(getShell(), requireSelection(
			ProjectSpace.class).toAPI()).execute();
	}

}