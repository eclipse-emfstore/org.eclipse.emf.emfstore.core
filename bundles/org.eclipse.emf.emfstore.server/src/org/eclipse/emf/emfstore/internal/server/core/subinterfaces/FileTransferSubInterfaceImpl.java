/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * pfeifferc
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.core.subinterfaces;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.emf.emfstore.internal.common.model.util.FileUtil;
import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.eclipse.emf.emfstore.internal.server.core.AbstractEmfstoreInterface;
import org.eclipse.emf.emfstore.internal.server.core.AbstractSubEmfstoreInterface;
import org.eclipse.emf.emfstore.internal.server.core.MonitorProvider;
import org.eclipse.emf.emfstore.internal.server.exceptions.FatalESException;
import org.eclipse.emf.emfstore.internal.server.exceptions.FileTransferException;
import org.eclipse.emf.emfstore.internal.server.exceptions.InvalidInputException;
import org.eclipse.emf.emfstore.internal.server.filetransfer.FileChunk;
import org.eclipse.emf.emfstore.internal.server.filetransfer.FilePartitionerUtil;
import org.eclipse.emf.emfstore.internal.server.filetransfer.FileTransferInformation;
import org.eclipse.emf.emfstore.internal.server.model.FileIdentifier;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.storage.XMIServerURIConverter;
import org.eclipse.emf.emfstore.server.auth.ESMethod;
import org.eclipse.emf.emfstore.server.auth.ESMethod.MethodId;

/**
 * The file transfer subinterface.
 *
 * @author pfeifferc
 */
public class FileTransferSubInterfaceImpl extends AbstractSubEmfstoreInterface {

	private static final String FILELOAD = "filetransfer"; //$NON-NLS-1$

	/**
	 * tmp folder for file uploads to server.
	 */
	public static final String TEMP_FOLDER = "tmp"; //$NON-NLS-1$

	/**
	 * Attachment folder for uploads and downloads.
	 */
	public static final String ATTACHMENT_FOLDER = "attachment"; //$NON-NLS-1$

	/**
	 * The delimiter that separates file attachment id, file version and file name in an uploaded file.
	 */
	public static final String FILE_NAME_DELIMITER = "_"; //$NON-NLS-1$

	/**
	 * @param parentInterface the parent interface
	 * @throws FatalESException if any fatal error occurs
	 */
	public FileTransferSubInterfaceImpl(AbstractEmfstoreInterface parentInterface) throws FatalESException {
		super(parentInterface);
	}

	/**
	 * Reads a chunk from the file linked to the fileInformation.
	 *
	 * @param projectId project attachment folder
	 * @param fileInformation file information object
	 * @return FileChunk
	 * @throws FileTransferException if any error occurs reading the file
	 * @throws InvalidInputException thrown if one of the parameters is null
	 */
	@ESMethod(MethodId.DOWNLOADFILECHUNK)
	public FileChunk downloadFileChunk(ProjectId projectId, FileTransferInformation fileInformation)
		throws FileTransferException, InvalidInputException {
		sanityCheckObjects(projectId, fileInformation);

		// check if folders exist, otherwise create
		createDirectories(projectId);
		// try to localize file that is to be downloaded
		File file;
		try {
			file = findFile(fileInformation, projectId);
		} catch (final FileNotFoundException e) {
			// throw new FileNotOnServerException(projectId, fileInformation.getFileIdentifier());
			return null;
		}

		return FilePartitionerUtil.readChunk(file, fileInformation);
	}

	/**
	 * Delete the given file
	 *
	 * @param projectId the ID of the project associated with the file to be deleted
	 * @param fileIdentifier the ID of the file to be deleted
	 */
	@ESMethod(MethodId.DELETEFILE)
	public void deleteFile(ProjectId projectId, FileIdentifier fileIdentifier) {
		synchronized (MonitorProvider.getInstance().getMonitor(FILELOAD)) {
			final File cachedFile = getCachedFile(fileIdentifier, projectId);
			cachedFile.delete();
		}
	}

	/**
	 * Writes a chunk to the file linked to the fileInformation in the fileChunk. If the data in the file chunk is null,
	 * this is treated as a request for a file version.
	 *
	 * @param fileChunk contains data and information about the file attachment, file version and chunk number
	 * @param projectId project id
	 * @return fileInformation containing the (new) file version
	 * @throws FileTransferException if any error occurs writing to the file
	 * @throws InvalidInputException thrown if one of the parameters is null
	 */
	@ESMethod(MethodId.UPLOADFILECHUNK)
	public FileTransferInformation uploadFileChunk(ProjectId projectId, FileChunk fileChunk)
		throws FileTransferException, InvalidInputException {
		sanityCheckObjects(projectId, fileChunk);
		synchronized (MonitorProvider.getInstance().getMonitor(FILELOAD)) {
			// check if folders exist, otherwise create
			createDirectories(projectId);
			final FileTransferInformation fileInfo = fileChunk.getFileInformation();

			// retrieve location for the temp file
			File tmpFile;
			try {
				if (fileChunk.getChunkNumber() == 0) {
					tmpFile = getTempFile(fileInfo, projectId);
				} else {
					tmpFile = findFileInTemp(fileInfo, projectId);
				}
			} catch (final FileNotFoundException e) {
				throw new FileTransferException(
					Messages.FileTransferSubInterfaceImpl_File_Inaccessible, e);
			}
			tmpFile.getParentFile().mkdirs();
			// file reslicer for reslicing temp file
			FilePartitionerUtil.writeChunk(tmpFile, fileChunk);
			// move file from temp folder to attachment folder if last file chunk is received
			if (fileChunk.isLast()) {
				try {
					// retrieve final location for file
					final File attachmentFile = getCachedFile(fileInfo.getFileIdentifier(), projectId);

					FileUtil.copyFile(tmpFile, attachmentFile);
					tmpFile.delete();
				} catch (final IOException e) {
					throw new FileTransferException(Messages.FileTransferSubInterfaceImpl_Move_Failed, e);
				}
			}
			return fileInfo;
		}
	}

	/**
	 * Creates the file attachment and temporary file attachment folders.
	 */
	private void createDirectories(ProjectId projectId) {
		final File createFolders = new File(getProjectAttachmentTempFolder(projectId));
		if (!createFolders.exists()) {
			createFolders.mkdirs();
		}
	}

	private File findFileInTemp(FileTransferInformation fileInfo, ProjectId projectId) throws FileNotFoundException {
		final File file = getTempFile(fileInfo, projectId);
		if (file.exists()) {
			return file;
		}
		throw new FileNotFoundException(MessageFormat.format(
			Messages.FileTransferSubInterfaceImpl_Locate_Tmp_Failed,
			fileInfo.getFileIdentifier()));
	}

	private File findFile(FileTransferInformation fileInfo, ProjectId projectId) throws FileNotFoundException {
		final File file = getCachedFile(fileInfo.getFileIdentifier(), projectId);
		if (file.exists()) {
			return file;
		}
		throw new FileNotFoundException(MessageFormat.format(
			Messages.FileTransferSubInterfaceImpl_Locate_Cache_Failed,
			fileInfo.getFileIdentifier()));
	}

	private File getTempFile(FileTransferInformation fileInfo, ProjectId projectId) {
		return new File(getProjectAttachmentTempFolder(projectId) + File.separator
			+ constructFileName(fileInfo.getFileIdentifier()));
	}

	private File getCachedFile(FileIdentifier fileIdentifier, ProjectId projectId) {
		return new File(
			getProjectAttachmentFolder(projectId) + File.separator + constructFileName(fileIdentifier));
	}

	private String constructFileName(FileIdentifier fileIdentifier) {
		return fileIdentifier.getIdentifier();
	}

	private String getProjectAttachmentFolder(ProjectId projectId) {
		return ServerConfiguration.getServerHome() + XMIServerURIConverter.FILE_PREFIX_PROJECTFOLDER
			+ projectId.getId()
			+ File.separator + ATTACHMENT_FOLDER;
	}

	private String getProjectAttachmentTempFolder(ProjectId projectId) {
		return getProjectAttachmentFolder(projectId) + File.separator + TEMP_FOLDER;
	}
}
