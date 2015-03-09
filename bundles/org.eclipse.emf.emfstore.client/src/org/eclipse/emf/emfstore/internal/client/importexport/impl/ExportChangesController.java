/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * emueller
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.importexport.impl;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.util.ResourceHelper;
import org.eclipse.emf.emfstore.internal.common.model.util.FileUtil;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;

/**
 * Exports pending changes on a given {@link ProjectSpace}.
 *
 * @author emueller
 */
public class ExportChangesController extends ProjectSpaceBasedExportController {

	/**
	 * Constructor.
	 *
	 * @param projectSpace the {@link ProjectSpace} whose local changes should be exported
	 */
	public ExportChangesController(ProjectSpace projectSpace) {
		super(projectSpace);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.importexport.IExportImportController#getFilteredNames()
	 */
	public String[] getFilteredNames() {
		return new String[] { "EMFStore change package (" + ExportImportDataUnits.Change.getExtension() + ")",
		"All Files (*.*)" };
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.importexport.IExportImportController#getFilteredExtensions()
	 */
	public String[] getFilteredExtensions() {
		return new String[] { "*" + ExportImportDataUnits.Change.getExtension(), "*.*" };
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.importexport.IExportImportController#getLabel()
	 */
	public String getLabel() {
		return "changes";
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.importexport.IExportImportController#getFilename()
	 */
	public String getFilename() {
		return "LocalChanges_" + getProjectSpace().getProjectName() + "@"
			+ getProjectSpace().getBaseVersion().getIdentifier();
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.importexport.IExportImportController#getParentFolderPropertyKey()
	 */
	public String getParentFolderPropertyKey() {
		return null;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.importexport.IExportImportController#execute(java.io.File,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void execute(File file, IProgressMonitor progressMonitor) throws IOException {

		if (!FileUtil.getExtension(file).equals(ExportImportDataUnits.Change.getExtension())) {
			file = new File(file.getAbsoluteFile() + ExportImportDataUnits.Change.getExtension());
		}

		final AbstractChangePackage changePackage = getProjectSpace().getLocalChangePackage(false);
		ResourceHelper.putElementIntoNewResourceWithProject(
			file.getAbsolutePath(),
			changePackage,
			getProjectSpace().getProject());
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.importexport.IExportImportController#isExport()
	 */
	public boolean isExport() {
		return true;
	}
}
