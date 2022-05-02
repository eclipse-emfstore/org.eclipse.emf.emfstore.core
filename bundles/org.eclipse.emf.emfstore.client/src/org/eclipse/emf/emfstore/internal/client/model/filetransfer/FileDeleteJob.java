/*******************************************************************************
 * Copyright (c) 2011-2017 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * edgar - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.filetransfer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.emfstore.internal.server.exceptions.FileTransferException;
import org.eclipse.emf.emfstore.internal.server.filetransfer.FileTransferInformation;
import org.eclipse.emf.emfstore.server.exceptions.ESException;

public class FileDeleteJob extends FileTransferJob {

	/**
	 * @param transferManager
	 * @param fileInfo
	 * @param name
	 */
	protected FileDeleteJob(FileTransferManager transferManager, FileTransferInformation fileInfo, String name) {
		super(transferManager, fileInfo, name);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			getConnectionAttributes();
		} catch (final FileTransferException ex) {
			setException(ex);
			monitor.setCanceled(true);
			monitor.done();
			return Status.CANCEL_STATUS;
		}
		try {
			getConnectionManager().deleteFile(
				getSessionId(),
				getProjectId(),
				getFileInformation().getFileIdentifier());
		} catch (final ESException ex) {
			setException(ex);
			monitor.setCanceled(true);
			monitor.done();
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}

}
