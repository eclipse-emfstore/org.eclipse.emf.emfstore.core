/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.filetransfer;

import org.eclipse.osgi.util.NLS;

/**
 * File transfer related messages.
 *
 * @author emueller
 *
 * @generated
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.emf.emfstore.internal.client.model.filetransfer.messages"; //$NON-NLS-1$
	public static String FileDownloadJob_FileDownload;
	public static String FileDownloadStatus_BlockedGetInitFailed;
	public static String FileDownloadStatus_CannotStartJob;
	public static String FileDownloadStatus_FileNotOnServer;
	public static String FileDownloadStatus_TransferFailed;
	public static String FileDownloadStatus_TransferNotFinishedYet;
	public static String FileTransferCacheManager_CreateTempFileFailed;
	public static String FileTransferCacheManager_FileNotInCache;
	public static String FileTransferCacheManager_MoveToCacheFailed_Exists;
	public static String FileTransferCacheManager_MoveToCacheFailed_FileMissing;
	public static String FileTransferCacheManager_MoveToCacheFailed_MoveFailed;
	public static String FileTransferJob_Transferring;
	public static String FileTransferJob_UnknownSession;
	public static String FileTransferManager_DeleteFile;
	public static String FileTransferManager_DeleteFileError;
	public static String FileTransferManager_ExceptionDuringCaching;
	public static String FileTransferManager_ExceptionDuringUpload;
	public static String FileTransferManager_FileDoesNotExist;
	public static String FileTransferManager_FileIdentifierIsNull;
	public static String FileTransferManager_FileIsNull;
	public static String FileTransferManager_FileNoInCache_1;
	public static String FileTransferManager_FileNoInCache_2;
	public static String FileTransferManager_FileNoInCache_3;
	public static String FileTransferManager_NoUploadPendingWithThatId;
	public static String FileTransferManager_UploadFailed;
	public static String FileTransferManager_UploadIsDirectory;
	public static String FileUploadJob_FileUpload;
	public static String FileUploadJob_SendingFile;
	public static String TransferCanceledException_FileTransferCancelled;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
