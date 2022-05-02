/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Finis - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.filetransfer;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.emfstore.internal.client.model.Usersession;
import org.eclipse.emf.emfstore.internal.client.model.filetransfer.util.ProjectSpaceUploadQueue;
import org.eclipse.emf.emfstore.internal.client.model.filetransfer.util.SimpleUploadQueue;
import org.eclipse.emf.emfstore.internal.client.model.filetransfer.util.UploadQueue;
import org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceBase;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreClientUtil;
import org.eclipse.emf.emfstore.internal.client.model.util.WorkspaceUtil;
import org.eclipse.emf.emfstore.internal.server.exceptions.FileTransferException;
import org.eclipse.emf.emfstore.internal.server.filetransfer.FileTransferInformation;
import org.eclipse.emf.emfstore.internal.server.model.FileIdentifier;
import org.eclipse.emf.emfstore.internal.server.model.ModelFactory;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;

/**
 * The main managing class on the client side for file transfers. Each project
 * space has an associated FileTransferManager. All file-related request from
 * the project space are delegated to that manager. The manager provides methods
 * to add files, get files and retrieve additional information about files.
 *
 * @author jfinis
 */
public class FileTransferManager {

	private static final String LOGGING_PREFIX = "UPLOAD"; //$NON-NLS-1$

	/**
	 * The associated cache manager.
	 */
	private final FileTransferCacheManager cacheManager;

	/**
	 * The associated project space.
	 */
	private final ProjectSpaceBase projectSpace;

	private final UploadQueue uploadQueue;

	private ProjectId projectId;

	private Usersession usersession;

	/**
	 * Constructor that creates a file transfer manager for a specific project
	 * space. Only to be called in the init of a project space!
	 *
	 * @param projectSpaceImpl
	 *            the project space to which this transfer manager belongs
	 */
	public FileTransferManager(ProjectSpaceBase projectSpaceImpl) {
		cacheManager = new FileTransferCacheManager(projectSpaceImpl);
		uploadQueue = new ProjectSpaceUploadQueue(projectSpaceImpl);
		projectSpace = projectSpaceImpl;
	}

	/**
	 * Constructor that creates a file transfer manager when no project space is available.
	 *
	 * @param projectId the project id
	 * @param usersession the usersession
	 */
	public FileTransferManager(ProjectId projectId, Usersession usersession) {
		this.projectId = projectId;
		this.usersession = usersession;
		cacheManager = new FileTransferCacheManager();
		uploadQueue = new SimpleUploadQueue();
		projectSpace = null;
	}

	/**
	 * Adds a file to be transferred (uploaded).
	 *
	 * @param file
	 *            the file to be transferred
	 *
	 * @return the {@link FileIdentifier} that associates the file with its ID
	 *
	 * @throws FileTransferException in case the {@code file} is either {@code null}, a directory
	 *             or does not exist
	 */
	public FileIdentifier addFile(File file) throws FileTransferException {
		return addFile(file, null);
	}

	/**
	 * Adds a file to be transferred (uploaded).
	 *
	 * @param file
	 *            the file to be transferred
	 * @param id
	 *            the ID that will be associated with the file being uploaded
	 *
	 * @return the {@link FileIdentifier} that associates the file with its ID
	 *
	 * @throws FileTransferException in case the {@code file} is either {@code null}, a directory
	 *             or does not exist
	 */
	public FileIdentifier addFile(File file, String id) throws FileTransferException {
		if (file == null) {
			throw new FileTransferException(Messages.FileTransferManager_FileIsNull);
		}
		if (file.isDirectory()) {
			throw new FileTransferException(Messages.FileTransferManager_UploadIsDirectory
				+ file.getAbsolutePath());
		}
		if (!file.exists()) {
			throw new FileTransferException(Messages.FileTransferManager_FileDoesNotExist + file.getAbsolutePath());
		}

		// Create the file identifier
		final FileIdentifier identifier = ModelFactory.eINSTANCE.createFileIdentifier();
		if (id != null) {
			identifier.setIdentifier(id);
		}

		// Move file to cache
		try {
			cacheManager.cacheFile(file, identifier);
		} catch (final IOException e) {
			throw new FileTransferException(Messages.FileTransferManager_ExceptionDuringCaching
				+ e.getMessage(), e);
		}

		// Add the file to the queue for files that should be
		// transmitted when a commit is done
		addToCommitQueue(identifier);

		return identifier;
	}

	/**
	 * Adds a file to the queue of pending uploads.
	 *
	 * @param identifier
	 */
	private void addToCommitQueue(final FileIdentifier identifier) {
		for (final FileIdentifier f : uploadQueue.getPendingUploads()) {
			if (f.getIdentifier().equals(identifier.getIdentifier())) {
				return;
			}

		}
		uploadQueue.add(identifier);
	}

	/**
	 * Uploads all files in the commit queue. Is called upon committing the
	 * project space.
	 *
	 * @param progress
	 *            progress monitor
	 */
	public void uploadQueuedFiles(IProgressMonitor progress) {
		try {
			final List<FileIdentifier> uploads = uploadQueue.getPendingUploads();
			while (!uploads.isEmpty()) {
				final FileIdentifier fi = uploads.get(0);

				// Is the file present in cache?
				// (it should be, unless there is a severe bug or the user has
				// manually deleted it)
				if (!cacheManager.hasCachedFile(fi)) {
					WorkspaceUtil.logException(
						MessageFormat.format(
							Messages.FileTransferManager_FileNoInCache_1
								+ Messages.FileTransferManager_FileNoInCache_2
								+ Messages.FileTransferManager_FileNoInCache_3,
							fi.getIdentifier()),
						null);
					// Remove from commit queue
					uploadQueue.remove(fi);
					continue;

				}
				EMFStoreClientUtil.logProjectDetails(LOGGING_PREFIX,
					MessageFormat.format("Uploading file with identifier {0}", fi.getIdentifier())); //$NON-NLS-1$
				final FileUploadJob job = new FileUploadJob(this, fi, true);
				final IStatus result = job.run(progress);

				if (job.getException() != null) {
					WorkspaceUtil.logException(Messages.FileTransferManager_ExceptionDuringUpload,
						job.getException());
					return;
				}

				if (result.getCode() == IStatus.CANCEL) {
					return;
				}
			}
		} catch (final FileTransferException e) {
			WorkspaceUtil.logException(Messages.FileTransferManager_UploadFailed, e);
		}
	}

	/**
	 * Delete the given file.
	 *
	 * @param progress an {@link IProgressMonitor} to monitor progress
	 * @param fileIdentifier the ID of the file to be deleted
	 */
	public void deleteFile(IProgressMonitor progress, FileIdentifier fileIdentifier) {
		final FileTransferInformation fileTransferInformation = new FileTransferInformation(fileIdentifier, 0);
		final FileDeleteJob job = new FileDeleteJob(this, fileTransferInformation,
			MessageFormat.format(Messages.FileTransferManager_DeleteFile, fileIdentifier.getIdentifier()));
		final IStatus result = job.run(progress);
		cacheManager.removeCachedFile(fileIdentifier);

		if (job.getException() != null) {
			WorkspaceUtil.logException(
				MessageFormat.format(
					Messages.FileTransferManager_DeleteFileError,
					fileIdentifier.getIdentifier()),
				job.getException());
			return;
		}

		if (result.getCode() == IStatus.CANCEL) {
			return;
		}
	}

	/**
	 * Returns the download status of the file that is associated with the given {@link FileIdentifier}.
	 *
	 * @param fileIdentifier
	 *            the file identifier whose download status should be retrieved
	 * @param isTriggeredByUI
	 *            whether the download of the file has been triggered by the UI
	 * @param forceDownload
	 *            whether to re-fetch the file even, if a file with the same identifier is already present;
	 *            set this to <code>true</code> in case you have files, which will be updated but keep the same
	 *            identifier
	 * @return the download status of the file
	 *
	 * @throws FileTransferException in case the given file identifier is {@code null}
	 */
	public FileDownloadStatus getFile(FileIdentifier fileIdentifier,
		boolean isTriggeredByUI, boolean forceDownload) throws FileTransferException {

		if (fileIdentifier == null) {
			throw new FileTransferException(Messages.FileTransferManager_FileIdentifierIsNull);
		}

		if (forceDownload || !cacheManager.hasCachedFile(fileIdentifier)) {
			return startDownload(fileIdentifier, isTriggeredByUI);
		}

		// If the file is cached locally, get it
		// if (cacheManager.hasCachedFile(fileIdentifier)) {
		return FileDownloadStatus.Factory.createAlreadyFinished(
			getProjectSpace()/* may be null */,
			fileIdentifier,
			cacheManager.getCachedFile(fileIdentifier));
		// }

		// Otherwise, start a download
		// return startDownload(fileIdentifier, isTriggeredByUI);
	}

	/**
	 * Returns the download status of the file that is associated with the given {@link FileIdentifier}.
	 *
	 * @param fileIdentifier
	 *            the file identifier whose download status should be retrieved
	 * @param isTriggeredByUI
	 *            whether the download of the file has been triggered by the UI
	 * @return the download status of the file
	 *
	 * @throws FileTransferException in case the given file identifier is {@code null}
	 */
	public FileDownloadStatus getFile(FileIdentifier fileIdentifier, boolean isTriggeredByUI)
		throws FileTransferException {
		return getFile(fileIdentifier, isTriggeredByUI, false);
	}

	/**
	 * Starts a download of a specific file. Returns a status object that can be
	 * queried to check how far the download is.
	 *
	 * @param fileIdentifier
	 *            the file to be downloaded
	 * @param monitor
	 *            a progress monitor for the download
	 * @return the status
	 */
	private FileDownloadStatus startDownload(FileIdentifier fileIdentifier, boolean isTriggeredByUI) {
		final FileDownloadStatus fds = FileDownloadStatus.Factory.createNew(
			getProjectSpace()/* may be null */,
			fileIdentifier);
		// TODO Check if true is correct here
		final FileDownloadJob job = new FileDownloadJob(fds, this, fileIdentifier, isTriggeredByUI);
		job.schedule();
		return fds;
	}

	/**
	 * Returns the cache manager.
	 *
	 * @return the associated cache manager
	 */
	FileTransferCacheManager getCache() {
		return cacheManager;
	}

	/**
	 * Gets the index of a waiting file upload in the upload queue or -1 if this
	 * upload is not in the queue.
	 *
	 * @param fileId
	 *            the index to be looked up in the queue
	 * @return the index in the queue or -1
	 */
	private int getWaitingUploadIndex(FileIdentifier fileId) {
		if (fileId == null) {
			return -1;
		}

		int i = 0;
		/*
		 * We need to loop over the pending uploads here. This is because we
		 * cannot use .remove(fileId) because remove uses equals to check for
		 * the element. Equals is not well-defined for EObjects, so we cannot
		 * use it here.
		 */
		for (final FileIdentifier upload : uploadQueue.getPendingUploads()) {
			// This is our equals: Compare the strings!
			if (upload.getIdentifier().equals(fileId.getIdentifier())) {
				return i;
			}
			i++;
		}
		return -1;
	}

	/**
	 * Removes a waiting upload from the queue. Throws a file transfer exception
	 * if the file is not in the list.
	 *
	 * @param fileId
	 *            the file to remove from the queue
	 * @throws FileTransferException
	 *             if the file is not in the queue
	 */
	void removeWaitingUpload(FileIdentifier fileId) throws FileTransferException {
		final int index = getWaitingUploadIndex(fileId);
		if (index != -1) {
			uploadQueue.remove(index);

		} else {
			// Not found in list? exception!
			throw new FileTransferException(
				MessageFormat.format(
					Messages.FileTransferManager_NoUploadPendingWithThatId, fileId));
		}
	}

	/**
	 * Return if a specific file is in the pending upload queue.
	 *
	 * @param fileIdentifier
	 *            the file to be looked up
	 * @return true, if the file is in the queue
	 */
	public boolean hasWaitingUpload(FileIdentifier fileIdentifier) {
		return getWaitingUploadIndex(fileIdentifier) != -1;
	}

	/**
	 * Cancels a pending upload. That means that the upload is removed from the
	 * queue and deleted from cache. If the file is not in the queue, nothing is
	 * done. If it is in the queue but not in the cache, then it is only removed
	 * from the queue.
	 *
	 * @param fileIdentifier
	 *            the file to be canceled
	 */
	public void cancelPendingUpload(FileIdentifier fileIdentifier) {
		// Remove from the waiting queue
		try {
			removeWaitingUpload(fileIdentifier);
		} catch (final FileTransferException e) {
			return;
		}

		// Remove from cache
		cacheManager.removeCachedFile(fileIdentifier);
	}

	/**
	 * returns a file information object for a specific file identifier.
	 *
	 * @param fileIdentifier
	 *            the identifier
	 * @return the file information for that identifier
	 */
	public FileInformation getFileInfo(FileIdentifier fileIdentifier) {
		return new FileInformation(fileIdentifier, this);
	}

	/**
	 * Returns the associated project space.
	 *
	 * @return the project to which this file transfer manager belongs to
	 */
	ProjectSpaceBase getProjectSpace() {
		return projectSpace;
	}

	/**
	 * @return the {@link ProjectId}
	 */
	ProjectId getProjectId() {
		if (getProjectSpace() != null) {
			return getProjectSpace().getProjectId();
		}
		return projectId;
	}

	/**
	 * @return the {@link Usersession}
	 */
	Usersession getUsersession() {
		if (getProjectSpace() != null) {
			return getProjectSpace().getUsersession();
		}
		return usersession;
	}

}
