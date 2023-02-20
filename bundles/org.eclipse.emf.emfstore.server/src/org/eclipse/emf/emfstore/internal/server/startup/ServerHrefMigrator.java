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
package org.eclipse.emf.emfstore.internal.server.startup;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.emf.emfstore.internal.common.model.util.FileUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * When using the default EMF XMI persistence HRefs between resources are persisted based on the non-normalized URIs of
 * the resources. Since version 1.1 introduced a new URI scheme for EMFStore, files that were persisted with version 1.0
 * and prior need to be migrated.
 * <p>
 * This migrator will update the HRefs in legacy files on the server side.
 *
 * @author jfaltermeier
 *
 */
public class ServerHrefMigrator {

	private static final String STORAGE_USS = "storage.uss"; //$NON-NLS-1$
	private static final String PROJECT_PREFIX = "project-"; //$NON-NLS-1$
	private File backup;
	private List<String> corruptProjectIds = new ArrayList<String>();

	/**
	 * Performs the migration, if needed. Creates a backup beforehand.
	 *
	 * @return <code>true</code> if migration was successful, <code>false</code> if an error occurred and the server
	 *         startup should be canceled.
	 */
	public boolean migrate() {

		final String serverHome = ServerConfiguration.getServerHome();

		// check if migration is needed
		if (isMigrationNeeded(serverHome + STORAGE_USS)) {

			if (backup != null) {
				return false;
			}

			try {
				backup = createBackup(ServerConfiguration.getServerHome(),
					ServerConfiguration.getServerHome() + "../backup" + System.currentTimeMillis()); //$NON-NLS-1$
			} catch (final IOException ex) {
				ModelUtil.logException(
					Messages.ServerHrefMigrator_ErrorDuringBackup, ex);
				return false;
			}

			// perform migration
			try {
				corruptProjectIds = doMigrate(serverHome);
				return true;
			} catch (final InvocationTargetException ex) {
				ModelUtil.logException(
					Messages.ServerHrefMigrator_ErrorDuringMigration, ex);
				return false;
			}
		}
		return true;
	}

	/** Create a XML document builder with disabled external entity resolvement to prevent XXE attacks */
	protected static DocumentBuilder createSafeDocumentBuilder() throws ParserConfigurationException {
		// See
		// https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html#jaxp-documentbuilderfactory-saxparserfactory-and-dom4j
		// on the features to disable
		final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setFeature("http://xml.org/sax/features/external-general-entities", false); //$NON-NLS-1$
		docFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false); //$NON-NLS-1$
		docFactory.setExpandEntityReferences(false);
		docFactory.setXIncludeAware(false);
		final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		return docBuilder;
	}

	protected static TransformerFactory createSafeTransformerFactory() {
		// Prevent XXE according to
		// https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html#transformerfactory
		final TransformerFactory factory = TransformerFactory.newInstance();
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); //$NON-NLS-1$
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, ""); //$NON-NLS-1$
		return factory;
	}

	private boolean isMigrationNeeded(String pathToServerSpace) {
		try {
			final String toMatch = getProjectAttribute(pathToServerSpace);
			if (toMatch == null) {
				return false;
			}
			return toMatch.contains("projectHistory.uph"); //$NON-NLS-1$
		} catch (final ParserConfigurationException ex) {
			ModelUtil.logException(
				Messages.ServerHrefMigrator_SkipMigration, ex);
		} catch (final SAXException ex) {
			ModelUtil.logException(
				Messages.ServerHrefMigrator_SkipMigration, ex);
		} catch (final IOException ex) {
			ModelUtil.logException(
				Messages.ServerHrefMigrator_SkipMigration, ex);
		}
		try {
			backup = createBackup(ServerConfiguration.getServerHome(),
				ServerConfiguration.getServerHome() + "../backup" + System.currentTimeMillis()); //$NON-NLS-1$
		} catch (final IOException ex) {
			backup = new File(""); //$NON-NLS-1$
			ModelUtil.logException(
				Messages.ServerHrefMigrator_BackupFailed, ex);
		}
		return true;
	}

	/**
	 * Creates a backup.
	 *
	 * @param from path to source
	 * @param to path to destination
	 * @return the backup file
	 * @throws IOException on a IO problem during backup creation
	 */
	protected File createBackup(String from, String to) throws IOException {
		final File sourceFile = new File(from);
		final File backupFile = new File(to);
		FileUtil.copyDirectory(sourceFile, backupFile);
		return backupFile;
	}

	private List<String> doMigrate(String serverHome) throws InvocationTargetException {
		ModelUtil.logInfo("[ServerHrefMigrator] Execute migration for server home: " + serverHome); //$NON-NLS-1$
		migrateNonContainment(serverHome + STORAGE_USS, "projects", new ServerSpaceRule()); //$NON-NLS-1$

		final File serverHomeFile = new File(serverHome);
		final File[] projectFiles = serverHomeFile.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith(PROJECT_PREFIX);
			}
		});

		final List<String> corruptProjectIds = new ArrayList<String>();

		for (final File f : projectFiles) {
			try {
				final String projectHistoryPath = f.getAbsolutePath() + "/projectHistory.uph"; //$NON-NLS-1$

				if (!new File(projectHistoryPath).exists()) {
					ModelUtil.logWarning(MessageFormat.format(
						Messages.ServerHrefMigrator_HistoryFileDoeNotExists,
						projectHistoryPath));
					removeReferencesToCorruptProject(serverHome + STORAGE_USS,
						f.getName().substring(PROJECT_PREFIX.length()));
					continue;
				}

				migrateContainmentHRefs(projectHistoryPath, "versions", //$NON-NLS-1$
					new VersionRule());

				final File[] versions = f.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.startsWith("version-"); //$NON-NLS-1$
					}
				});
				for (final File version : versions) {
					final String versionPath = version.getAbsolutePath();
					migrateNonContainment(versionPath, "nextVersion", new VersionMultiRule()); //$NON-NLS-1$
					migrateNonContainment(versionPath, "previousVersion", new VersionMultiRule()); //$NON-NLS-1$
					migrateNonContainment(versionPath, "ancestorVersion", new VersionMultiRule()); //$NON-NLS-1$
					migrateNonContainment(versionPath, "branchedVersions", new VersionMultiRule()); //$NON-NLS-1$
					migrateNonContainment(versionPath, "mergedToVersion", new VersionMultiRule()); //$NON-NLS-1$
					migrateNonContainment(versionPath, "mergedFromVersion", new VersionMultiRule()); //$NON-NLS-1$
				}
			} catch (final InvocationTargetException exception) {
				ModelUtil.logException(MessageFormat.format(
					Messages.ServerHrefMigrator_MigrationFailed, f.getAbsolutePath()), exception);
				corruptProjectIds.add(f.getName().substring(PROJECT_PREFIX.length()));
				continue;
			}
		}

		return corruptProjectIds;
	}

	private String getProjectAttribute(String pathToFile) throws ParserConfigurationException, SAXException,
		IOException {
		final DocumentBuilder docBuilder = createSafeDocumentBuilder();
		final Document doc = docBuilder.parse(pathToFile);

		final Node serverSpace = doc.getFirstChild();
		final NamedNodeMap attr = serverSpace.getAttributes();
		final Node nodeAttr = attr.getNamedItem("projects"); //$NON-NLS-1$
		if (nodeAttr == null) {
			return null;
		}
		final String projectsOld = nodeAttr.getTextContent();
		final String[] projects = projectsOld.split(" "); //$NON-NLS-1$
		if (projects.length < 1) {
			return null;
		}
		return projects[0];
	}

	/**
	 * Updates the href attribute in tags with the given name.
	 *
	 * @param pathToFile the path of the xmi file to be updated
	 * @param tagName the tag for which the hrefs are to be updated
	 * @param rule the rule computing the new value
	 * @throws InvocationTargetException in case of error
	 */
	protected void migrateContainmentHRefs(String pathToFile,
		String tagName, UpdateXMIAttributeRule rule) throws InvocationTargetException {

		try {
			final DocumentBuilder docBuilder = createSafeDocumentBuilder();
			final Document doc = docBuilder.parse(pathToFile);

			final NodeList tagElements = doc.getElementsByTagName(tagName);

			for (int i = 0; i < tagElements.getLength(); i++) {
				final Node pS = tagElements.item(i);
				final NamedNodeMap attr = pS.getAttributes();
				final Node nodeAttr = attr.getNamedItem("href"); //$NON-NLS-1$
				final String hrefOld = nodeAttr.getTextContent();
				final String hrefNew = rule.getNewAttribute(hrefOld);
				nodeAttr.setTextContent(hrefNew);
			}

			final TransformerFactory transformerFactory = createSafeTransformerFactory();
			final Transformer transformer = transformerFactory.newTransformer();
			final DOMSource source = new DOMSource(doc);
			final StreamResult result = new StreamResult(new File(pathToFile));
			transformer.transform(source, result);
		} catch (final DOMException ex) {
			throw new InvocationTargetException(ex);
		} catch (final TransformerConfigurationException ex) {
			throw new InvocationTargetException(ex);
		} catch (final ParserConfigurationException ex) {
			throw new InvocationTargetException(ex);
		} catch (final SAXException ex) {
			throw new InvocationTargetException(ex);
		} catch (final IOException ex) {
			throw new InvocationTargetException(ex);
		} catch (final TransformerFactoryConfigurationError ex) {
			throw new InvocationTargetException(ex);
		} catch (final TransformerException ex) {
			throw new InvocationTargetException(ex);
		}
	}

	private void removeReferencesToCorruptProject(String serverHome, String projectId)
		throws InvocationTargetException {
		try {
			final DocumentBuilder builder = createSafeDocumentBuilder();
			final Document doc = builder.parse(serverHome);
			final XPathFactory xPathfactory = XPathFactory.newInstance();
			final XPath xpath = xPathfactory.newXPath();
			final XPathExpression expr = xpath.compile("//projects[@id=\"" + projectId + "\"]"); //$NON-NLS-1$ //$NON-NLS-2$
			final NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < nl.getLength(); i++) {
				nl.item(i).getParentNode().removeChild(nl.item(i));
			}
			final TransformerFactory transformerFactory = createSafeTransformerFactory();
			final Transformer transformer = transformerFactory.newTransformer();
			final DOMSource source = new DOMSource(doc);
			final StreamResult result = new StreamResult(new File(serverHome));
			transformer.transform(source, result);
		} catch (final ParserConfigurationException ex) {
			throw new InvocationTargetException(ex);
		} catch (final SAXException ex) {
			throw new InvocationTargetException(ex);
		} catch (final IOException ex) {
			throw new InvocationTargetException(ex);
		} catch (final XPathExpressionException ex) {
			throw new InvocationTargetException(ex);
		} catch (final TransformerConfigurationException ex) {
			throw new InvocationTargetException(ex);
		} catch (final TransformerException ex) {
			throw new InvocationTargetException(ex);
		}
	}

	/**
	 * Updates the attribute with the given name.
	 *
	 * @param pathToFile the path of the xmi file to be updated
	 * @param tagName the tag for which the attribute contents are to be updated
	 * @param rule the rule computing the new value
	 * @throws InvocationTargetException in case of error
	 */
	protected void migrateNonContainment(String pathToFile,
		String tagName, UpdateXMIAttributeRule rule) throws InvocationTargetException {
		try {
			final DocumentBuilder docBuilder = createSafeDocumentBuilder();
			final Document doc = docBuilder.parse(pathToFile);

			final Node serverSpace = doc.getFirstChild();
			final NamedNodeMap attr = serverSpace.getAttributes();
			final Node nodeAttr = attr.getNamedItem(tagName);
			if (nodeAttr == null) {
				return;
			}
			final String attributeOld = nodeAttr.getTextContent();
			final String attributeNew = rule.getNewAttribute(attributeOld);
			nodeAttr.setTextContent(attributeNew);

			final TransformerFactory transformerFactory = createSafeTransformerFactory();
			final Transformer transformer = transformerFactory.newTransformer();
			final DOMSource source = new DOMSource(doc);
			final StreamResult result = new StreamResult(new File(pathToFile));
			transformer.transform(source, result);
		} catch (final DOMException ex) {
			throw new InvocationTargetException(ex);
		} catch (final TransformerConfigurationException ex) {
			throw new InvocationTargetException(ex);
		} catch (final ParserConfigurationException ex) {
			throw new InvocationTargetException(ex);
		} catch (final SAXException ex) {
			throw new InvocationTargetException(ex);
		} catch (final IOException ex) {
			throw new InvocationTargetException(ex);
		} catch (final TransformerFactoryConfigurationError ex) {
			throw new InvocationTargetException(ex);
		} catch (final TransformerException ex) {
			throw new InvocationTargetException(ex);
		}
	}

	/**
	 * Returns a list of project IDs that could not be migrated.
	 *
	 * @return the corruptProjectIds
	 */
	public List<String> getCorruptProjectIds() {
		return corruptProjectIds;
	}
}
