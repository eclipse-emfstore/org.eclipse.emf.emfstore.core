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

import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.internal.client.ui.controller.UICommitProjectController;

/**
 * <p>
 * Handler for commiting a project.
 * </p>
 * <p>
 * It is assumed that the user previously has selected a {@link ESLocalProject} instance.<br/>
 * Alternatively, the project to be committed may also be passed via a constructor.
 * </p>
 * 
 * @author emueller
 * 
 */
public class CommitProjectHandler extends AbstractEMFStoreHandler {

	private ESLocalProject localProject;

	/**
	 * Default constructor.
	 */
	public CommitProjectHandler() {
		this(null);
	}

	/**
	 * Constructor.
	 * 
	 * @param localProject
	 *            the local project to be committed
	 */
	public CommitProjectHandler(ESLocalProject localProject) {
		this.localProject = localProject;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.ui.handlers.AbstractEMFStoreHandler#handle()
	 */
	@Override
	public void handle() {

		if (localProject == null) {
			localProject = requireSelection(ESLocalProject.class);
		}

		new UICommitProjectController(getShell(), localProject).execute();
	}

}
