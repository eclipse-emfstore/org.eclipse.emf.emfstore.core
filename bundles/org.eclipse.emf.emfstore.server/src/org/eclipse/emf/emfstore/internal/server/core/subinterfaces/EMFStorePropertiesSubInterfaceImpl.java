/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * groeber
 * emueller
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.core.subinterfaces;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.emfstore.internal.common.model.EMFStoreProperty;
import org.eclipse.emf.emfstore.internal.server.core.AbstractEmfstoreInterface;
import org.eclipse.emf.emfstore.internal.server.core.AbstractSubEmfstoreInterface;
import org.eclipse.emf.emfstore.internal.server.core.MonitorProvider;
import org.eclipse.emf.emfstore.internal.server.exceptions.FatalESException;
import org.eclipse.emf.emfstore.internal.server.model.ProjectHistory;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.server.auth.ESMethod;
import org.eclipse.emf.emfstore.server.auth.ESMethod.MethodId;
import org.eclipse.emf.emfstore.server.exceptions.ESException;

/**
 * The {@link EMFStorePropertiesSubInterfaceImpl} class is responsible for
 * handling modifications of EMFStore properties.
 *
 * @author groeber
 * @author emueller
 */
public class EMFStorePropertiesSubInterfaceImpl extends AbstractSubEmfstoreInterface {

	private static final String EMFSTORE_PROPERTIES_MONITOR = "EmfStorePropertiesMonitor"; //$NON-NLS-1$
	private final Map<ProjectHistory, Map<String, EMFStoreProperty>> cache;

	/**
	 * @param parentInterface
	 *            the parent interface
	 * @throws FatalESException
	 *             if any fatal error occurs
	 */
	public EMFStorePropertiesSubInterfaceImpl(AbstractEmfstoreInterface parentInterface) throws FatalESException {
		super(parentInterface);
		cache = new LinkedHashMap<ProjectHistory, Map<String, EMFStoreProperty>>();
	}

	/**
	 * Set the Shared Properties from client on server.
	 *
	 * @param properties
	 *            properties to be set
	 * @param projectId
	 *            Project where the properties should be saved
	 * @throws ESException
	 *             if the specified project does not exist
	 *
	 * @return a list of properties that could not be updated since they are outdated
	 */
	@ESMethod(MethodId.SETEMFPROPERTIES)
	public List<EMFStoreProperty> setEMFProperties(List<EMFStoreProperty> properties, ProjectId projectId)
		throws ESException {
		sanityCheckObjects(properties, projectId);

		synchronized (MonitorProvider.getInstance().getMonitor(EMFSTORE_PROPERTIES_MONITOR)) {

			final List<EMFStoreProperty> rejectedProperties = new ArrayList<EMFStoreProperty>();
			final ProjectHistory history = findHistory(projectId);

			if (history == null) {
				throw new ESException(Messages.EMFStorePropertiesSubInterfaceImpl_Project_Does_Not_Exist);
			}

			final EList<EMFStoreProperty> sharedProperties = history.getSharedProperties();
			final Set<EMFStoreProperty> replacedProperties = new LinkedHashSet<EMFStoreProperty>();

			for (final EMFStoreProperty property : properties) {
				final EMFStoreProperty foundProperty = findProperty(history, property.getKey());

				if (foundProperty == null) {
					// property has not been shared yet
					sharedProperties.add(property);
					updateCache(history, property);

					if (property.isVersioned()) {
						property.increaseVersion();
					}
				} else {
					if (property.isVersioned()) {
						if (property.getVersion() == foundProperty.getVersion()) {
							// update property
							sharedProperties.set(sharedProperties.indexOf(foundProperty), property);
							replacedProperties.add(foundProperty);
							property.increaseVersion();
						} else {
							// received property is outdated, return current property
							rejectedProperties.add(foundProperty);
						}
					} else {
						sharedProperties.set(sharedProperties.indexOf(foundProperty), property);
						replacedProperties.add(foundProperty);
					}
				}
			}

			try {
				getServerSpace().save();
			} catch (final IOException e) {
				// rollback
				sharedProperties.removeAll(properties);
				sharedProperties.addAll(replacedProperties);
				throw new ESException(Messages.EMFStorePropertiesSubInterfaceImpl_Properties_Not_Set, e);
			}

			return rejectedProperties;
		}
	}

	/**
	 * Return the Properties for a specific Project.
	 *
	 * @param projectId
	 *            ProjectId for the properties
	 * @return EMap containing the Key string and the property value
	 * @throws ESException
	 *             if specified property does not exist
	 */
	@ESMethod(MethodId.GETEMFPROPERTIES)
	public List<EMFStoreProperty> getEMFProperties(ProjectId projectId) throws ESException {
		sanityCheckObjects(projectId);

		final ProjectHistory history = findHistory(projectId);

		if (history != null) {
			final List<EMFStoreProperty> temp = new ArrayList<EMFStoreProperty>();
			for (final EMFStoreProperty prop : history.getSharedProperties()) {
				temp.add(prop);
			}
			return temp;
		}

		throw new ESException("The Project does not exist on the server. Cannot set the properties."); //$NON-NLS-1$

	}

	/**
	 * Find the {@link ProjectHistory} belonging to the project with the given {@link ProjectId}.
	 *
	 * @param projectId
	 *            a project ID
	 * @return the found project history or <code>null</code> if none has been found
	 */
	private ProjectHistory findHistory(ProjectId projectId) {
		final EList<ProjectHistory> serverProjects = getServerSpace().getProjects();

		for (final ProjectHistory history : serverProjects) {
			if (history.getProjectId().equals(projectId)) {
				return history;
			}
		}

		return null;
	}

	/**
	 * Finds the property with the given name within the given {@link ProjectHistory}.
	 *
	 * @param projectHistory
	 *            the project history that should be looked up
	 * @param propertyName
	 *            the name of the property to be found
	 * @return the actual property or <code>null</code> if no such property has been found
	 */
	private EMFStoreProperty findProperty(ProjectHistory projectHistory, String propertyName) {
		final Map<String, EMFStoreProperty> propertiesMap = initCacheForHistory(projectHistory);
		return propertiesMap.get(propertyName);
	}

	/**
	 * Initializes a cache entry for the given {@link ProjectHistory}.
	 *
	 * @param projectHistory
	 *            the history information for which a property-related cache entry should be created
	 * @return the updated cache map containing the new cache entry
	 */
	private Map<String, EMFStoreProperty> initCacheForHistory(ProjectHistory projectHistory) {

		Map<String, EMFStoreProperty> propertiesMap = cache.get(projectHistory);

		if (propertiesMap == null) {
			propertiesMap = new LinkedHashMap<String, EMFStoreProperty>();
			for (final EMFStoreProperty prop : projectHistory.getSharedProperties()) {
				propertiesMap.put(prop.getKey(), prop);
			}
		}

		return propertiesMap;
	}

	/**
	 * Updates the cache by adding the given {@link EMFStoreProperty} to the shared
	 * properties of the given {@link ProjectHistory}.
	 *
	 * @param history
	 *            the history
	 * @param property
	 *            the property to be added to the history
	 */
	private void updateCache(ProjectHistory history, EMFStoreProperty property) {
		final Map<String, EMFStoreProperty> properties = initCacheForHistory(history);
		properties.put(property.getKey(), property);
	}
}
