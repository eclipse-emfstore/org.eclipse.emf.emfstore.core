/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * wesendon
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.core.helper;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.eclipse.emf.emfstore.internal.server.exceptions.FatalESException;
import org.eclipse.emf.emfstore.internal.server.exceptions.StorageException;
import org.eclipse.emf.emfstore.internal.server.model.ProjectHistory;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.model.ServerSpace;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.Version;
import org.eclipse.emf.emfstore.internal.server.storage.XMIServerURIConverter;
import org.eclipse.emf.emfstore.server.ESServerURIUtil;

/**
 * Helper for creating resources etc.
 *
 * @author wesendon
 */
// TODO: internal
public class ResourceHelper {

	private final ServerSpace serverSpace;

	/**
	 * Default constructor.
	 *
	 * @param serverSpace
	 *            serverspace
	 * @throws FatalESException
	 *             in case of failure
	 */
	public ResourceHelper(ServerSpace serverSpace) throws FatalESException {
		this.serverSpace = serverSpace;
	}

	/**
	 * Creates a resource for project history.
	 *
	 * @param projectHistory
	 *            project history
	 * @throws FatalESException
	 *             if saving fails
	 */
	public void createResourceForProjectHistory(ProjectHistory projectHistory) throws FatalESException {
		final URI projectHistoryURI = ESServerURIUtil.createProjectHistoryURI(projectHistory.getProjectId());
		saveInResource(projectHistory, projectHistoryURI);
	}

	/**
	 * Creates a resource for a new version.
	 *
	 * @param version
	 *            version
	 * @param projectId
	 *            project id
	 * @throws FatalESException
	 *             if saving fails
	 */
	public void createResourceForVersion(Version version, ProjectId projectId) throws FatalESException {
		final URI versionURI = ESServerURIUtil.createVersionURI(projectId, version.getPrimarySpec());
		saveInResource(version, versionURI);
	}

	/**
	 * Creates a resource for a new project.
	 *
	 * @param project
	 *            project
	 * @param projectId
	 *            projectid
	 * @param versionId
	 *            versionid
	 * @throws FatalESException
	 *             if saving fails
	 */
	public void createResourceForProject(Project project, PrimaryVersionSpec versionId, ProjectId projectId)
		throws FatalESException {
		final URI projectStateURI = ESServerURIUtil.createProjectStateURI(projectId, versionId);
		saveInResourceWithProject(project, projectStateURI, project);
	}

	/**
	 * Creates a resource for a changepackage.
	 *
	 * @param changePackage
	 *            changepackage
	 * @param versionId
	 *            versionId
	 * @param projectId
	 *            projectId
	 * @throws FatalESException
	 *             if saving fails
	 */
	public void createResourceForChangePackage(AbstractChangePackage changePackage, PrimaryVersionSpec versionId,
		ProjectId projectId) throws FatalESException {
		final URI changePackageURI = ESServerURIUtil.createChangePackageURI(projectId, versionId);
		saveInResource(changePackage, changePackageURI);
	}

	/**
	 * Deletes a project state. The {@link Resource} the project is contained in
	 * will be unloaded as well as deleted.
	 *
	 * @param version
	 *            the version to be deleted
	 * @param projectId
	 *            the ID of the project to be deleted
	 */
	public void deleteProjectState(Version version, ProjectId projectId) {
		try {
			/*
			 * the project state of the version may not have been loaded yet. calling the getter however will load it,
			 * just to delete if afterwards. it is more performant to create a new unloaded resource for the purpose of
			 * deletion.
			 */
			final URI projectStateURI = ESServerURIUtil.createProjectStateURI(version.eResource().getURI());
			final ResourceSet resourceSet = ModelUtil.createResourceSetForURI(projectStateURI);
			final Resource deleteResource = resourceSet.createResource(projectStateURI);
			deleteResource.delete(null);
			version.setProjectStateResource(null);
		} catch (final IOException e) {
			ModelUtil.logWarning("Could not delete project state with id " + projectId.getId() + " and version "
				+ version.getPrimarySpec().getIdentifier() + ".", e);
		}
	}

	/**
	 * Gets and checks a number from a given server property. This number -
	 * referred as x - describes the size of an interval between project states.
	 * It's needed to determine whether a project state should be saved or be
	 * backuped.
	 *
	 * @see ServerConfiguration#PROJECTSTATE_VERSION_PERSISTENCE_EVERYXVERSIONS_X
	 * @param policy
	 *            policy name from server configuration
	 * @param defaultPolicy
	 *            default policy name from server configuration
	 * @param allowZero
	 *            allow zero for x
	 * @return x
	 */
	public int getXFromPolicy(String policy, String defaultPolicy, boolean allowZero) {
		int x;
		try {
			x = Integer.parseInt(ServerConfiguration.getProperties().getProperty(policy, defaultPolicy));
		} catch (final NumberFormatException e) {
			x = 1;
			ModelUtil.logWarning("Couldn't read property: " + policy + " , x set to 1", e);
		}
		if (x < 0) {
			x = 1;
			ModelUtil.logWarning("Policy " + policy + " with x < 0 not possible, x set to 1.");
		}
		if (!allowZero && x == 0) {
			x = 1;
			ModelUtil.logWarning("Policy " + policy + " with x = 0 not possible, x set to 1.");
		}
		return x;
	}

	/**
	 * Returns the file path to a given project.
	 *
	 * @param projectId
	 *            the project id
	 * @return file path
	 */
	public String getProjectFolder(ProjectId projectId) {
		return ServerConfiguration.getServerHome() + XMIServerURIConverter.FILE_PREFIX_PROJECTFOLDER
			+ projectId.getId()
			+ File.separatorChar;
	}

	private void saveInResource(EObject obj, URI resourceURI) throws FatalESException {
		final Resource resource = serverSpace.eResource().getResourceSet().createResource(resourceURI);
		resource.getContents().add(obj);
		save(obj);
	}

	private void saveInResourceWithProject(EObject obj, URI resourceURI, Project project) throws FatalESException {
		final Resource resource = serverSpace.eResource().getResourceSet().createResource(resourceURI);
		resource.getContents().add(obj);

		if (resource instanceof XMIResource) {
			final XMIResource xmiResource = (XMIResource) resource;
			for (final EObject modelElement : project.getAllModelElements()) {
				final ModelElementId modelElementId = project.getModelElementId(modelElement);
				xmiResource.setID(modelElement, modelElementId.getId());
			}
		}

		save(obj);
	}

	/**
	 * Saves the given EObject and sets the IDs on the eObject's resource for
	 * all model elements contained in the given project.
	 *
	 * @param eObject
	 *            the EObject to be saved
	 * @param project
	 *            the project, that is used to set the IDs of all model elements
	 *            within the project on the resource
	 * @throws FatalESException
	 *             in case of failure
	 */
	public void saveWithProject(EObject eObject, Project project) throws FatalESException {
		final Resource resource = eObject.eResource();

		if (resource instanceof XMIResource) {
			final XMIResource xmiResource = (XMIResource) resource;
			for (final EObject modelElement : project.getAllModelElements()) {
				final ModelElementId modelElementId = project.getModelElementId(modelElement);
				xmiResource.setID(modelElement, modelElementId.getId());
			}
		}

		save(eObject);
	}

	/**
	 * Saves an eObject.
	 *
	 * @param object
	 *            the object
	 * @throws FatalESException
	 *             in case of failure
	 */
	public void save(EObject object) throws FatalESException {
		try {
			ModelUtil.saveResource(object.eResource(), ModelUtil.getResourceLogger());
			// BEGIN SUPRESS CATCH EXCEPTION
		} catch (final Exception e) {
			throw new FatalESException(StorageException.NOSAVE, e);
		}
		// END SUPRESS CATCH EXCEPTION
	}

	/**
	 * Saves all modified resources in the serverspace's resource set.
	 *
	 * @throws FatalESException
	 *             in case of failure
	 */
	public void saveAll() throws FatalESException {
		for (final Resource res : serverSpace.eResource().getResourceSet().getResources()) {
			if (res.isLoaded() && res.isModified()) {
				try {
					ModelUtil.saveResource(res, ModelUtil.getResourceLogger());
					// BEGIN SUPRESS CATCH EXCEPTION
				} catch (final Exception e) {
					throw new FatalESException(StorageException.NOSAVE, e);
				}
				// END SUPRESS CATCH EXCEPTION
			}
		}

	}
}
