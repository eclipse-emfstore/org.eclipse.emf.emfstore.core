/*******************************************************************************
 * Copyright (c) 2011-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.startup;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.emf.emfstore.internal.client.model.Configuration;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.startup.ServerHrefMigrator;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * When using the default EMF XMI persistence hrefs between resources are persisted based on the non-normalized URIs of
 * the resources. Since version 1.1 introduced a new URI scheme for EMFStore, files that were persisted with version 1.0
 * and prior need to be migrated.
 * <p>
 * This migrator will update the Hrefs in legacy files on the client side.
 *
 * @author jfaltermeier
 *
 */
public class ClientHrefMigrator extends ServerHrefMigrator {

	private File backup;

	/**
	 * Performs the migration, if needed. Creates a backup beforehand.
	 *
	 * @return <code>true</code> if migration was successful, <code>false</code> if an error occurred and the client
	 *         startup should be canceled.
	 */
	@Override
	public boolean migrate() {

		final String sEMFStoreClient = Configuration.getFileInfo().getWorkspaceDirectory();

		// check if migration is needed
		if (isMigrationNeeded(sEMFStoreClient + "workspace.ucw")) { //$NON-NLS-1$

			if (backup != null) {
				return false;
			}

			try {
				backup = createBackup(Configuration.getFileInfo().getWorkspaceDirectory(),
					Configuration.getFileInfo().getWorkspaceDirectory() + "../backup" + System.currentTimeMillis()); //$NON-NLS-1$
			} catch (final IOException ex) {
				ModelUtil.logException(
					"Error during the backup creation.", ex);
				return false;
			}

			// perform migration
			try {
				doMigrate(sEMFStoreClient);
				return true;
			} catch (final InvocationTargetException ex) {
				ModelUtil.logException(
					"Error during the migration process.", ex);
				return false;
			}
		}
		return true;
	}

	private boolean isMigrationNeeded(String pathToFile) {
		try {
			final String toMatch = getHrefAttribute(pathToFile, "projectSpaces"); //$NON-NLS-1$
			if (toMatch == null) {
				return false;
			}
			return toMatch.contains("projectspace.esp"); //$NON-NLS-1$
		} catch (final ParserConfigurationException ex) {
			ModelUtil.logException(
				"Cannot determine whether migration is needed. Migration will be skipped, backup will be created.", ex);
		} catch (final SAXException ex) {
			ModelUtil.logException(
				"Cannot determine whether migration is needed. Migration will be skipped, backup will be created.", ex);
		} catch (final IOException ex) {
			ModelUtil.logException(
				"Cannot determine whether migration is needed. Migration will be skipped, backup will be created.", ex);
		}
		try {
			backup = createBackup(Configuration.getFileInfo().getWorkspaceDirectory(),
				Configuration.getFileInfo().getWorkspaceDirectory() + "../backup" + System.currentTimeMillis()); //$NON-NLS-1$
		} catch (final IOException ex) {
			backup = new File(""); //$NON-NLS-1$
			ModelUtil.logException(
				"Creating the backup failed.", ex);
		}
		return true;
	}

	private void doMigrate(String sEMFStoreClient) throws InvocationTargetException {
		migrateContainmentHRefs(sEMFStoreClient + "workspace.ucw", "projectSpaces", //$NON-NLS-1$ //$NON-NLS-2$
			new ProjectSpaceRule());

		final File fEMFStoreClient = new File(sEMFStoreClient);
		final File[] projectSpaces = fEMFStoreClient.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith("ps-"); //$NON-NLS-1$
			}
		});
		for (final File f : projectSpaces) {
			final String pS = f.getAbsolutePath() + "/projectspace.esp"; //$NON-NLS-1$
			migrateContainmentHRefs(pS, "project", //$NON-NLS-1$
				new ProjectAndChangePackageRule());
			migrateContainmentHRefs(pS, "localChangePackage", //$NON-NLS-1$
				new ProjectAndChangePackageRule());
			migrateNonContainment(pS, "workspace", new WorkspaceRule()); //$NON-NLS-1$
			migrateNonContainment(pS, "usersession", new UsersessionRule()); //$NON-NLS-1$
		}
	}

	private String getHrefAttribute(String pathToFile,
		String tagName) throws ParserConfigurationException, SAXException, IOException {
		final DocumentBuilderFactory docFactory = DocumentBuilderFactory
			.newInstance();
		final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		final Document doc = docBuilder.parse(new File(pathToFile).toURI().toString());

		final NodeList tagElements = doc.getElementsByTagName(tagName);

		if (tagElements.getLength() > 0) {
			final Node pS = tagElements.item(0);
			final NamedNodeMap attr = pS.getAttributes();
			final Node nodeAttr = attr.getNamedItem("href"); //$NON-NLS-1$
			final String hrefOld = nodeAttr.getTextContent();
			return hrefOld;
		}
		return null;
	}
}
