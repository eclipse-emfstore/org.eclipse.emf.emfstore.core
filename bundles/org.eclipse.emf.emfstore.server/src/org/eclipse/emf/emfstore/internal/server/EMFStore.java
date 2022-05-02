/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * MaximilianKoegel
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.emfstore.internal.common.model.EMFStoreProperty;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.server.exceptions.InvalidVersionSpecException;
import org.eclipse.emf.emfstore.internal.server.filetransfer.FileChunk;
import org.eclipse.emf.emfstore.internal.server.filetransfer.FileTransferInformation;
import org.eclipse.emf.emfstore.internal.server.model.FileIdentifier;
import org.eclipse.emf.emfstore.internal.server.model.ProjectHistory;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.model.ProjectInfo;
import org.eclipse.emf.emfstore.internal.server.model.SessionId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.OrgUnitProperty;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.BranchInfo;
import org.eclipse.emf.emfstore.internal.server.model.versioning.BranchVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageEnvelope;
import org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo;
import org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryQuery;
import org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.TagVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersionSpec;
import org.eclipse.emf.emfstore.server.exceptions.ESException;

/**
 * An EMF store is responsible for storing projects, change management on
 * projects and for persisting projects.
 *
 * @author Maximilian Koegel
 * @generated NOT
 */
public interface EMFStore extends EMFStoreInterface {

	/**
	 * Virtual URI for change package de-/serialization.
	 */
	URI CHANGEPACKAGE_URI = URI.createURI("emfstoreVirtualChangePackageUri"); //$NON-NLS-1$

	/**
	 * Virtual URI for project de-/serialization.
	 */
	URI PROJECT_URI = URI.createURI("emfstoreVirtualProjectUri"); //$NON-NLS-1$

	/**
	 * Get a list of projects the user of the session id can access. The server
	 * should is determined by the session id.
	 *
	 * @param sessionId
	 *            the session id for authentication
	 * @return a list of project infos for the projects the user can access
	 * @throws ESException
	 *             if any error in the EmfStore occurs
	 * @generated NOT
	 */
	List<ProjectInfo> getProjectList(SessionId sessionId) throws ESException;

	/**
	 * Gets a project in a certain revision from the server. Depending on your
	 * persistence properties, this method can become expensive because it has
	 * to recalculate the requested project state.
	 *
	 * @see ServerConfiguration#PROJECTSTATE_VERSION_PERSISTENCE
	 * @param sessionId
	 *            the session id for authentication
	 * @param projectId
	 *            the project id of the project to get
	 * @param versionSpec
	 *            the version to get
	 * @return a project in the specified revision
	 * @throws ESException
	 *             if any error in the EmfStore occurs
	 * @generated NOT
	 */
	Project getProject(SessionId sessionId, ProjectId projectId, VersionSpec versionSpec) throws ESException;

	/**
	 * Create a new version on the server of the given project.
	 *
	 * @param sessionId
	 *            the session id for authentication
	 * @param projectId
	 *            the project id
	 * @param baseVersionSpec
	 *            the version the project was last synched with the server
	 * @param changePackage
	 *            the changes performed on the project since last synch
	 * @param targetBranch
	 *            this should be set if a new branch shout be created with this
	 *            commit. Otherwise set null
	 * @param sourceVersion
	 *            if a branch was merged and the resulting merged changed are
	 *            committed this should be set to the incoming branch inorder to
	 *            set correct links in the version model. Can be null otherwise
	 * @param logMessage
	 *            the log message for the new version
	 * @return the version specifier of the version created on the server
	 * @throws InvalidVersionSpecException
	 *             if the base version is not equal to the current head
	 *             revision.
	 * @throws ESException
	 *             if any error in the EmfStore occurs
	 * @generated NOT
	 */
	PrimaryVersionSpec createVersion(SessionId sessionId, ProjectId projectId, PrimaryVersionSpec baseVersionSpec,
		AbstractChangePackage changePackage, BranchVersionSpec targetBranch, PrimaryVersionSpec sourceVersion,
		LogMessage logMessage) throws ESException, InvalidVersionSpecException;

	/**
	 * Submits a single {@link ChangePackageEnvelope} containing a change package fragment that will be aggregated to a
	 * complete {@link org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage ChangePackage} once
	 * all fragments are available.
	 *
	 * @param sessionId
	 *            the {@link SessionId} for authentication
	 * @param projectId
	 *            the {@link ProjectId}
	 * @param envelope
	 *            a{@link ChangePackageEnvelope} containing the change package fragment
	 *
	 * @return an ID that is used to identify the set of submitted {@link ChangePackageEnvelope}s
	 *
	 * @throws ESException in case the fragment could not be created
	 */
	String uploadChangePackageFragment(SessionId sessionId, ProjectId projectId, ChangePackageEnvelope envelope)
		throws ESException;

	/**
	 * Retrieves a change package fragment.
	 *
	 * @param sessionId
	 *            the {@link SessionId} for authentication purposes
	 * @param projectId
	 *            the {@link ProjectId} for access control purposes
	 * @param proxyId
	 *            the ID of the change package proxy that is used to identify available fragments
	 * @param fragmentIndex
	 *            the index of fragment
	 * @return a {@link ChangePackageEnvelope} containing the requested change package fragment
	 * @throws ESException in case the fragment could not be fetched
	 */
	ChangePackageEnvelope downloadChangePackageFragment(SessionId sessionId, ProjectId projectId, String proxyId,
		int fragmentIndex)
		throws ESException;

	/**
	 * Resolve a version specified to a primary version specifier.
	 *
	 * @param sessionId
	 *            the session id for authentication
	 * @param versionSpec
	 *            the version specifier to resolve
	 * @param projectId
	 *            the project id
	 * @return a primary version specifier identifing the same version
	 * @throws ESException
	 *             if any error in the EmfStore occurs
	 * @generated NOT
	 */
	PrimaryVersionSpec resolveVersionSpec(SessionId sessionId, ProjectId projectId, VersionSpec versionSpec)
		throws ESException;

	/**
	 * Get changes from the server.
	 *
	 * @param sessionId
	 *            the session id for authentication
	 * @param projectId
	 *            the project id
	 * @param source
	 *            the source version specifier
	 * @param target
	 *            the target version specifier
	 * @return a list of change packages from source to target representing the
	 *         changes that happened between the two versions.
	 * @throws ESException
	 *             if any error in the EmfStore occurs
	 * @generated NOT
	 */
	List<AbstractChangePackage> getChanges(SessionId sessionId, ProjectId projectId, VersionSpec source,
		VersionSpec target)
		throws ESException;

	/**
	 * Lista all branches of the given project.
	 *
	 * @param sessionId
	 *            the session id for authentication
	 * @param projectId
	 *            the project id
	 * @return list of {@link BranchInfo}
	 * @throws ESException
	 *             in case of an error
	 */
	List<BranchInfo> getBranches(SessionId sessionId, ProjectId projectId) throws ESException;

	/**
	 * Get history information from the server. The list returned will describe
	 * the versions as request through {@link HistoryQuery}.
	 *
	 * @param sessionId
	 *            the session id
	 * @param projectId
	 *            the project id
	 * @param historyQuery
	 *            the historyQuery
	 * @return list of history information
	 * @throws ESException
	 *             if any error in the EmfStore occurs
	 * @generated NOT
	 */
	List<HistoryInfo> getHistoryInfo(SessionId sessionId, ProjectId projectId, HistoryQuery<?> historyQuery)
		throws ESException;

	/**
	 * Adds a tag to a version of the specified project.
	 *
	 * @param sessionId
	 *            the session id
	 * @param projectId
	 *            the project id
	 * @param versionSpec
	 *            the version versionSpec
	 * @param tag
	 *            the tag
	 * @throws ESException
	 *             if any error in the EmfStore occurs
	 */
	void addTag(SessionId sessionId, ProjectId projectId, PrimaryVersionSpec versionSpec, TagVersionSpec tag)
		throws ESException;

	/**
	 * Removes a tag to a version of the specified project.
	 *
	 * @param sessionId
	 *            the session id
	 * @param projectId
	 *            the project id
	 * @param versionSpec
	 *            the version versionSpec
	 * @param tag
	 *            the tag to be removed
	 * @throws ESException
	 *             if any error in the EmfStore occurs
	 */
	void removeTag(SessionId sessionId, ProjectId projectId, PrimaryVersionSpec versionSpec, TagVersionSpec tag)
		throws ESException;

	/**
	 * Create a new project on the server.
	 *
	 * @param sessionId
	 *            the session id for authentication
	 * @param name
	 *            the name of the server
	 * @param description
	 *            the description
	 * @param logMessage
	 *            the logMessage
	 * @return a {@link ProjectInfo} for the new project
	 * @throws ESException
	 *             if any error in the EmfStore occurs
	 * @generated NOT
	 */
	ProjectInfo createEmptyProject(SessionId sessionId, String name, String description, LogMessage logMessage)
		throws ESException;

	/**
	 * Create a new project on the server. This createProject method allows to
	 * create a project on the server with initial projectstate (share project).
	 *
	 * @param sessionId
	 *            the session id for authentication
	 * @param name
	 *            the name of the server
	 * @param description
	 *            the description
	 * @param logMessage
	 *            the logMessage
	 * @param project
	 *            the initial project state
	 * @return a {@link ProjectInfo} for the new project
	 * @throws ESException
	 *             if any error in the EmfStore occurs
	 * @generated NOT
	 */
	ProjectInfo createProject(SessionId sessionId, String name, String description, LogMessage logMessage,
		Project project) throws ESException;

	/**
	 * Deletes a project on the server. It's possible to delete the project from
	 * the containment tree and if wanted to deleted the related files too.
	 *
	 * @param sessionId
	 *            the session id
	 * @param projectId
	 *            the project id
	 * @param deleteFiles
	 *            if true, the project files will be deleted too
	 * @throws ESException
	 *             in case of failure
	 */
	void deleteProject(SessionId sessionId, ProjectId projectId, boolean deleteFiles) throws ESException;

	/**
	 * Resolves a user by id and returns an ACUser with all roles on the server.
	 * Also roles from groups are aggregated and added to the user. To resolve
	 * other user than the requesting user himself, the user has to have admin
	 * access rights. If id is null, the requesting user will be resolved.
	 *
	 * @param sessionId
	 *            session id
	 * @param id
	 *            user id, can be null, then requesting user gets resolved
	 * @return ACuser with all roles on the server
	 * @throws ESException
	 *             if any error in the EmfStore occurs
	 */
	ACUser resolveUser(SessionId sessionId, ACOrgUnitId id) throws ESException;

	/**
	 * Imports a project history to the server. The project history elements
	 * such as version, projecstate etc will be devided in several files on the
	 * server file system. The server will try to use the specified project id,
	 * if it already exists a new id is generated.
	 *
	 * @param sessionId
	 *            sessionid
	 * @param projectHistory
	 *            project history
	 * @return projectId
	 * @throws ESException
	 *             in case of failure
	 */
	ProjectId importProjectHistoryToServer(SessionId sessionId, ProjectHistory projectHistory) throws ESException;

	/**
	 * Exports a given project history from the server. Caution if you try to
	 * export big projects from the server.
	 *
	 * @param sessionId
	 *            session id
	 * @param projectId
	 *            project id
	 * @return a projecthistory
	 * @throws ESException
	 *             in case of failure
	 */
	ProjectHistory exportProjectHistoryFromServer(SessionId sessionId, ProjectId projectId) throws ESException;

	/**
	 * Uploads a file chunk to the server.
	 *
	 * @param sessionId
	 *            session id
	 * @param projectId
	 *            project id
	 * @param fileChunk
	 *            file chunk
	 * @return FileVersion denoting the current file version to be written to
	 * @throws ESException
	 *             if any error occurs in the EmfStore
	 */
	FileTransferInformation uploadFileChunk(SessionId sessionId, ProjectId projectId, FileChunk fileChunk)
		throws ESException;

	/**
	 * Downloads a file chunk from the server.
	 *
	 * @param sessionId
	 *            session id
	 * @param projectId
	 *            project id
	 * @param fileInformation
	 *            file information
	 * @return FileChunk
	 * @throws ESException
	 *             if any error occurs in the EmfStore
	 */
	FileChunk downloadFileChunk(SessionId sessionId, ProjectId projectId, FileTransferInformation fileInformation)
		throws ESException;

	/**
	 * Delete a file from the server
	 *
	 * @param sessionId the ID of the current session
	 * @param projectId the ID of the project associated with the file
	 * @param fileIdentifier the ID of the file to be deleted
	 * @throws ESException in case any error occurs while deleting the file
	 */
	void deleteFile(SessionId sessionId, ProjectId projectId, FileIdentifier fileIdentifier)
		throws ESException;

	/**
	 * @param sessionId
	 *            session id
	 * @param changedProperty
	 *            the property that has been changed client-side
	 * @param tmpUser
	 *            the respective user
	 * @param projectId
	 *            the project id
	 * @throws ESException
	 *             if any error occurs in the EmfStore
	 */
	void transmitProperty(SessionId sessionId, OrgUnitProperty changedProperty, ACUser tmpUser, ProjectId projectId)
		throws ESException;

	/**
	 * Store EMFProperties on the server.
	 *
	 * @param sessionId
	 *            sessionId
	 * @param property
	 *            list properties which shall be shared on the server.
	 * @param projectId
	 *            the project id
	 * @return a list of properties which have not been set on the server due
	 *         there were more recent versions of these properties on the server
	 * @throws ESException
	 *             if any error occurs in the EmfStore
	 */
	List<EMFStoreProperty> setEMFProperties(SessionId sessionId, List<EMFStoreProperty> property, ProjectId projectId)
		throws ESException;

	/**
	 * Get stored EMFStoreProperties from the server.
	 *
	 * @param sessionId
	 *            sessionId
	 * @param projectId
	 *            the projct id
	 *
	 * @return list of EMFStoreProperties
	 *
	 * @throws ESException
	 *             if any error occurs in the EmfStore
	 *
	 **/
	List<EMFStoreProperty> getEMFProperties(SessionId sessionId, ProjectId projectId) throws ESException;

	/**
	 * Register a new EPackage.
	 *
	 * @param sessionId
	 *            session id
	 * @param pkg
	 *            the EPackage to be registered
	 *
	 * @throws ESException
	 *             if any error occurs in the EmfStore
	 */
	void registerEPackage(SessionId sessionId, EPackage pkg) throws ESException;

	/**
	 * Returns the version of the EMFStore server.
	 *
	 * @param sessionId
	 *            a session ID
	 *
	 * @return the version of the EMFStore server
	 *
	 * @throws ESException
	 *             in case of an error
	 */
	String getVersion(SessionId sessionId) throws ESException;
}
