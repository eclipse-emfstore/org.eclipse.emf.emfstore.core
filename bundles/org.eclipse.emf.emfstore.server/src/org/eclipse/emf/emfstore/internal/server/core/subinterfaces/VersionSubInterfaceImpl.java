/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto von Wesendonk - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.core.subinterfaces;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.emfstore.internal.common.APIUtil;
import org.eclipse.emf.emfstore.internal.common.ESCollections;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.common.model.impl.ProjectImpl;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.SerializationException;
import org.eclipse.emf.emfstore.internal.server.EMFStoreController;
import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.eclipse.emf.emfstore.internal.server.core.AbstractEmfstoreInterface;
import org.eclipse.emf.emfstore.internal.server.core.AbstractSubEmfstoreInterface;
import org.eclipse.emf.emfstore.internal.server.core.helper.ResourceHelper;
import org.eclipse.emf.emfstore.internal.server.exceptions.BranchInfoMissingException;
import org.eclipse.emf.emfstore.internal.server.exceptions.FatalESException;
import org.eclipse.emf.emfstore.internal.server.exceptions.InvalidVersionSpecException;
import org.eclipse.emf.emfstore.internal.server.exceptions.StorageException;
import org.eclipse.emf.emfstore.internal.server.model.ProjectHistory;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.model.SessionId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESUserImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AncestorVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.BranchInfo;
import org.eclipse.emf.emfstore.internal.server.model.versioning.BranchVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageEnvelope;
import org.eclipse.emf.emfstore.internal.server.model.versioning.DateVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.HeadVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PagedUpdateVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.TagVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.Version;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningFactory;
import org.eclipse.emf.emfstore.internal.server.model.versioning.Versions;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.server.ESCloseableIterable;
import org.eclipse.emf.emfstore.server.auth.ESMethod;
import org.eclipse.emf.emfstore.server.auth.ESMethod.MethodId;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.exceptions.ESUpdateRequiredException;
import org.eclipse.emf.emfstore.server.model.ESSessionId;
import org.eclipse.emf.emfstore.server.model.ESUser;

import com.google.common.base.Optional;

/**
 * This subinterface implements all version related functionality.
 *
 * @author wesendon
 */
public class VersionSubInterfaceImpl extends AbstractSubEmfstoreInterface {

	/**
	 * Default constructor.
	 *
	 * @param parentInterface
	 *            parent interface
	 * @throws FatalESException
	 *             in case of failure
	 */
	public VersionSubInterfaceImpl(AbstractEmfstoreInterface parentInterface) throws FatalESException {
		super(parentInterface);
	}

	/**
	 * Resolves a versionSpec and delivers the corresponding primary
	 * versionSpec.
	 *
	 * @param projectId
	 *            project id
	 * @param versionSpec
	 *            versionSpec
	 * @return primary versionSpec
	 *
	 * @throws InvalidVersionSpecException
	 *             if the project ID is invalid
	 * @throws ESException
	 *             if versionSpec can't be resolved or other failure
	 */
	@ESMethod(MethodId.RESOLVEVERSIONSPEC)
	public PrimaryVersionSpec resolveVersionSpec(ProjectId projectId, VersionSpec versionSpec)
		throws InvalidVersionSpecException, ESException {

		sanityCheckObjects(projectId, versionSpec);

		synchronized (getMonitor()) {
			final ProjectHistory projectHistory = getSubInterface(ProjectSubInterfaceImpl.class).getProject(projectId);

			if (versionSpec instanceof PrimaryVersionSpec) {
				return resolvePrimaryVersionSpec(projectHistory, (PrimaryVersionSpec) versionSpec);
			} else if (versionSpec instanceof HeadVersionSpec) {
				return resolveHeadVersionSpec(projectHistory, (HeadVersionSpec) versionSpec);
			} else if (versionSpec instanceof DateVersionSpec) {
				return resolveDateVersionSpec(projectHistory, (DateVersionSpec) versionSpec);
			} else if (versionSpec instanceof TagVersionSpec) {
				return resolveTagVersionSpec(projectHistory, (TagVersionSpec) versionSpec);
			} else if (versionSpec instanceof BranchVersionSpec) {
				return resolveBranchVersionSpec(projectHistory, (BranchVersionSpec) versionSpec);
			} else if (versionSpec instanceof AncestorVersionSpec) {
				return resolveAncestorVersionSpec(projectHistory, (AncestorVersionSpec) versionSpec);
			} else if (versionSpec instanceof PagedUpdateVersionSpec) {
				return resolvePagedUpdateVersionSpec(projectHistory, (PagedUpdateVersionSpec) versionSpec);
			}

			throw new InvalidVersionSpecException(Messages.VersionSubInterfaceImpl_UnknownVersionSpec);
		}
	}

	private PrimaryVersionSpec resolveAncestorVersionSpec(ProjectHistory projectHistory,
		AncestorVersionSpec versionSpec)
		throws InvalidVersionSpecException {

		Version currentSource = getVersion(projectHistory, versionSpec.getSource());
		Version currentTarget = getVersion(projectHistory, versionSpec.getTarget());

		if (currentSource == null || currentTarget == null) {
			throw new InvalidVersionSpecException(Messages.VersionSubInterfaceImpl_Invalid_Source_Or_Target);
		}

		// The goal is to find the common ancestor version of the source and
		// target version from different branches. In
		// order to find the ancestor the algorithm starts at the specified
		// version and walks down the version tree in
		// parallel for source and target until the current versions are equal
		// and the ancestor is found. In Each step
		// only one version (of target and source) is decremented. To find the
		// global ancestor it is necessary that the
		// version with the higher version number is decremented.
		while (currentSource != null && currentTarget != null) {
			if (currentSource == currentTarget) {
				return currentSource.getPrimarySpec();
			}

			// Shortcut for most common merge usecase: If you have 2 parallel
			// branches, only seperated by one level and merge several times from the one branch into the another.
			// This case is also supported by #getVersions
			if (currentSource.getMergedFromVersion().contains(currentTarget)) {
				return currentTarget.getPrimarySpec();
			}

			if (currentSource.getPrimarySpec().getIdentifier() >= currentTarget.getPrimarySpec().getIdentifier()) {
				currentSource = findNextVersion(currentSource);
			} else {
				currentTarget = findNextVersion(currentTarget);
			}

		}
		throw new InvalidVersionSpecException(Messages.VersionSubInterfaceImpl_NoAncestorFound);
	}

	private PrimaryVersionSpec resolvePrimaryVersionSpec(ProjectHistory projectHistory, PrimaryVersionSpec versionSpec)
		throws InvalidVersionSpecException {
		final int index = versionSpec.getIdentifier();
		final String branch = versionSpec.getBranch();
		final int versions = projectHistory.getVersions().size();
		if (0 > index || index >= versions || branch == null) {
			throw new InvalidVersionSpecException(MessageFormat.format(
				Messages.VersionSubInterfaceImpl_InvalidVersionRequested,
				index));
		}

		if (branch.equals(VersionSpec.GLOBAL)) {
			return projectHistory.getVersions().get(index).getPrimarySpec();
		}

		// Get biggest primary version of given branch which is equal or lower
		// to the given versionSpec
		for (int i = index; i >= 0; i--) {
			final Version version = projectHistory.getVersions().get(i);
			if (branch.equals(version.getPrimarySpec().getBranch())) {
				return version.getPrimarySpec();
			}

		}
		throw new InvalidVersionSpecException(Messages.VersionSubInterfaceImpl_PrimaryVersionNotFound);
	}

	private PrimaryVersionSpec resolveHeadVersionSpec(ProjectHistory projectHistory, HeadVersionSpec versionSpec)
		throws InvalidVersionSpecException {
		if (VersionSpec.GLOBAL.equals(versionSpec.getBranch())) {
			return projectHistory.getVersions().get(projectHistory.getVersions().size() - 1).getPrimarySpec();
		}
		final BranchInfo info = getBranchInfo(projectHistory, versionSpec);
		if (info != null) {
			return info.getHead();
		}
		throw new InvalidVersionSpecException(Messages.VersionSubInterfaceImpl_HeadVersionNotFound);
	}

	private PrimaryVersionSpec resolveDateVersionSpec(ProjectHistory projectHistory, DateVersionSpec versionSpec) {
		for (final Version version : projectHistory.getVersions()) {
			final LogMessage logMessage = version.getLogMessage();
			if (logMessage == null || logMessage.getDate() == null) {
				continue;
			}
			if (versionSpec.getDate().before(logMessage.getDate())) {
				final Version previousVersion = version.getPreviousVersion();
				if (previousVersion == null) {
					return VersioningFactory.eINSTANCE.createPrimaryVersionSpec();
				}
				return previousVersion.getPrimarySpec();
			}
		}
		return projectHistory.getLastVersion().getPrimarySpec();
	}

	private PrimaryVersionSpec resolveTagVersionSpec(ProjectHistory projectHistory, TagVersionSpec versionSpec)
		throws InvalidVersionSpecException {
		for (final Version version : projectHistory.getVersions()) {
			for (final TagVersionSpec tag : version.getTagSpecs()) {
				if (versionSpec.equals(tag)) {
					return ModelUtil.clone(version.getPrimarySpec());
				}
			}
		}
		throw new InvalidVersionSpecException(Messages.VersionSubInterfaceImpl_TagVersionNotFound);
	}

	private PrimaryVersionSpec resolveBranchVersionSpec(ProjectHistory projectHistory, BranchVersionSpec versionSpec)
		throws BranchInfoMissingException {
		final BranchInfo branchInfo = getBranchInfo(projectHistory, versionSpec);
		if (branchInfo == null) {
			throw new BranchInfoMissingException(Messages.VersionSubInterfaceImpl_NoBranchInfo);
		}
		return branchInfo.getHead();
	}

	/**
	 * Given a {@link ChangePackageEnvelope} which contains a change package fragment,
	 * stores one or more fragments by attaching them to a session specific adapter.
	 *
	 * @param sessionId
	 *            the {@link SessionId} belonging to the calling user
	 * @param projectId
	 *            the {@link ProjectId}
	 * @param envelope
	 *            the {@link ChangePackageEnvelope} containing the fragment
	 * @return an ID identifying the stored fragment(s)
	 * @throws ESException in case the fragment couldn't be stored
	 */
	@ESMethod(MethodId.UPLOADCHANGEPACKAGEFRAGMENT)
	public String uploadChangePackageFragment(SessionId sessionId,
		ProjectId projectId,
		ChangePackageEnvelope envelope) throws ESException {

		final String proxyId = generateProxyId(projectId.getId());

		final ESSessionId resolvedSession = getAccessControl().getSessions().resolveSessionById(sessionId.getId());

		if (resolvedSession == null) {
			throw new ESException(
				MessageFormat.format(
					Messages.VersionSubInterfaceImpl_0, sessionId.getId()));
		}

		final SessionId session = APIUtil.toInternal(SessionId.class, resolvedSession);
		final Optional<ChangePackageFragmentUploadAdapter> maybeAdapter = ESCollections.find(session.eAdapters(),
			ChangePackageFragmentUploadAdapter.class);
		ChangePackageFragmentUploadAdapter adapter;

		if (!maybeAdapter.isPresent()) {
			adapter = new ChangePackageFragmentUploadAdapter();
			session.eAdapters().add(adapter);
		} else {
			adapter = maybeAdapter.get();
		}

		adapter.addFragment(proxyId, envelope.getFragment());
		if (envelope.isLast()) {
			adapter.markAsComplete(proxyId);
		}

		return proxyId;
	}

	/**
	 * Fetches a single change package fragment.
	 *
	 * @param sessionId
	 *            the {@link SessionId} representing the requesting user
	 * @param projectId the {@link ProjectId} of the associated Project
	 * @param proxyId
	 *            the ID that identifies the list of stored fragments
	 * @param fragmentIndex
	 *            allows to request different change package fragments
	 * @return a {@link ChangePackageEnvelope} containing the change package fragment
	 * @throws ESException in case the mandatory session adapter is missing
	 */
	@ESMethod(MethodId.DOWNLOADCHANGEPACKAGEFRAGMENT)
	public ChangePackageEnvelope downloadChangePackageFragment(SessionId sessionId, ProjectId projectId, String proxyId,
		int fragmentIndex)
		throws ESException {

		final ESSessionId resolvedSession = getAccessControl().getSessions().resolveSessionById(sessionId.getId());
		final SessionId session = APIUtil.toInternal(SessionId.class, resolvedSession);
		final Optional<ChangePackageFragmentProviderAdapter> maybeAdapter = ESCollections.find(session.eAdapters(),
			ChangePackageFragmentProviderAdapter.class);

		if (!maybeAdapter.isPresent()) {
			throw new ESException(Messages.VersionSubInterfaceImpl_ChangePackageFragmentProviderAdapterMissing
				+ sessionId);
		}

		final ChangePackageFragmentProviderAdapter adapter = maybeAdapter.get();
		final ChangePackageEnvelope envelope = VersioningFactory.eINSTANCE.createChangePackageEnvelope();
		final List<String> fragment = adapter.getFragment(proxyId, fragmentIndex);

		envelope.getFragment().addAll(fragment);
		envelope.setFragmentCount(adapter.getFragmentSize(proxyId));
		envelope.setFragmentIndex(fragmentIndex);

		if (envelope.isLast()) {
			adapter.markAsConsumed(proxyId);
		}

		return envelope;
	}

	private String generateProxyId(String projectId) {
		return UUID.nameUUIDFromBytes(projectId.getBytes()).toString();
	}

	/**
	 * Create a new version.
	 *
	 * @param sessionId
	 *            the ID of the session being used to create a new version
	 * @param projectId
	 *            the ID of the project for which a new version is created
	 * @param baseVersionSpec
	 *            the base version
	 * @param changePackage
	 *            the change package containing all changes that make up the new version
	 * @param targetBranch
	 *            the target branch for which to create the new version
	 * @param sourceVersion
	 *            the source version
	 * @param logMessage
	 *            a log message
	 * @return the new version
	 * @throws ESException in case of failure
	 */
	@ESMethod(MethodId.CREATEVERSION)
	public PrimaryVersionSpec createVersion(SessionId sessionId, ProjectId projectId,
		PrimaryVersionSpec baseVersionSpec, AbstractChangePackage changePackage, BranchVersionSpec targetBranch,
		PrimaryVersionSpec sourceVersion, LogMessage logMessage) throws ESException {

		getAccessControl().getSessions().isValid(sessionId.toAPI());
		final ESUser rawUser = getAccessControl().getSessions().getRawUser(sessionId.toAPI());
		final ACUser tmpUser = (ACUser) ESUserImpl.class.cast(rawUser).toInternalAPI();
		final ESUser copyAndResolveUser = getAccessControl().getOrgUnitResolverServive().copyAndResolveUser(
			tmpUser.toAPI());
		final ACUser user = (ACUser) ESUserImpl.class.cast(copyAndResolveUser).toInternalAPI();
		sanityCheckObjects(sessionId, projectId, baseVersionSpec, changePackage, logMessage);

		final ProjectHistory projectHistory = getSubInterface(ProjectSubInterfaceImpl.class).getProject(projectId);
		ModelUtil.logProjectDetails("Creating version on server...", user.getName(), projectHistory.getProjectName(), //$NON-NLS-1$
			projectHistory.getProjectId().getId(), targetBranch != null ? targetBranch.getBranch() : null,
			baseVersionSpec.getIdentifier());

		if (FileBasedChangePackage.class.isInstance(changePackage)
			&& !ServerConfiguration.useFileBasedChangePackageOnServer()) {
			// File-based change package should never arrive here in production mode
			throw new ESException(Messages.VersionSubInterfaceImpl_FileBasedChangePackageNotAllowed);
		} else if (ChangePackage.class.isInstance(changePackage)
			&& ServerConfiguration.useFileBasedChangePackageOnServer()) {
			// Regular change package should never arrive here in production mode
			throw new ESException(Messages.VersionSubInterfaceImpl_FileBasedChangePackageExpected);
		}

		final PrimaryVersionSpec result = internalCreateVersion(projectId, baseVersionSpec, changePackage, targetBranch,
			sourceVersion, logMessage, user);

		ModelUtil.logProjectDetails("Creating version on server... done", user.getName(), //$NON-NLS-1$
			projectHistory.getProjectName(), projectHistory.getProjectId().getId(),
			targetBranch != null ? targetBranch.getBranch() : null,
			baseVersionSpec.getIdentifier());

		return result;
	}

	private PrimaryVersionSpec internalCreateVersion(ProjectId projectId, PrimaryVersionSpec baseVersionSpec,
		AbstractChangePackage changePackage, BranchVersionSpec targetBranch, PrimaryVersionSpec sourceVersion,
		LogMessage logMessage, final ACUser user) throws ESException {
		synchronized (getMonitor()) {

			final long currentTimeMillis = System.currentTimeMillis();
			final ProjectHistory projectHistory = getSubInterface(ProjectSubInterfaceImpl.class).getProject(projectId);

			// Find branch
			final BranchInfo baseBranch = getBranchInfo(projectHistory, baseVersionSpec);
			final Version baseVersion = getVersion(projectHistory, baseVersionSpec);

			if (baseVersion == null || baseBranch == null) {
				throw new InvalidVersionSpecException(
					Messages.VersionSubInterfaceImpl_InvalidBranchOrVersion);
			}

			// defined here fore scoping reasons
			Version newVersion = null;
			BranchInfo newBranch = null;

			// copy project and apply changes
			final Project newProjectState = ((ProjectImpl) getSubInterface(ProjectSubInterfaceImpl.class).getProject(
				baseVersion)).copy();
			changePackage.apply(newProjectState);

			// regular commit
			if (isRegularCommit(targetBranch, baseVersion)) {

				newVersion = performRegularCommit(baseVersionSpec, logMessage, user, projectHistory, baseBranch,
					baseVersion, newProjectState);

				// case for new branch creation
			} else if (isNewBranchCommit(targetBranch, projectHistory)) {
				checkNewBranchCommitPreRequisites(targetBranch.getBranch());
				// when branch does NOT exist, create new branch
				newVersion = createVersion(projectHistory, newProjectState, logMessage, user, baseVersion);
				newBranch = createNewBranch(projectHistory, baseVersion.getPrimarySpec(), newVersion.getPrimarySpec(),
					targetBranch);
				newVersion.setAncestorVersion(baseVersion);

			} else {
				// This point only can be reached with invalid input
				throw new IllegalStateException(Messages.VersionSubInterfaceImpl_TargetBranchCombination_Invalid);
			}

			if (sourceVersion != null) {
				newVersion.getMergedFromVersion().add(getVersion(projectHistory, sourceVersion));
			}

			// try to save
			try {
				try {
					trySave(projectId, changePackage, projectHistory, newVersion, newProjectState);
				} catch (final FatalESException e) {
					// try to roll back. removing version is necessary in all cases
					rollback(projectHistory, baseBranch, baseVersion, newVersion, newBranch, e);
				}

				// if ancestor isn't null, a new branch was created. In this
				// case we want to keep the old base project
				// state
				if (newVersion.getAncestorVersion() == null && baseVersion.getProjectState() != null) {
					// delete projectstate from last revision depending on
					// persistence policy
					deleteOldProjectStateAccordingToOptions(projectId, baseVersion);
				}

				save(baseVersion);
				save(projectHistory);

			} catch (final FatalESException e) {
				// roll back failed
				EMFStoreController.getInstance().shutdown(e);
				throw new ESException(Messages.VersionSubInterfaceImpl_ShuttingServerDown);
			}

			ModelUtil.logInfo(
				Messages.VersionSubInterfaceImpl_TotalTimeForCommit + (System.currentTimeMillis() - currentTimeMillis));
			return newVersion.getPrimarySpec();
		}
	}

	/**
	 * @param targetBranch
	 * @throws InvalidVersionSpecException
	 */
	private void checkNewBranchCommitPreRequisites(String targetBranchName) throws InvalidVersionSpecException {
		if (targetBranchName.equals(StringUtils.EMPTY)) {
			throw new InvalidVersionSpecException(Messages.VersionSubInterfaceImpl_EmptyBranch_Not_Allowed);
		} else if (targetBranchName.equals(VersionSpec.GLOBAL)) {
			throw new InvalidVersionSpecException(
				Messages.VersionSubInterfaceImpl_BranchName_Reserved_1
					+ VersionSpec.GLOBAL + Messages.VersionSubInterfaceImpl_BranchName_Reserved_2);
		}
	}

	private Version performRegularCommit(PrimaryVersionSpec baseVersionSpec, LogMessage logMessage, final ACUser user,
		final ProjectHistory projectHistory, final BranchInfo baseBranch, final Version baseVersion,
		final Project newProjectState) throws ESUpdateRequiredException, ESException {
		Version newVersion;
		// If branch is null or branch equals base branch, create new
		// version for specific branch
		if (!baseVersionSpec.equals(isHeadOfBranch(projectHistory, baseVersion.getPrimarySpec()))) {
			throw new ESUpdateRequiredException();
		}
		newVersion = createVersion(projectHistory, newProjectState, logMessage, user, baseVersion);
		newVersion.setPreviousVersion(baseVersion);
		baseBranch.setHead(ModelUtil.clone(newVersion.getPrimarySpec()));
		return newVersion;
	}

	/**
	 * @param targetBranch
	 * @param projectHistory
	 * @return
	 */
	private boolean isNewBranchCommit(BranchVersionSpec targetBranch, final ProjectHistory projectHistory) {
		return getBranchInfo(projectHistory, targetBranch) == null;
	}

	/**
	 * @param targetBranch
	 * @param baseVersion
	 * @return
	 */
	private boolean isRegularCommit(BranchVersionSpec targetBranch, final Version baseVersion) {
		return targetBranch == null || baseVersion.getPrimarySpec().getBranch().equals(targetBranch.getBranch());
	}

	private void rollback(final ProjectHistory projectHistory, final BranchInfo baseBranch,
		final Version baseVersion, Version newVersion, BranchInfo newBranch, final FatalESException e)
		throws StorageException {
		projectHistory.getVersions().remove(newVersion);

		if (newBranch == null) {
			// normal commit
			baseVersion.setNextVersion(null);
			baseBranch.setHead(ModelUtil.clone(baseVersion.getPrimarySpec()));
		} else {
			// branch commit
			baseVersion.getBranchedVersions().remove(newVersion);
			projectHistory.getBranches().remove(newBranch);
		}
		// TODO: delete obsolete project, change package and version files
		throw new StorageException(StorageException.NOSAVE, e);
	}

	/**
	 * @param projectId
	 * @param changePackage
	 * @param projectHistory
	 * @param newVersion
	 * @param newProjectState
	 * @throws FatalESException
	 */
	private void trySave(ProjectId projectId, AbstractChangePackage changePackage, final ProjectHistory projectHistory,
		Version newVersion, final Project newProjectState) throws FatalESException {
		getResourceHelper().createResourceForProject(newProjectState,
			newVersion.getPrimarySpec(), projectHistory.getProjectId());
		getResourceHelper().createResourceForChangePackage(changePackage, newVersion.getPrimarySpec(),
			projectId);
		if (FileBasedChangePackage.class.isInstance(changePackage)) {
			try {
				/* move the temporary file to the project folder */
				final URI uri = changePackage.eResource().getURI();
				final URI normalizedUri = changePackage.eResource().getResourceSet().getURIConverter().normalize(uri);
				final String filePath = normalizedUri.toFileString() + ".1"; //$NON-NLS-1$
				FileBasedChangePackage.class.cast(changePackage).move(filePath);
				ModelUtil.saveResource(changePackage.eResource(), ModelUtil.getResourceLogger());
			} catch (final IOException ex) {
				throw new FatalESException(StorageException.NOSAVE, ex);
			}
		}

		getResourceHelper().createResourceForVersion(newVersion, projectHistory.getProjectId());

		newVersion.setProjectStateResource(newProjectState.eResource());
		newVersion.setChangeResource(changePackage.eResource());
	}

	private BranchInfo createNewBranch(ProjectHistory projectHistory, PrimaryVersionSpec baseSpec,
		PrimaryVersionSpec primarySpec, BranchVersionSpec branch) {
		primarySpec.setBranch(branch.getBranch());

		final BranchInfo branchInfo = VersioningFactory.eINSTANCE.createBranchInfo();
		branchInfo.setName(branch.getBranch());
		branchInfo.setSource(ModelUtil.clone(baseSpec));
		branchInfo.setHead(ModelUtil.clone(primarySpec));

		projectHistory.getBranches().add(branchInfo);

		return branchInfo;
	}

	private Version createVersion(ProjectHistory projectHistory, Project projectState, LogMessage logMessage,
		ACUser user, Version previousVersion) throws ESException {
		final Version newVersion = VersioningFactory.eINSTANCE.createVersion();

		long computedChecksum = ModelUtil.NO_CHECKSUM;

		try {
			if (ServerConfiguration.isComputeChecksumOnCommitActive()) {
				computedChecksum = ModelUtil.computeChecksum(projectState);
				ModelUtil.logProjectDetails(
					MessageFormat.format("Checksum computation during version create: {0}", computedChecksum), //$NON-NLS-1$
					user.getName(), projectHistory.getProjectName(), projectHistory.getProjectId().getId(), null, -1);
			}
		} catch (final SerializationException exception) {
			// TODO: clarify what to do in case checksum computation fails + provide ext. point
			throw new ESException(MessageFormat.format(
				Messages.VersionSubInterfaceImpl_ChecksumComputationFailed,
				projectHistory.getProjectName()), exception);
		}

		// newVersion.setChanges(changePackage);

		logMessage.setDate(new Date());
		logMessage.setAuthor(user.getName());
		newVersion.setLogMessage(logMessage);

		// latest version == getVersion.size() (version start with index 0 as
		// the list), branch from previous is used.
		newVersion.setPrimarySpec(Versions.createPRIMARY(previousVersion.getPrimarySpec(), projectHistory.getVersions()
			.size()));
		newVersion.getPrimarySpec().setProjectStateChecksum(computedChecksum);
		newVersion.setNextVersion(null);

		projectHistory.getVersions().add(newVersion);
		return newVersion;
	}

	private Version getVersion(ProjectHistory projectHistory, PrimaryVersionSpec baseVersionSpec) {
		if (0 > baseVersionSpec.getIdentifier()
			|| baseVersionSpec.getIdentifier() > projectHistory.getVersions().size() - 1) {
			return null;
		}
		final Version version = projectHistory.getVersions().get(baseVersionSpec.getIdentifier());
		if (version == null || !version.getPrimarySpec().equals(baseVersionSpec)) {
			return null;
		}
		return version;
	}

	private PrimaryVersionSpec isHeadOfBranch(ProjectHistory projectHistory, PrimaryVersionSpec versionSpec) {
		final BranchInfo branchInfo = getBranchInfo(projectHistory, versionSpec);
		if (branchInfo != null && branchInfo.getHead().equals(versionSpec)) {
			return branchInfo.getHead();
		}
		return null;
	}

	private BranchInfo getBranchInfo(ProjectHistory projectHistory, VersionSpec versionSpec) {
		for (final BranchInfo branchInfo : projectHistory.getBranches()) {
			if (branchInfo.getName().equals(versionSpec.getBranch())) {
				return branchInfo;
			}
		}
		return null;
	}

	/**
	 * Returns all branches for the project with the given ID.
	 *
	 * @param projectId
	 *            the ID of a project
	 * @return a list containing information about each branch
	 * @throws ESException in case of failure
	 */
	@ESMethod(MethodId.GETBRANCHES)
	public List<BranchInfo> getBranches(ProjectId projectId) throws ESException {
		synchronized (getMonitor()) {
			final ProjectHistory projectHistory = getSubInterface(ProjectSubInterfaceImpl.class).getProject(projectId);
			final ArrayList<BranchInfo> result = new ArrayList<BranchInfo>();
			for (final BranchInfo branch : projectHistory.getBranches()) {
				result.add(ModelUtil.clone(branch));
			}
			return result;
		}
	}

	/**
	 * Deletes projectstate from last revision depending on persistence policy.
	 *
	 * @param projectId
	 *            project id
	 * @param previousHeadVersion
	 *            last head version
	 */
	private void deleteOldProjectStateAccordingToOptions(ProjectId projectId, Version previousHeadVersion) {
		if (shouldDeleteOldProjectStateAccordingToOptions(projectId, previousHeadVersion, getResourceHelper())) {
			getResourceHelper().deleteProjectState(previousHeadVersion, projectId);
		}
	}

	/**
	 * Whether a projectstate should be deleted according to the server configuration.
	 *
	 * @param projectId project id
	 * @param previousHeadVersion last head version
	 * @param resourceHelper the resource helper
	 * @return <code>true</code> if project state should be deleted, <code>false</code> otherwise
	 */
	static boolean shouldDeleteOldProjectStateAccordingToOptions(
		ProjectId projectId,
		Version previousHeadVersion,
		ResourceHelper resourceHelper) {

		final boolean keepBecauseOfTaggedVersion = ServerConfiguration.createProjectStateOnTag()
			&& !previousHeadVersion.getTagSpecs().isEmpty();
		if (keepBecauseOfTaggedVersion) {
			return false;
		}

		final String property = ServerConfiguration.getProperties().getProperty(
			ServerConfiguration.PROJECTSTATE_VERSION_PERSISTENCE,
			ServerConfiguration.PROJECTSPACE_VERSION_PERSISTENCE_DEFAULT);

		if (property.equals(ServerConfiguration.PROJECTSTATE_VERSION_PERSISTENCE_EVERYXVERSIONS)) {

			final int x = resourceHelper.getXFromPolicy(
				ServerConfiguration.PROJECTSTATE_VERSION_PERSISTENCE_EVERYXVERSIONS_X,
				ServerConfiguration.PROJECTSTATE_VERSION_PERSISTENCE_EVERYXVERSIONS_X_DEFAULT, false);

			// always save projecstate of first version
			final int lastVersion = previousHeadVersion.getPrimarySpec().getIdentifier();
			if (lastVersion != 0 && lastVersion % x != 0) {
				return true;
			}

		} else {
			return true;
		}

		return false;
	}

	/**
	 * Returns all changes within the specified version range for a given project.
	 *
	 * @param projectId
	 *            the ID of a project
	 * @param source
	 *            the source version
	 * @param target
	 *            the target version (inclusive)
	 * @return a list of change packages containing all the changes for the specified version range
	 *
	 * @throws InvalidVersionSpecException
	 *             if an invalid version has been specified
	 * @throws ESException
	 *             in case of failure
	 */
	@ESMethod(MethodId.GETCHANGES)
	public List<AbstractChangePackage> getChanges(ProjectId projectId, VersionSpec source, VersionSpec target)
		throws InvalidVersionSpecException, ESException {

		sanityCheckObjects(projectId, source, target);
		final PrimaryVersionSpec resolvedSource = resolveVersionSpec(projectId, source);
		final PrimaryVersionSpec resolvedTarget = resolveVersionSpec(projectId, target);
		// if target and source are equal return empty list
		if (resolvedSource.getIdentifier() == resolvedTarget.getIdentifier()) {
			return new ArrayList<AbstractChangePackage>();
		}

		synchronized (getMonitor()) {

			final boolean updateForward = resolvedTarget.getIdentifier() > resolvedSource.getIdentifier();

			// Example: if you want the changes to get from version 5 to 7, you
			// need the changes contained in version 6
			// and 7. The reason is that each version holds the changes which
			// occurred from the predecessor to the
			// version itself. Version 5 holds the changes to get from version 4
			// to 5 and therefore is irrelevant.
			// For that reason the first version is removed, since getVersions
			// always sorts ascending order.
			final List<Version> versions = getVersions(projectId, resolvedSource, resolvedTarget);
			if (versions.size() > 1) {
				versions.remove(0);
			}

			List<AbstractChangePackage> result = new ArrayList<AbstractChangePackage>();
			for (final Version version : versions) {
				final AbstractChangePackage changes = version.getChanges();
				if (changes != null) {
					changes.setLogMessage(ModelUtil.clone(version.getLogMessage()));
					result.add(changes);
				}
			}

			// if source is after target in time
			if (!updateForward) {
				// reverse list and change packages
				final List<AbstractChangePackage> resultReverse = new ArrayList<AbstractChangePackage>();
				for (final AbstractChangePackage changePackage : result) {

					final ChangePackage changePackageReverse = VersioningFactory.eINSTANCE.createChangePackage();
					final ESCloseableIterable<AbstractOperation> reversedOperations = changePackage
						.reversedOperations();
					final ArrayList<AbstractOperation> copiedReversedOperations = new ArrayList<AbstractOperation>();
					try {
						for (final AbstractOperation op : reversedOperations.iterable()) {
							copiedReversedOperations.add(op.reverse());
						}
					} finally {
						reversedOperations.close();
					}

					for (final AbstractOperation reversedOperation : copiedReversedOperations) {
						changePackageReverse.add(reversedOperation);
					}

					// copy again log message
					// reverse() created a new change package without copying
					// existent attributes
					changePackageReverse.setLogMessage(ModelUtil.clone(
						changePackage.getLogMessage()));
					resultReverse.add(changePackageReverse);
				}

				Collections.reverse(resultReverse);
				result = resultReverse;
			}

			return result;
		}
	}

	/**
	 * Returns the specified version of a project.
	 *
	 * @param projectId
	 *            project id
	 * @param versionSpec
	 *            versionSpec
	 * @return the version
	 * @throws ESException
	 *             if version couldn't be found
	 */
	protected Version getVersion(ProjectId projectId, PrimaryVersionSpec versionSpec) throws ESException {
		final ProjectHistory project = getSubInterface(ProjectSubInterfaceImpl.class).getProject(projectId);
		return getVersion(project, versionSpec);
	}

	/**
	 * Returns a list of versions starting from source and ending with target.
	 * This method returns the version always in an ascanding order. So if you
	 * need it ordered differently you have to reverse the list.
	 *
	 * @param projectId
	 *            project id
	 * @param source
	 *            source
	 * @param target
	 *            target
	 * @return list of versions
	 * @throws ESException
	 *             if source or target are out of range or any other problem
	 *             occurs
	 */
	protected List<Version> getVersions(ProjectId projectId, PrimaryVersionSpec source, PrimaryVersionSpec target)
		throws ESException {
		if (source.compareTo(target) < 1) {
			final ProjectHistory projectHistory = getSubInterface(ProjectSubInterfaceImpl.class).getProject(projectId);

			final Version sourceVersion = getVersion(projectHistory, source);
			final Version targetVersion = getVersion(projectHistory, target);

			if (sourceVersion == null || targetVersion == null) {
				throw new InvalidVersionSpecException(Messages.VersionSubInterfaceImpl_NoSourceNorTarget);
			}
			final List<Version> result = new ArrayList<Version>();

			// since the introduction of branches the versions are collected
			// in different order.
			Version currentVersion = targetVersion;
			while (currentVersion != null) {
				result.add(currentVersion);
				if (currentVersion.equals(sourceVersion)) {
					break;
				}
				if (currentVersion.getPrimarySpec().compareTo(sourceVersion.getPrimarySpec()) < 0) {
					// walked too far, invalid path.
					throw new InvalidVersionSpecException(Messages.VersionSubInterfaceImpl_InvalidPath);
				}
				// Shortcut for most common merge usecase: If you have 2
				// parallel branches and merge several times
				// from the one branch into the another.
				if (currentVersion.getMergedFromVersion().contains(sourceVersion)) {
					// add sourceVersion because #getChanges always removes
					// the first version
					result.add(sourceVersion);
					break;
				}

				currentVersion = findNextVersion(currentVersion);
			}
			// versions are collected in descending order, so the result has to be reversed.
			Collections.reverse(result);
			return result;
		}

		return getVersions(projectId, target, source);
	}

	/**
	 * Helper method which retrieves the next version in the history tree. This
	 * method must be used in reversed order. With the introduction of branches, the versions are organized in a tree
	 * structure. Therefore, next versions are always searched for walking up the tree.
	 *
	 * @param currentVersion
	 *            current version
	 * @return version
	 * @throws InvalidVersionSpecException
	 *             if the path can't be followed further
	 */
	public static Version findNextVersion(Version currentVersion) throws InvalidVersionSpecException {
		// find next version
		if (currentVersion.getPreviousVersion() != null) {
			currentVersion = currentVersion.getPreviousVersion();
		} else if (currentVersion.getAncestorVersion() != null) {
			currentVersion = currentVersion.getAncestorVersion();
		} else {
			throw new InvalidVersionSpecException(Messages.VersionSubInterfaceImpl_NextVersionInvalid);
		}
		return currentVersion;
	}

	private PrimaryVersionSpec resolvePagedUpdateVersionSpec(ProjectHistory projectHistory,
		PagedUpdateVersionSpec baseVersion) {

		int changes = 0;
		PrimaryVersionSpec resolvedSpec = baseVersion.getBaseVersionSpec();
		int maxChanges = baseVersion.getMaxChanges();

		int i = resolvedSpec.getIdentifier();

		AbstractChangePackage cp = projectHistory.getVersions().get(i).getChanges();

		if (i == projectHistory.getVersions().size() - 1) {
			return projectHistory.getVersions().get(i).getPrimarySpec();
		}

		do {
			cp = projectHistory.getVersions().get(++i).getChanges();
		} while (cp == null && i < projectHistory.getVersions().size());

		// pull at least one change package
		if (cp.leafSize() > maxChanges) {
			maxChanges = cp.leafSize();
		}

		while (changes < maxChanges && i < projectHistory.getVersions().size()) {
			resolvedSpec = projectHistory.getVersions().get(i).getPrimarySpec();
			final Version version = projectHistory.getVersions().get(i);
			final AbstractChangePackage changePackage = version.getChanges();

			if (changePackage != null) {
				final int size = changePackage.leafSize();
				if (changes + size >= maxChanges) {
					resolvedSpec = projectHistory.getVersions().get(i).getPrimarySpec();
					break;
				}
				changes += size;
			}
			i += 1;
		}

		return resolvedSpec;
	}
}