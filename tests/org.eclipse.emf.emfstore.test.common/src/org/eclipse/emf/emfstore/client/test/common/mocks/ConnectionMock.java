/*******************************************************************************
 * Copyright (c) 2011-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.test.common.mocks;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.emfstore.internal.client.model.ServerInfo;
import org.eclipse.emf.emfstore.internal.client.model.connectionmanager.ConnectionManager;
import org.eclipse.emf.emfstore.internal.common.APIUtil;
import org.eclipse.emf.emfstore.internal.common.model.EMFStoreProperty;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.EMFStore;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.AccessControl;
import org.eclipse.emf.emfstore.internal.server.connection.xmlrpc.util.ShareProjectAdapter;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.exceptions.InvalidVersionSpecException;
import org.eclipse.emf.emfstore.internal.server.filetransfer.FileChunk;
import org.eclipse.emf.emfstore.internal.server.filetransfer.FileTransferInformation;
import org.eclipse.emf.emfstore.internal.server.model.AuthenticationInformation;
import org.eclipse.emf.emfstore.internal.server.model.ClientVersionInfo;
import org.eclipse.emf.emfstore.internal.server.model.FileIdentifier;
import org.eclipse.emf.emfstore.internal.server.model.ModelFactory;
import org.eclipse.emf.emfstore.internal.server.model.ProjectHistory;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.model.ProjectInfo;
import org.eclipse.emf.emfstore.internal.server.model.SessionId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.OrgUnitProperty;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESAuthenticationInformationImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.BranchInfo;
import org.eclipse.emf.emfstore.internal.server.model.versioning.BranchVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageEnvelope;
import org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo;
import org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryQuery;
import org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.TagVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersionSpec;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.model.ESAuthenticationInformation;
import org.eclipse.emf.emfstore.server.model.ESSessionId;

public class ConnectionMock implements ConnectionManager {

	private final EMFStore emfStore;
	// TODO: auth mock is never used locally
	private final HashSet<SessionId> sessions;
	private final AccessControl accessControl;
	private boolean deleteFiles;

	public ConnectionMock(EMFStore emfStore, AccessControl accessControl) {
		this.emfStore = emfStore;
		this.accessControl = accessControl;
		sessions = new LinkedHashSet<SessionId>();
	}

	/**
	 * Returns the {@link AccessControl}.
	 *
	 * @return the access control
	 */
	public AccessControl getAccessControl() {
		return accessControl;
	}

	public AuthenticationInformation logIn(final String username, final String password, final ServerInfo severInfo,
		final ClientVersionInfo clientVersionInfo) throws ESException {
		final ESAuthenticationInformation logIn = accessControl.getLoginService()
			.logIn(username, password, ModelUtil.clone(clientVersionInfo).toAPI());
		final AuthenticationInformation authInfo = ESAuthenticationInformationImpl.class.cast(logIn).toInternalAPI();
		sessions.add(authInfo.getSessionId());
		return authInfo;
	}

	public void logout(final SessionId sessionId) throws ESException {
		accessControl.getLoginService().logout(ModelUtil.clone(sessionId).toAPI());
		sessions.remove(sessionId);
	}

	public boolean isLoggedIn(final SessionId sessionId) {
		return sessions.contains(ModelUtil.clone(sessionId));
	}

	public List<ProjectInfo> getProjectList(final SessionId sessionId) throws ESException {
		final SessionId clonedSessionId = checkSessionId(sessionId);
		return ModelUtil.clone(emfStore.getProjectList(clonedSessionId));
	}

	public Project getProject(final SessionId sessionId, final ProjectId projectId, final VersionSpec versionSpec)
		throws ESException {
		final SessionId clonedSessionId = checkSessionId(sessionId);
		return ModelUtil.clone(
			emfStore.getProject(clonedSessionId,
				ModelUtil.clone(projectId),
				ModelUtil.clone(versionSpec)));
	}

	public PrimaryVersionSpec createVersion(final SessionId sessionId, final ProjectId projectId,
		final PrimaryVersionSpec baseVersionSpec, final AbstractChangePackage changePackage,
		final BranchVersionSpec targetBranch,
		final PrimaryVersionSpec sourceVersion, final LogMessage logMessage)
		throws ESException, InvalidVersionSpecException {

		AbstractChangePackage cp = changePackage;
		final SessionId clonedSessionId = checkSessionId(sessionId);

		if (FileBasedChangePackage.class.isInstance(changePackage)) {
			cp = FileBasedChangePackage.class.cast(changePackage).toInMemoryChangePackage();
		}

		return ModelUtil.clone(emfStore.createVersion(clonedSessionId, ModelUtil.clone(projectId),
			ModelUtil.clone(baseVersionSpec), ModelUtil.clone(cp), ModelUtil.clone(targetBranch),
			ModelUtil.clone(sourceVersion), ModelUtil.clone(logMessage)));
	}

	public PrimaryVersionSpec resolveVersionSpec(final SessionId sessionId, final ProjectId projectId,
		final VersionSpec versionSpec)
		throws ESException {

		final SessionId clonedSessionId = checkSessionId(sessionId);
		return ModelUtil.clone(
			emfStore.resolveVersionSpec(clonedSessionId,
				ModelUtil.clone(projectId),
				ModelUtil.clone(versionSpec)));
	}

	public List<AbstractChangePackage> getChanges(final SessionId sessionId, final ProjectId projectId,
		final VersionSpec source,
		VersionSpec target) throws ESException {
		final SessionId clonedSessionId = checkSessionId(sessionId);
		final List<AbstractChangePackage> changes = emfStore.getChanges(
			clonedSessionId,
			ModelUtil.clone(projectId),
			ModelUtil.clone(source),
			ModelUtil.clone(target));
		return ModelUtil.clone(changes);
	}

	public List<BranchInfo> getBranches(final SessionId sessionId, final ProjectId projectId) throws ESException {
		final SessionId clonedSessionId = checkSessionId(sessionId);
		return ModelUtil.clone(emfStore.getBranches(clonedSessionId, ModelUtil.clone(projectId)));
	}

	public List<HistoryInfo> getHistoryInfo(final SessionId sessionId, final ProjectId projectId,
		final HistoryQuery<?> historyQuery)
		throws ESException {
		final SessionId clonedSessionId = checkSessionId(sessionId);
		return ModelUtil.clone(
			emfStore.getHistoryInfo(
				clonedSessionId,
				ModelUtil.clone(projectId),
				ModelUtil.clone(historyQuery)));
	}

	public void addTag(final SessionId sessionId, final ProjectId projectId, final PrimaryVersionSpec versionSpec,
		final TagVersionSpec tag)
		throws ESException {
		final SessionId clonedSessionId = checkSessionId(sessionId);
		emfStore.addTag(
			clonedSessionId,
			ModelUtil.clone(projectId),
			ModelUtil.clone(versionSpec),
			ModelUtil.clone(tag));
	}

	public void removeTag(final SessionId sessionId, final ProjectId projectId, final PrimaryVersionSpec versionSpec,
		final TagVersionSpec tag)
		throws ESException {
		final SessionId clonedSessionId = checkSessionId(sessionId);
		emfStore.removeTag(
			clonedSessionId,
			ModelUtil.clone(projectId),
			ModelUtil.clone(versionSpec),
			ModelUtil.clone(tag));
	}

	public ProjectInfo createEmptyProject(final SessionId sessionId, final String name, final String description,
		final LogMessage logMessage)
		throws ESException {
		final SessionId clonedSessionId = checkSessionId(sessionId);
		final ProjectInfo projectInfo = emfStore.createEmptyProject(clonedSessionId, name, description,
			ModelUtil.clone(logMessage));
		final ESSessionId resolvedSession = accessControl.getSessions().resolveSessionById(clonedSessionId.getId());
		final SessionId session = APIUtil.toInternal(SessionId.class, resolvedSession);
		ShareProjectAdapter.attachTo(session, projectInfo.getProjectId());
		return projectInfo;
	}

	public ProjectInfo createProject(final SessionId sessionId, final String name, final String description,
		final LogMessage logMessage,
		final Project project) throws ESException {

		final SessionId clonedSessionId = ModelUtil.clone(sessionId);
		checkSessionId(clonedSessionId);
		final ProjectInfo projectInfo = emfStore.createProject(clonedSessionId, name, description,
			ModelUtil.clone(logMessage),
			ModelUtil.clone(project));
		final ESSessionId resolvedSession = accessControl.getSessions().resolveSessionById(clonedSessionId.getId());
		final SessionId session = APIUtil.toInternal(SessionId.class, resolvedSession);
		ShareProjectAdapter.attachTo(session, projectInfo.getProjectId());
		return projectInfo;
	}

	public void deleteProject(final SessionId sessionId, final ProjectId projectId, final boolean deleteFiles)
		throws ESException {
		final SessionId clonedSessionId = checkSessionId(sessionId);
		this.deleteFiles = deleteFiles;
		emfStore.deleteProject(clonedSessionId, ModelUtil.clone(projectId), deleteFiles);
	}

	public boolean didDeleteFiles() {
		return deleteFiles;
	}

	public ACUser resolveUser(final SessionId sessionId, final ACOrgUnitId id) throws ESException {
		final SessionId clonedSessionId = checkSessionId(sessionId);
		return ModelUtil.clone(emfStore.resolveUser(clonedSessionId, ModelUtil.clone(id)));
	}

	public ProjectId importProjectHistoryToServer(final SessionId sessionId, final ProjectHistory projectHistory)
		throws ESException {
		final SessionId clonedSessionId = checkSessionId(sessionId);
		return ModelUtil.clone(
			emfStore.importProjectHistoryToServer(
				clonedSessionId,
				ModelUtil.clone(projectHistory)));
	}

	public ProjectHistory exportProjectHistoryFromServer(final SessionId sessionId, final ProjectId projectId)
		throws ESException {
		final SessionId clonedSessionId = checkSessionId(sessionId);
		return ModelUtil.clone(
			emfStore.exportProjectHistoryFromServer(clonedSessionId,
				ModelUtil.clone(projectId)));
	}

	public FileTransferInformation uploadFileChunk(final SessionId sessionId, final ProjectId projectId,
		final FileChunk fileChunk)
		throws ESException {
		final SessionId clonedSessionId = checkSessionId(sessionId);
		return emfStore.uploadFileChunk(clonedSessionId, ModelUtil.clone(projectId), fileChunk);
	}

	public FileChunk downloadFileChunk(final SessionId sessionId, final ProjectId projectId,
		final FileTransferInformation fileInformation) throws ESException {
		final SessionId clonedSessionId = checkSessionId(sessionId);
		return emfStore.downloadFileChunk(clonedSessionId, ModelUtil.clone(projectId), fileInformation);
	}

	public void transmitProperty(final SessionId sessionId, final OrgUnitProperty changedProperty, final ACUser user,
		final ProjectId projectId)
		throws ESException {
		final SessionId clonedSessionId = checkSessionId(sessionId);
		emfStore.transmitProperty(
			clonedSessionId,
			ModelUtil.clone(changedProperty),
			ModelUtil.clone(user),
			ModelUtil.clone(projectId));
	}

	public List<EMFStoreProperty> setEMFProperties(final SessionId sessionId, final List<EMFStoreProperty> property,
		final ProjectId projectId) throws ESException {

		final SessionId clonedSessionId = checkSessionId(sessionId);
		return ModelUtil.clone(
			emfStore.setEMFProperties(
				clonedSessionId,
				ModelUtil.clone(property),
				ModelUtil.clone(projectId)));
	}

	public List<EMFStoreProperty> getEMFProperties(final SessionId sessionId, final ProjectId projectId)
		throws ESException {
		final SessionId clonedSessionId = checkSessionId(sessionId);
		return ModelUtil.clone(emfStore.getEMFProperties(clonedSessionId, ModelUtil.clone(projectId)));
	}

	public void registerEPackage(final SessionId sessionId, final EPackage pkg) throws ESException {
		final SessionId clonedSessionId = checkSessionId(sessionId);
		emfStore.registerEPackage(clonedSessionId, ModelUtil.clone(pkg));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.EMFStore#getVersion(org.eclipse.emf.emfstore.internal.server.model.SessionId)
	 */
	public String getVersion(final SessionId sessionId) throws ESException {
		final SessionId clonedSessionId = checkSessionId(sessionId);
		return emfStore.getVersion(clonedSessionId);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.model.connectionmanager.ConnectionManager#getVersion(org.eclipse.emf.emfstore.internal.client.model.ServerInfo)
	 */
	public String getVersion(final ServerInfo serverInfo) throws ESException {
		final SessionId sessionId = ModelFactory.eINSTANCE.createSessionId();
		sessionId.setId(serverInfo.getUrl().toString() + "/defaultSession"); //$NON-NLS-1$
		sessions.add(sessionId);
		return emfStore.getVersion(sessionId);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.EMFStore#uploadChangePackageFragment(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.emfstore.internal.server.model.ProjectId,
	 *      org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageEnvelope)
	 */
	public String uploadChangePackageFragment(final SessionId sessionId, final ProjectId projectId,
		final ChangePackageEnvelope envelope)
		throws ESException {

		final SessionId clonedSessionId = checkSessionId(sessionId);

		return emfStore.uploadChangePackageFragment(
			clonedSessionId,
			ModelUtil.clone(projectId),
			ModelUtil.clone(envelope));
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.EMFStore#downloadChangePackageFragment(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.emfstore.internal.server.model.ProjectId, java.lang.String, int)
	 */
	public ChangePackageEnvelope downloadChangePackageFragment(final SessionId sessionId, ProjectId projectId,
		final String proxyId, final int fragmentIndex)
		throws ESException {
		final SessionId clonedSessionId = checkSessionId(sessionId);
		return ModelUtil.clone(
			emfStore.downloadChangePackageFragment(
				clonedSessionId,
				projectId,
				proxyId,
				fragmentIndex));
	}

	private SessionId checkSessionId(SessionId sessionId) throws ESException {
		final SessionId clonedSessionId = ModelUtil.clone(sessionId);
		if (!isLoggedIn(clonedSessionId)) {
			throw new AccessControlException();
		}
		return clonedSessionId;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.server.EMFStore#deleteFile(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.emfstore.internal.server.model.ProjectId,
	 *      org.eclipse.emf.emfstore.internal.server.model.FileIdentifier)
	 */
	public void deleteFile(SessionId sessionId, ProjectId projectId, FileIdentifier fileIdentifier) throws ESException {
		final SessionId clonedSessionId = checkSessionId(sessionId);
		emfStore.deleteFile(clonedSessionId, projectId, fileIdentifier);
	}
}