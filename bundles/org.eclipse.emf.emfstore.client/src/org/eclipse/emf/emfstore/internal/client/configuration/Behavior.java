/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto von Wesendonk, Edgar Mueller, Maximilian Koegel - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.configuration;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.client.ESServer;
import org.eclipse.emf.emfstore.client.handler.ESChecksumErrorHandler;
import org.eclipse.emf.emfstore.client.handler.ESOperationModifier;
import org.eclipse.emf.emfstore.client.provider.ESClientConfigurationProvider;
import org.eclipse.emf.emfstore.client.util.ESCopier;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionElement;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPointException;
import org.eclipse.emf.emfstore.internal.client.model.ModelFactory;
import org.eclipse.emf.emfstore.internal.client.model.ServerInfo;
import org.eclipse.emf.emfstore.internal.client.model.Usersession;
import org.eclipse.emf.emfstore.internal.client.model.connectionmanager.KeyStoreManager;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.DefaultCopier;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESServerImpl;
import org.eclipse.emf.emfstore.internal.client.model.util.ChecksumErrorHandler;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;

import com.google.common.base.Optional;

/**
 * Configuration options that influence the behavior of the client.
 *
 * @author emueller
 * @author ovonwesen
 * @author mkoegel
 */
public class Behavior {

	/**
	 * The checksum value that is used in case no checksum should be computed.
	 */
	public static final long NO_CHECKSUM = -1;

	/**
	 * Base identifier for all change recording related options.
	 */
	public static final String RESOURCE_OPTIONS_EXTENSION_POINT_NAME = "org.eclipse.emf.emfstore.client.changeRecordingOptions"; //$NON-NLS-1$

	/**
	 * Auto save option identifier.
	 */
	public static final String AUTO_SAVE_EXTENSION_POINT_ATTRIBUTE_NAME = "autoSave"; //$NON-NLS-1$

	/**
	 * Re-record option identifier.
	 */
	public static final String RERECORD_LOCAL_CHANGES_EXTENSION_POINT_ATTRIBUTE_NAME = "rerecordLocalChanges"; //$NON-NLS-1$

	/**
	 * 'Cut off incoming references' option identifier.
	 */
	public static final String CUT_OFF_INCOMING_CROSS_REFS_EXTENSION_POINT_ATTRIBUTE_NAME = "cutOffIncomingCrossReferences"; //$NON-NLS-1$

	/**
	 * 'Force commands' option identifier.
	 */
	public static final String FORCE_COMMANDS_EXTENSION_POINT_ATTRIBUTE_NAME = "forceCommands"; //$NON-NLS-1$

	/**
	 * 'Deny add cut elements to model elements feature' option identifier.
	 */
	public static final String DENY_ADD_CUT_ELEMENTS_TO_MODELELEMENTS_FEATURE_EXTENSION_POINT_ATTRIBUTE_NAME = "denyAddCutElementsToModelElements"; //$NON-NLS-1$

	/**
	 * Use in-memory change packages option identifier.
	 */
	public static final String USE_IN_MEMORY_CHANGE_PACKAGE = "useInMemoryChangePackage"; //$NON-NLS-1$

	/**
	 * Change package fragment size option identifier.
	 */
	public static final String CHANGEPACKAGE_FRAGMENT_SIZE = "changePackageFragmentationSize"; //$NON-NLS-1$

	/**
	 * Operation modifier option identifier.
	 */
	public static final String OPERATION_MODIFIER = "operationModifier"; //$NON-NLS-1$

	/**
	 * Copier option identifier.
	 */
	public static final String COPIER = "copier"; //$NON-NLS-1$

	private static Boolean isAutoSaveActive;
	private static Boolean isRerecordingActive;
	private static Boolean isCutOffIncomingCrossReferencesActive;
	private static Boolean isForceCommandsActive;
	private static Boolean isDenyAddCutElementsToModelElementsFeatureActive;
	private static Boolean isUseMemoryChangePackageActive;
	private static Optional<Integer> changePackageFragmentSize;
	private static ESOperationModifier operationModifier;
	private static List<ESCopier> copierList;

	private ESChecksumErrorHandler checksumErrorHandler;

	/**
	 * Whether to enable the automatic saving of the workspace.
	 * If disabled, performance improves vastly, but clients have to
	 * perform the saving of the workspace manually.
	 *
	 * @param enabled whether to enable auto save
	 */
	public void setAutoSave(boolean enabled) {
		isAutoSaveActive = Boolean.valueOf(enabled);
	}

	/**
	 * Whether auto-save is enabled.
	 *
	 * @return true, if auto-save is enabled, false otherwise
	 */
	public boolean isAutoSaveEnabled() {
		if (isAutoSaveActive == null) {
			isAutoSaveActive = new ESExtensionPoint(RESOURCE_OPTIONS_EXTENSION_POINT_NAME)
				.getBoolean(
					AUTO_SAVE_EXTENSION_POINT_ATTRIBUTE_NAME, false);
		}
		return isAutoSaveActive;
	}

	/**
	 * Whether re-recording is enabled.
	 *
	 * @return <code>true</code>, if re-recording is enabled, <code>false</code> otherwise
	 */
	public Boolean isRerecordingActivated() {

		if (isRerecordingActive == null) {
			isRerecordingActive = new ESExtensionPoint(RESOURCE_OPTIONS_EXTENSION_POINT_NAME)
				.getBoolean(RERECORD_LOCAL_CHANGES_EXTENSION_POINT_ATTRIBUTE_NAME, Boolean.TRUE);
		}

		return isRerecordingActive;
	}

	/**
	 * Whether incoming cross references should be cut off.
	 *
	 * @return {@link Boolean#TRUE}, if incoming cross references are cut off, {@link Boolean#FALSE} otherwise
	 */
	public Boolean isCutOffIncomingCrossReferencesActivated() {

		if (isCutOffIncomingCrossReferencesActive == null) {
			isCutOffIncomingCrossReferencesActive = new ESExtensionPoint(RESOURCE_OPTIONS_EXTENSION_POINT_NAME)
				.getBoolean(CUT_OFF_INCOMING_CROSS_REFS_EXTENSION_POINT_ATTRIBUTE_NAME, Boolean.TRUE);
		}

		return isCutOffIncomingCrossReferencesActive;
	}

	/**
	 * Whether the usage of commands is enforced. Default is {@link Boolean#FALSE}.
	 *
	 * @return {@link Boolean#TRUE}, if usage of commands is enforced, {@link Boolean#FALSE} otherwise
	 */
	public Boolean isForceCommandsActived() {

		if (isForceCommandsActive == null) {
			isForceCommandsActive = new ESExtensionPoint(RESOURCE_OPTIONS_EXTENSION_POINT_NAME)
				.getBoolean(FORCE_COMMANDS_EXTENSION_POINT_ATTRIBUTE_NAME, Boolean.FALSE);
		}

		return isForceCommandsActive;
	}

	/**
	 * Whether cut elements are added automatically as regular model elements by default.
	 *
	 * @return {@link Boolean#TRUE}, if cut elements are added automatically as regular elements, {@link Boolean#FALSE}
	 *         otherwise
	 */
	public Boolean isDenyAddCutElementsToModelElementsFeatureActived() {

		if (isDenyAddCutElementsToModelElementsFeatureActive == null) {
			isDenyAddCutElementsToModelElementsFeatureActive = new ESExtensionPoint(
				RESOURCE_OPTIONS_EXTENSION_POINT_NAME)
					.getBoolean(DENY_ADD_CUT_ELEMENTS_TO_MODELELEMENTS_FEATURE_EXTENSION_POINT_ATTRIBUTE_NAME,
						Boolean.FALSE);
		}

		return isDenyAddCutElementsToModelElementsFeatureActive;
	}

	/**
	 * Whether the checksum check is active. If true, and checksum comparison fails, an {@link ESChecksumErrorHandler}
	 * will be active.
	 *
	 * @return true, if the checksum comparison is activated, false otherwise
	 */
	public boolean isChecksumCheckActive() {
		final ESExtensionPoint extensionPoint = new ESExtensionPoint(
			"org.eclipse.emf.emfstore.client.checksumErrorHandler"); //$NON-NLS-1$
		return extensionPoint.getBoolean("isActive", true); //$NON-NLS-1$
	}

	/**
	 * Returns the active {@link ESChecksumErrorHandler}. The default is {@link ChecksumErrorHandler#AUTOCORRECT}.
	 *
	 * @return the active checksum error handler
	 */
	public ESChecksumErrorHandler getChecksumErrorHandler() {

		if (checksumErrorHandler == null) {

			final ESExtensionPoint extensionPoint = new ESExtensionPoint(
				"org.eclipse.emf.emfstore.client.checksumErrorHandler"); //$NON-NLS-1$

			final ESExtensionElement elementWithHighestPriority = extensionPoint.getElementWithHighestPriority();

			if (elementWithHighestPriority != null) {
				final ESChecksumErrorHandler errorHandler = elementWithHighestPriority
					.getClass("errorHandler", //$NON-NLS-1$
						ESChecksumErrorHandler.class);

				if (errorHandler != null) {
					checksumErrorHandler = errorHandler;
				}
			}

			if (checksumErrorHandler == null) {
				checksumErrorHandler = ChecksumErrorHandler.CANCEL;
			}
		}

		return checksumErrorHandler;
	}

	/**
	 * Set the active {@link ESChecksumErrorHandler}.
	 *
	 * @param errorHandler
	 *            the error handler to be set
	 */
	public void setChecksumErrorHandler(ESChecksumErrorHandler errorHandler) {
		checksumErrorHandler = errorHandler;
	}

	/**
	 * Whether the in-memory change package should be used.
	 *
	 * @return {@code true}, if the in-memory change package should be used, {@code false} otherwise
	 */
	public boolean useInMemoryChangePackage() {
		if (isUseMemoryChangePackageActive == null) {
			final ESExtensionPoint extensionPoint = new ESExtensionPoint(RESOURCE_OPTIONS_EXTENSION_POINT_NAME)
				.setThrowException(true);
			try {
				isUseMemoryChangePackageActive = extensionPoint.getBoolean(USE_IN_MEMORY_CHANGE_PACKAGE, false);
			} catch (final ESExtensionPointException e) {
				isUseMemoryChangePackageActive = Boolean.getBoolean("emfstore.inMemoryChangePackage"); //$NON-NLS-1$
			}
		}
		return isUseMemoryChangePackageActive;
	}

	/**
	 * Returns the change package fragments size.
	 *
	 * @return the fragment size in operations, or absent, if none configured
	 */
	public Optional<Integer> getChangePackageFragmentSize() {
		if (changePackageFragmentSize == null) {
			final Integer fragmentSize = new ESExtensionPoint(RESOURCE_OPTIONS_EXTENSION_POINT_NAME)
				.getInteger(CHANGEPACKAGE_FRAGMENT_SIZE);
			if (fragmentSize == null) {
				changePackageFragmentSize = Optional.absent();
			} else {
				changePackageFragmentSize = Optional.of(fragmentSize);
			}
		}

		return changePackageFragmentSize;
	}

	/**
	 * Returns the operation modifier.
	 *
	 * @return the operation modifier in use
	 */
	public ESOperationModifier getOperationModifier() {
		if (operationModifier == null) {
			final ESOperationModifier modifier = new ESExtensionPoint(RESOURCE_OPTIONS_EXTENSION_POINT_NAME)
				.getClass(OPERATION_MODIFIER, ESOperationModifier.class);
			if (modifier == null) {
				operationModifier = new ESOperationModifier() {
					// return operations unaltered
					public List<AbstractOperation> modify(List<AbstractOperation> operations, Command command) {
						return operations;
					}
				};
			} else {
				operationModifier = modifier;
			}
		}

		return operationModifier;
	}

	/**
	 * Returns the copier that is used to copy {@link EObject}s.
	 *
	 * @param eObject the {@link EObject} to be copied
	 *
	 * @return the copier
	 */
	public ESCopier getESCopierFor(EObject eObject) {
		if (copierList == null) {
			copierList = new ESExtensionPoint(RESOURCE_OPTIONS_EXTENSION_POINT_NAME).getClasses(COPIER, ESCopier.class);
		}

		ESCopier selectedCopier = new DefaultCopier();
		final int maxPriority = -1;
		for (final ESCopier copier : copierList) {
			if (copier.shouldHandle(eObject) > maxPriority) {
				selectedCopier = copier;
			}
		}

		return selectedCopier;
	}

	/**
	 * Sets the fragment size to be used when splitting change packages.
	 *
	 * @param fragmentSize
	 *            the fragment size (operation count)
	 */
	public void setChangePackageFragmentSize(Optional<Integer> fragmentSize) {
		changePackageFragmentSize = fragmentSize;
	}

	/**
	 * Get the default server info.
	 *
	 * @return server info
	 */
	public List<ServerInfo> getDefaultServerInfos() {
		final ESClientConfigurationProvider provider = new ESExtensionPoint(
			"org.eclipse.emf.emfstore.client.defaultConfigurationProvider") //$NON-NLS-1$
				.getClass("providerClass", //$NON-NLS-1$
					ESClientConfigurationProvider.class);
		final ArrayList<ServerInfo> result = new ArrayList<ServerInfo>();
		if (provider != null) {
			final List<ESServer> defaultServerInfos = provider.getDefaultServerInfos();

			for (final ESServer server : defaultServerInfos) {
				result.add(((ESServerImpl) server).toInternalAPI());
			}

			return result;
		}
		result.add(getLocalhostServerInfo());
		return result;
	}

	private ServerInfo getLocalhostServerInfo() {
		final ServerInfo serverInfo = ModelFactory.eINSTANCE.createServerInfo();
		serverInfo.setName("Localhost Server"); //$NON-NLS-1$
		serverInfo.setPort(8080);
		serverInfo.setUrl("localhost"); //$NON-NLS-1$
		serverInfo.setCertificateAlias(KeyStoreManager.DEFAULT_CERTIFICATE);

		final Usersession superUsersession = ModelFactory.eINSTANCE.createUsersession();
		superUsersession.setServerInfo(serverInfo);
		superUsersession.setPassword("super"); //$NON-NLS-1$
		superUsersession.setSavePassword(true);
		superUsersession.setUsername("super"); //$NON-NLS-1$
		serverInfo.setLastUsersession(superUsersession);

		return serverInfo;
	}

}
