/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * mkoegel
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.emfstore.client.ESServer;
import org.eclipse.emf.emfstore.client.exceptions.ESServerStartFailedException;
import org.eclipse.emf.emfstore.client.test.common.dsl.Add;
import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil;
import org.eclipse.emf.emfstore.client.test.common.util.ServerUtil;
import org.eclipse.emf.emfstore.internal.client.model.filetransfer.FileDownloadStatus;
import org.eclipse.emf.emfstore.internal.client.model.filetransfer.FileDownloadStatus.Status;
import org.eclipse.emf.emfstore.internal.client.model.filetransfer.FileTransferManager;
import org.eclipse.emf.emfstore.internal.server.exceptions.FatalESException;
import org.eclipse.emf.emfstore.internal.server.exceptions.FileTransferException;
import org.eclipse.emf.emfstore.internal.server.model.FileIdentifier;
import org.eclipse.emf.emfstore.internal.server.model.ModelFactory;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the file manager.
 *
 * @author mkoegel
 *
 */
public class FileManagerTest extends TransmissionTests {

	private static ESServer server;
	private File file;

	@BeforeClass
	public static void beforeClass() {
		try {
			server = ServerUtil.startMockServer().getServer();
			server.login(
				ServerUtil.superUser(),
				ServerUtil.superUserPassword());
		} catch (final IllegalArgumentException ex) {
			fail(ex.getMessage());
		} catch (final ESServerStartFailedException ex) {
			fail(ex.getMessage());
		} catch (final FatalESException ex) {
			fail(ex.getMessage());
		} catch (final ESException ex) {
			fail(ex.getMessage());
		}
	}

	@AfterClass
	public static void afterClass() {
		ServerUtil.stopServer();
	}

	@After
	@Override
	public void after() {
		if (file != null) {
			FileUtils.deleteQuietly(file);
			file = null;
		}
		super.after();
	}

	@Test
	public void testTransfer() throws ESException, IOException, InterruptedException {
		file = File.createTempFile("foo", "tmp"); //$NON-NLS-1$//$NON-NLS-2$
		file.deleteOnExit();
		final FileIdentifier id = getProjectSpace1().addFile(file);
		// dummy change, addFile is not recognized as a change
		Add.toProject(getProjectSpace1().toAPI(), Create.testElement());
		ProjectUtil.commit(getProjectSpace1().toAPI());

		ProjectUtil.update(getProjectSpace2().toAPI());
		final FileDownloadStatus status = getProjectSpace2().getFile(id);
		assertTrue(status != null);
		// wait for file to be completely transferred
		while (status.getStatus() != Status.FINISHED) {
			Thread.sleep(500);
		}
		final FileInputStream fileInputStream = new FileInputStream(file);
		final FileInputStream fileInputStream2 = new FileInputStream(status.getTransferredFile());
		try {
			assertEquals(IOUtils.toByteArray(fileInputStream), IOUtils.toByteArray(fileInputStream2));
		} finally {
			fileInputStream.close();
			fileInputStream2.close();
		}
	}

	@Test
	public void testTransferWithBlocking() throws ESException, IOException, InterruptedException {
		file = File.createTempFile("foo", "tmp"); //$NON-NLS-1$//$NON-NLS-2$
		file.deleteOnExit();
		final FileIdentifier id = getProjectSpace1().addFile(file);
		// dummy change, addFile is not recognized as a change
		Add.toProject(getProjectSpace1().toAPI(), Create.testElement());
		ProjectUtil.commit(getProjectSpace1().toAPI());

		ProjectUtil.update(getProjectSpace1().toAPI());

		final FileDownloadStatus status = getProjectSpace2().getFile(id);
		assertTrue(status != null);
		status.getTransferredFile(true);
		final FileInputStream fileInputStream = new FileInputStream(file);
		final FileInputStream fileInputStream2 = new FileInputStream(status.getTransferredFile(true));
		try {
			assertEquals(IOUtils.toByteArray(fileInputStream), IOUtils.toByteArray(fileInputStream2));
		} finally {
			fileInputStream.close();
			fileInputStream2.close();
		}
	}

	@Test
	public void testFileUploadWithoutDirectProjectSpaceUsage()
		throws IOException, FileTransferException, InterruptedException {
		/* setup */
		file = File.createTempFile("foo", "tmp"); //$NON-NLS-1$//$NON-NLS-2$
		file.deleteOnExit();
		FileUtils.writeStringToFile(file, System.currentTimeMillis() + "FOObar"); //$NON-NLS-1$

		/* act */
		final FileTransferManager transferManager1 = new FileTransferManager(
			getProjectSpace1().getProjectId()/* id */,
			getProjectSpace1().getUsersession()/* usersession */);
		transferManager1.addFile(file, "myId"); //$NON-NLS-1$
		transferManager1.uploadQueuedFiles(new NullProgressMonitor());

		/* assert */
		final FileTransferManager transferManager2 = new FileTransferManager(
			getProjectSpace2().getProjectId()/* id */,
			getProjectSpace2().getUsersession()/* usersession */);
		final FileIdentifier fileIdentifier = ModelFactory.eINSTANCE.createFileIdentifier();
		fileIdentifier.setIdentifier("myId"); //$NON-NLS-1$
		final FileDownloadStatus status = transferManager2.getFile(fileIdentifier, false);
		assertTrue(status != null);
		// wait for file to be completely transferred
		while (status.getStatus() != Status.FINISHED) {
			if (status.getStatus() == Status.FAILED) {
				fail("download failed"); //$NON-NLS-1$
			}
			Thread.sleep(100);
		}
		final File transferredFile = status.getTransferredFile(true);
		final FileInputStream fileInputStream = new FileInputStream(file);
		final FileInputStream fileInputStream2 = new FileInputStream(transferredFile);
		try {
			assertTrue(Arrays.equals(IOUtils.toByteArray(fileInputStream), IOUtils.toByteArray(fileInputStream2)));
		} finally {
			fileInputStream.close();
			fileInputStream2.close();
		}
	}

	@Test
	public void testFileUploadWithoutDirectProjectSpaceUsageOverride()
		throws IOException, FileTransferException, InterruptedException {
		/* setup */
		file = File.createTempFile("foo", "tmp"); //$NON-NLS-1$//$NON-NLS-2$
		file.deleteOnExit();
		FileUtils.writeStringToFile(file, System.currentTimeMillis() + "FOObar"); //$NON-NLS-1$
		final FileTransferManager transferManager1 = new FileTransferManager(
			getProjectSpace1().getProjectId()/* id */,
			getProjectSpace1().getUsersession()/* usersession */);
		transferManager1.addFile(file, "myId"); //$NON-NLS-1$
		transferManager1.uploadQueuedFiles(new NullProgressMonitor());

		/* act */
		final File file2 = File.createTempFile("foo2", "tmp"); //$NON-NLS-1$//$NON-NLS-2$
		file2.deleteOnExit();
		FileUtils.writeStringToFile(file2, System.currentTimeMillis() + "FOObar2"); //$NON-NLS-1$
		transferManager1.addFile(file2, "myId"); //$NON-NLS-1$
		transferManager1.uploadQueuedFiles(new NullProgressMonitor());

		/* assert */
		final FileTransferManager transferManager2 = new FileTransferManager(
			getProjectSpace2().getProjectId()/* id */,
			getProjectSpace2().getUsersession()/* usersession */);
		final FileIdentifier fileIdentifier = ModelFactory.eINSTANCE.createFileIdentifier();
		fileIdentifier.setIdentifier("myId"); //$NON-NLS-1$
		final FileDownloadStatus status = transferManager2.getFile(fileIdentifier, false);
		assertTrue(status != null);
		// wait for file to be completely transferred
		while (status.getStatus() != Status.FINISHED) {
			if (status.getStatus() == Status.FAILED) {
				fail("download failed"); //$NON-NLS-1$
			}
			Thread.sleep(100);
		}
		final File transferredFile = status.getTransferredFile(true);
		final FileInputStream fileInputStream = new FileInputStream(file2);
		final FileInputStream fileInputStream2 = new FileInputStream(transferredFile);
		try {
			assertTrue(Arrays.equals(IOUtils.toByteArray(fileInputStream), IOUtils.toByteArray(fileInputStream2)));
		} finally {
			fileInputStream.close();
			fileInputStream2.close();
		}
	}

	@Test
	public void testFileUploadWithoutDirectProjectSpaceUsagePathSeparatorInId()
		throws IOException, FileTransferException, InterruptedException {
		/* setup */
		file = File.createTempFile("foo", "tmp"); //$NON-NLS-1$//$NON-NLS-2$
		file.deleteOnExit();
		FileUtils.writeStringToFile(file, System.currentTimeMillis() + "FOObar"); //$NON-NLS-1$

		/* act */
		final FileTransferManager transferManager1 = new FileTransferManager(
			getProjectSpace1().getProjectId()/* id */,
			getProjectSpace1().getUsersession()/* usersession */);
		transferManager1.addFile(file, "myFolder/myId"); //$NON-NLS-1$
		transferManager1.uploadQueuedFiles(new NullProgressMonitor());

		/* assert */
		final FileTransferManager transferManager2 = new FileTransferManager(
			getProjectSpace2().getProjectId()/* id */,
			getProjectSpace2().getUsersession()/* usersession */);
		final FileIdentifier fileIdentifier = ModelFactory.eINSTANCE.createFileIdentifier();
		fileIdentifier.setIdentifier("myFolder/myId"); //$NON-NLS-1$
		final FileDownloadStatus status = transferManager2.getFile(fileIdentifier, false);
		assertTrue(status != null);
		// wait for file to be completely transferred
		while (status.getStatus() != Status.FINISHED) {
			if (status.getStatus() == Status.FAILED) {
				fail("download failed"); //$NON-NLS-1$
			}
			Thread.sleep(100);
		}
		final File transferredFile = status.getTransferredFile(true);
		final FileInputStream fileInputStream = new FileInputStream(file);
		final FileInputStream fileInputStream2 = new FileInputStream(transferredFile);
		try {
			assertTrue(Arrays.equals(IOUtils.toByteArray(fileInputStream), IOUtils.toByteArray(fileInputStream2)));
		} finally {
			fileInputStream.close();
			fileInputStream2.close();
		}
		assertEquals("myId", transferredFile.getName()); //$NON-NLS-1$
		assertEquals("myFolder", transferredFile.getParentFile().getName()); //$NON-NLS-1$
	}

	@Test
	public void testDelete() throws IOException, FileTransferException, InterruptedException {
		/* setup */
		file = File.createTempFile("foo", "tmp"); //$NON-NLS-1$//$NON-NLS-2$
		file.deleteOnExit();
		FileUtils.writeStringToFile(file, System.currentTimeMillis() + "FOObar"); //$NON-NLS-1$

		final FileTransferManager transferManager1 = new FileTransferManager(
			getProjectSpace1().getProjectId()/* id */,
			getProjectSpace1().getUsersession()/* usersession */);
		transferManager1.addFile(file, "myId"); //$NON-NLS-1$
		transferManager1.uploadQueuedFiles(new NullProgressMonitor());

		/* assert */
		final FileTransferManager transferManager2 = new FileTransferManager(
			getProjectSpace2().getProjectId()/* id */,
			getProjectSpace2().getUsersession()/* usersession */);
		final FileIdentifier fileIdentifier = ModelFactory.eINSTANCE.createFileIdentifier();
		fileIdentifier.setIdentifier("myId"); //$NON-NLS-1$
		final FileDownloadStatus status = transferManager2.getFile(fileIdentifier, false);
		assertTrue(status != null);
		// wait for file to be completely transferred
		while (status.getStatus() != Status.FINISHED) {
			if (status.getStatus() == Status.FAILED) {
				fail("download failed"); //$NON-NLS-1$
			}
			Thread.sleep(100);
		}

		// act
		transferManager2.deleteFile(new NullProgressMonitor(), fileIdentifier);
		final FileDownloadStatus status2 = transferManager2.getFile(fileIdentifier, false);

		while (status2.getStatus() == Status.NOT_STARTED) {
			TimeUnit.MILLISECONDS.sleep(100);
		}

		// assert
		// status must be cancelled as the file does not exist
		assertEquals(status2.getStatus(), Status.CANCELLED);
		assertFalse(transferManager2.getFileInfo(fileIdentifier).isCached());
	}
}
