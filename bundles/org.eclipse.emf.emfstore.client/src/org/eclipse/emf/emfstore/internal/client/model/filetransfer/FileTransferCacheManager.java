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

import org.eclipse.emf.emfstore.internal.client.model.Configuration;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.provider.XMIClientURIConverter;
import org.eclipse.emf.emfstore.internal.common.model.util.FileUtil;
import org.eclipse.emf.emfstore.internal.server.exceptions.FileTransferException;
import org.eclipse.emf.emfstore.internal.server.model.FileIdentifier;

/**
 * This class manages the locally cached files for file transfers. A cache
 * manager instance is contained in every FileTransferManager. No other cache
 * managers should be created.
 *
 * @author jfinis
 */
public class FileTransferCacheManager {

	/**
	 * Temporary folder for file uploads to server.
	 */
	public static final String TEMP_FOLDER = "tmp"; //$NON-NLS-1$

	/**
	 * project folder prefix.
	 */
	public static final String PROJECT_FOLDER_PREFIX = "project-"; //$NON-NLS-1$

	/**
	 * Attachment folder for uploads and downloads.
	 */
	public static final String ATTACHMENT_FOLDER = "attachment"; //$NON-NLS-1$

	/**
	 * The delimiter that separates file attachment id, file version and file
	 * name in an uploaded file.
	 */
	public static final String FILE_NAME_DELIMITER = "_"; //$NON-NLS-1$

	/**
	 * The cache folder, constructed from the identifier of the project space.
	 */
	private final File cacheFolder;

	/**
	 * The temp folder is a folder where unfinished file downloads are stored.
	 */
	private final File tempCacheFolder;

	/**
	 * Default constructor for a specific project space.
	 *
	 * @param projectSpaceImpl
	 *            the project space to which this cache belongs.
	 */
	public FileTransferCacheManager(ProjectSpace projectSpaceImpl) {
		cacheFolder = new File(getCacheFolder(projectSpaceImpl));
		tempCacheFolder = new File(cacheFolder, "temp"); //$NON-NLS-1$
		mkdirs();
	}

	/**
	 * Default constructor for uploads without an available project space.
	 */
	public FileTransferCacheManager() {
		cacheFolder = new File(new File(System.getProperty("java.io.tmpdir")), //$NON-NLS-1$
			"ESFileTransferCacheManager" + System.currentTimeMillis()); //$NON-NLS-1$
		tempCacheFolder = new File(cacheFolder, "temp"); //$NON-NLS-1$
		mkdirs();
		cacheFolder.deleteOnExit();
	}

	/**
	 * Returns the default file cache folder of a given projectspace.
	 *
	 * @param projectSpace the projectSpace
	 * @return the name of the cache folder
	 */
	public static String getCacheFolder(ProjectSpace projectSpace) {
		return Configuration.getFileInfo().getWorkspaceDirectory()
			+ XMIClientURIConverter.PROJECT_SAPCE_DIRECTORY_PREFIX
			+ projectSpace.getIdentifier()
			+ File.separatorChar
			+ "files" + File.separatorChar; //$NON-NLS-1$
	}

	/**
	 * Returns true iff a file with this identifier is present in the cache.
	 * After this method has returned true, it is guaranteed that the
	 * getCachedFile method finds the file and does not throw an exception.
	 *
	 * @param identifier
	 *            the identifier of the file
	 * @return if the file is present in the cache
	 */
	public boolean hasCachedFile(FileIdentifier identifier) {
		final File f = getFileFromId(cacheFolder, identifier);
		return f.exists();
	}

	/**
	 * Returns a cached file with a given identifier. If the file does not
	 * exist, a FileTransferException is thrown.
	 *
	 * @param identifier
	 *            the identifier of the file
	 * @return the file
	 * @throws FileTransferException
	 *             if the file is not present in the cache
	 */
	public File getCachedFile(FileIdentifier identifier) throws FileTransferException {
		final File f = getFileFromId(cacheFolder, identifier);
		if (!f.exists()) {
			throw new FileTransferException(
				MessageFormat.format(
					Messages.FileTransferCacheManager_FileNotInCache, identifier));
		}
		return f;
	}

	/**
	 * Adds a file to the cache using a given id. If a file with the given id is
	 * already in the cache, it is overwritten.
	 *
	 * @param input
	 *            the file to be cached
	 * @param id
	 *            the id to be used for the file.
	 * @return the file in the cache folder
	 * @throws IOException
	 *             any IO Exception that can occur during copying a file
	 */
	public File cacheFile(File input, FileIdentifier id) throws IOException {
		mkdirs();
		final File destination = new File(cacheFolder, id.getIdentifier());
		FileUtil.copyFile(input, destination);
		return destination;
	}

	/**
	 * Creates a file in the temporary cache folder for a specified file id and
	 * returns it. If the file already exists, it is deleted and newly created.
	 *
	 * @param id
	 *            the file id for which to create a temporary file
	 * @return the temporary file
	 * @throws FileTransferException
	 *             if an IO exception occurred during the creation of a new file
	 */
	public File createTempFile(FileIdentifier id) throws FileTransferException {
		mkdirs();
		final File cacheFile = getFileFromId(tempCacheFolder, id);
		cacheFile.getParentFile().mkdirs();
		if (cacheFile.exists()) {
			cacheFile.delete();
		}
		try {
			cacheFile.createNewFile();
		} catch (final IOException e) {
			throw new FileTransferException(Messages.FileTransferCacheManager_CreateTempFileFailed);
		}
		return cacheFile;
	}

	/**
	 * This method moves a file from the temporary folder into the cache. It should
	 * be called after a temporary file was written successfully. A file
	 * transfer exception is thrown in the following cases: - The file does not
	 * exist in the temporary folder - The file already exists in the cache folder -
	 * The file cannot be moved to the cache folder (the rename operation fails)
	 *
	 * @param id
	 *            the id of the file which is to be moved
	 * @return the new location of the file after moving it
	 * @throws FileTransferException
	 *             thrown if the temporary file does not exist, the final file
	 *             already exists, or if the rename operation which moves the
	 *             file fails
	 */
	public File moveTempFileToCache(FileIdentifier id) throws FileTransferException {
		return moveTempFileToCache(id, false);
	}

	/**
	 * This method moves a file from the temporary folder into the cache. It should
	 * be called after a temporary file was written successfully. A file
	 * transfer exception is thrown in the following cases: - The file does not
	 * exist in the temporary folder - The file already exists in the cache folder -
	 * The file cannot be moved to the cache folder (the rename operation fails)
	 *
	 * @param id
	 *            the id of the file which is to be moved
	 * @param overwrite
	 *            whether to overwrite an existing file, if one with the given id is found
	 * @return the new location of the file after moving it
	 * @throws FileTransferException
	 *             thrown if the temporary file does not exist, the final file
	 *             already exists, or if the rename operation which moves the
	 *             file fails
	 */
	public File moveTempFileToCache(FileIdentifier id, boolean overwrite) throws FileTransferException {
		mkdirs();
		final File cacheFile = getFileFromId(cacheFolder, id);
		final File tmpFile = getFileFromId(tempCacheFolder, id);
		if (!tmpFile.exists()) {
			throw new FileTransferException(
				Messages.FileTransferCacheManager_MoveToCacheFailed_FileMissing
					+ id.getIdentifier());
		}
		if (cacheFile.exists() && overwrite) {
			cacheFile.delete();
		}
		if (cacheFile.exists()) {
			throw new FileTransferException(
				Messages.FileTransferCacheManager_MoveToCacheFailed_Exists
					+ id.getIdentifier());
		}
		cacheFile.getParentFile().mkdirs();
		if (!tmpFile.renameTo(cacheFile)) {
			throw new FileTransferException(Messages.FileTransferCacheManager_MoveToCacheFailed_MoveFailed);
		}
		return cacheFile;
	}

	/**
	 * Builds a file from a cache folder and a file identifier.
	 *
	 * @param folder
	 *            the base folder in which the file is
	 * @param id
	 *            the file identifier of that file
	 * @return the assembled file
	 */
	private File getFileFromId(File folder, FileIdentifier id) {
		return new File(folder, id.getIdentifier());
	}

	/**
	 * Creates all necessary directories (cache and temp).
	 */
	private void mkdirs() {
		cacheFolder.mkdirs();
		tempCacheFolder.mkdirs();
	}

	/**
	 * Removes a file from the cache. Does nothing if no such file is cached.
	 * Returns true if the file was successfully deleted from cache, otherwise
	 * false.
	 *
	 * @param fileIdentifier
	 *            the identifier of the file to be removed
	 * @return true iff the file was deleted successfully
	 */
	public boolean removeCachedFile(FileIdentifier fileIdentifier) {
		final File toRemove = getFileFromId(cacheFolder, fileIdentifier);
		if (toRemove.exists()) {
			return toRemove.delete();
		}
		return false;
	}
}
