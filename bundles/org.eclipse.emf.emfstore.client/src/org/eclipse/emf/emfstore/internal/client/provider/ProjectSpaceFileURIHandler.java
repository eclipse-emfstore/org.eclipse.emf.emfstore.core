/*******************************************************************************
 * Copyright (c) 2013 EclipseSource Muenchen GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Johannes Faltermeier
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.provider;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.FileURIHandlerImpl;
import org.eclipse.emf.emfstore.internal.client.model.Configuration;

/**
 * Handler for projectspace file URIs. Adds functionality for successfully deleting temp folders.
 * 
 * @author jfaltermeier
 * 
 */
public class ProjectSpaceFileURIHandler extends FileURIHandlerImpl {

	@Override
	public boolean canHandle(URI uri) {
		String extension = "." + uri.fileExtension();
		if (extension.equals(Configuration.getFileInfo().getLocalChangePackageFileExtension()) ||
			extension.equals(Configuration.getFileInfo().getProjectFragmentFileExtension()) ||
			extension.equals(Configuration.getFileInfo().getProjectSpaceFileExtension())) {
			return true;
		}
		return false;
	}

	@Override
	public void delete(URI uri, Map<?, ?> options) throws IOException
	{
		// TODO check options
		File file = new File(uri.toFileString());
		File parent = file.getParentFile();
		file.delete();

		if (parent != null && parent.exists() && parent.listFiles().length == 1 && parent.listFiles()[0].isDirectory()) {
			// if there is only one directory left, it's the temp folder.
			FileUtils.deleteDirectory(parent);
		}
	}

}
