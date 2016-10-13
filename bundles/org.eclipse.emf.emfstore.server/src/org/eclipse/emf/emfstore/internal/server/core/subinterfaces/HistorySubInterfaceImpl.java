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
package org.eclipse.emf.emfstore.internal.server.core.subinterfaces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.eclipse.emf.emfstore.internal.server.core.AbstractEmfstoreInterface;
import org.eclipse.emf.emfstore.internal.server.core.AbstractSubEmfstoreInterface;
import org.eclipse.emf.emfstore.internal.server.exceptions.FatalESException;
import org.eclipse.emf.emfstore.internal.server.exceptions.InvalidInputException;
import org.eclipse.emf.emfstore.internal.server.exceptions.InvalidVersionSpecException;
import org.eclipse.emf.emfstore.internal.server.exceptions.StorageException;
import org.eclipse.emf.emfstore.internal.server.model.ProjectHistory;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.BranchInfo;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo;
import org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryQuery;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ModelElementQuery;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PathQuery;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.RangeQuery;
import org.eclipse.emf.emfstore.internal.server.model.versioning.TagVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.Version;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningFactory;
import org.eclipse.emf.emfstore.internal.server.model.versioning.Versions;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.server.ESCloseableIterable;
import org.eclipse.emf.emfstore.server.ESServerURIUtil;
import org.eclipse.emf.emfstore.server.auth.ESMethod;
import org.eclipse.emf.emfstore.server.auth.ESMethod.MethodId;
import org.eclipse.emf.emfstore.server.exceptions.ESException;

/**
 * This subinterfaces implements all history related functionality for the
 * EmfStoreImpl interface.
 *
 * @author wesendon
 */
public class HistorySubInterfaceImpl extends AbstractSubEmfstoreInterface {

	/**
	 * Default constructor.
	 *
	 * @param parentInterface
	 *            parent interface
	 * @throws FatalESException
	 *             in case of failure
	 */
	public HistorySubInterfaceImpl(AbstractEmfstoreInterface parentInterface) throws FatalESException {
		super(parentInterface);
	}

	@Override
	protected void initSubInterface() throws FatalESException {
		super.initSubInterface();
	}

	/**
	 * Add a tag to the specified version specifier.
	 *
	 * @param projectId
	 *            the ID of a project
	 * @param versionSpec
	 *            the version specifier
	 * @param tag
	 *            the tag to be added
	 * @throws ESException in case of failure
	 *
	 */
	@ESMethod(MethodId.ADDTAG)
	public void addTag(ProjectId projectId, PrimaryVersionSpec versionSpec, TagVersionSpec tag)
		throws ESException {

		sanityCheckObjects(projectId, versionSpec, tag);
		synchronized (getMonitor()) {
			final Version version = getSubInterface(VersionSubInterfaceImpl.class).getVersion(projectId, versionSpec);
			// stamp branch instead of throwing an exception
			tag.setBranch(versionSpec.getBranch());
			version.getTagSpecs().add(tag);

			try {
				save(version);
			} catch (final FatalESException e) {
				throw new StorageException(StorageException.NOSAVE);
			}

			if (ServerConfiguration.createProjectStateOnTag()) {
				final URI projectStateURI = ESServerURIUtil.createProjectStateURI(projectId, version.getPrimarySpec());
				if (ESServerURIUtil.exists(projectStateURI)) {
					/* the project state is existing, just return */
					return;
				}
				final Project projectState = ProjectSubInterfaceImpl.getProjectFromVersion(version);
				try {
					getResourceHelper().createResourceForProject(projectState, versionSpec, projectId);
				} catch (final FatalESException e) {
					throw new StorageException(StorageException.NOSAVE);
				}
			}
		}
	}

	/**
	 * Removes the tag from the specified version specifier.
	 *
	 * @param projectId
	 *            the ID of a project
	 * @param versionSpec
	 *            the version specifier
	 * @param tag
	 *            the tag to be removed
	 * @throws ESException in case of failure
	 *
	 */
	@ESMethod(MethodId.REMOVETAG)
	public void removeTag(ProjectId projectId, PrimaryVersionSpec versionSpec, TagVersionSpec tag)
		throws ESException {
		sanityCheckObjects(projectId, versionSpec, tag);
		synchronized (getMonitor()) {
			/* remove tag */
			final Version version = getSubInterface(VersionSubInterfaceImpl.class).getVersion(projectId, versionSpec);
			final Iterator<TagVersionSpec> iterator = version.getTagSpecs().iterator();
			while (iterator.hasNext()) {
				if (iterator.next().getName().equals(tag.getName())) {
					iterator.remove();
				}
			}
			try {
				save(version);
			} catch (final FatalESException e) {
				throw new StorageException(StorageException.NOSAVE);
			}

			/* delete project state if necessary */
			if (ServerConfiguration.createProjectStateOnTag()) {
				final URI projectStateURI = ESServerURIUtil.createProjectStateURI(projectId, version.getPrimarySpec());
				if (!ESServerURIUtil.exists(projectStateURI)) {
					/* the project state is not existing, just return */
					return;
				}
				if (VersionSubInterfaceImpl.shouldDeleteOldProjectStateAccordingToOptions(projectId, version,
					getResourceHelper())) {
					getResourceHelper().deleteProjectState(version, projectId);
				}
			}
		}
	}

	/**
	 * Returns history information for the given project.
	 *
	 * @param projectId
	 *            the {@link ProjectId} of the project whose history should be fetched
	 * @param historyQuery
	 *            the history query
	 * @return a list containing {@link HistoryInfo} about the project
	 * @throws ESException in case an error occurs while fetching the history
	 */
	@ESMethod(MethodId.GETHISTORYINFO)
	public List<HistoryInfo> getHistoryInfo(ProjectId projectId, HistoryQuery<?> historyQuery) throws ESException {
		sanityCheckObjects(projectId, historyQuery);
		synchronized (getMonitor()) {

			// TODO LCP model element query disabled
			if (historyQuery instanceof ModelElementQuery) {

				return handleMEQuery(projectId, (ModelElementQuery) historyQuery);

			} else if (historyQuery instanceof RangeQuery) {

				return versionToHistoryInfo(projectId, handleRangeQuery(projectId, (RangeQuery<?>) historyQuery),
					historyQuery.isIncludeChangePackages());

			} else if (historyQuery instanceof PathQuery) {

				return versionToHistoryInfo(projectId, handlePathQuery(projectId, (PathQuery) historyQuery),
					historyQuery.isIncludeChangePackages());

			}
			return Collections.emptyList();
		}
	}

	private List<Version> handleRangeQuery(ProjectId projectId, RangeQuery<?> query) throws ESException {
		final ProjectHistory project = getSubInterface(ProjectSubInterfaceImpl.class).getProject(projectId);
		if (query.isIncludeAllVersions()) {
			return getAllVersions(project, sourceNumber(query) - query.getLowerLimit(),
				sourceNumber(query) + query.getUpperLimit(), true);
		}
		final Version version = getSubInterface(VersionSubInterfaceImpl.class).getVersion(projectId, query.getSource());
		final TreeSet<Version> result = new TreeSet<Version>(new VersionComparator(false));
		result.addAll(addForwardVersions(project, version, query.getUpperLimit(), query.isIncludeIncoming(),
			query.isIncludeOutgoing()));
		result.add(version);
		result.addAll(addBackwardVersions(project, version, query.getLowerLimit(), query.isIncludeIncoming(),
			query.isIncludeOutgoing()));
		return new ArrayList<Version>(result);
	}

	private List<Version> handlePathQuery(ProjectId projectId, PathQuery query) throws ESException {
		final ProjectHistory project = getSubInterface(ProjectSubInterfaceImpl.class).getProject(projectId);
		if (query.isIncludeAllVersions()) {
			final List<Version> result = getAllVersions(project, sourceNumber(query), targetNumber(query), false);
			if (targetNumber(query) > sourceNumber(query)) {
				Collections.reverse(result);
			}
			return result;
		}
		if (query.getSource() == null || query.getTarget() == null) {
			throw new InvalidInputException();
		}
		final List<Version> result = getSubInterface(VersionSubInterfaceImpl.class).getVersions(projectId,
			query.getSource(),
			query.getTarget());
		if (query.getTarget().compareTo(query.getSource()) < 0) {
			Collections.reverse(result);
		}
		return result;
	}

	private List<HistoryInfo> handleMEQuery(ProjectId projectId, ModelElementQuery query) throws ESException {
		final List<Version> relevantVersions = filterVersions(
			handleRangeQuery(projectId, query),
			query.getModelElements());
		final List<HistoryInfo> historyInfos = versionToHistoryInfo(
			projectId,
			relevantVersions,
			query.isIncludeChangePackages());
		final List<HistoryInfo> filteredHistoryInfos = new ArrayList<HistoryInfo>();

		for (final HistoryInfo historyInfo : historyInfos) {
			final HistoryInfo clonedHistoryInfo = ModelUtil.clone(historyInfo);
			final List<AbstractOperation> ops = filterOperationsForSelectedElements(
				query.getModelElements(),
				clonedHistoryInfo);

			if (historyInfo.getChangePackage() != null) {
				final ChangePackage newChangePackage = VersioningFactory.eINSTANCE.createChangePackage();
				newChangePackage.getOperations().addAll(ops);
				newChangePackage.setLogMessage(historyInfo.getChangePackage().getLogMessage());
				clonedHistoryInfo.setChangePackage(newChangePackage);
			}
			filteredHistoryInfos.add(clonedHistoryInfo);
		}
		return filteredHistoryInfos;
	}

	// TODO LCP combine with op filtering
	private List<Version> filterVersions(List<Version> inRange, List<ModelElementId> modelElements) {
		final ArrayList<Version> result = new ArrayList<Version>();
		for (final Version version : inRange) {
			// special case for initial version
			if (version.getPrimarySpec() != null && version.getPrimarySpec().getIdentifier() == 0) {
				if (version.getProjectState() != null) {
					for (final ModelElementId id : modelElements) {
						if (version.getProjectState().contains(id)) {
							result.add(version);
							break;
						}
					}
				}
			}
			if (version.getChanges() == null) {
				continue;
			}

			final Set<ModelElementId> involvedModelElements = getAllInvolvedModelElements(version.getChanges());
			for (final ModelElementId id : modelElements) {
				if (involvedModelElements.contains(id)) {
					result.add(version);
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Returns all model elements associated with the given change package.
	 *
	 * @param changePackage the change package to analyze
	 * @return a set of {@link ModelElementId}s representing the involved model elements
	 */
	public Set<ModelElementId> getAllInvolvedModelElements(AbstractChangePackage changePackage) {
		final Set<ModelElementId> result = new LinkedHashSet<ModelElementId>();
		final ESCloseableIterable<AbstractOperation> operations = changePackage.operations();
		try {
			for (final AbstractOperation op : operations.iterable()) {
				result.addAll(op.getAllInvolvedModelElements());
			}
		} finally {
			operations.close();
		}
		return result;
	}

	private int sourceNumber(HistoryQuery<?> query) throws ESException {
		if (query.getSource() == null) {
			throw new InvalidInputException();
		}
		return query.getSource().getIdentifier();
	}

	private int targetNumber(PathQuery query) throws ESException {
		if (query.getTarget() == null) {
			throw new InvalidInputException();
		}
		return query.getTarget().getIdentifier();
	}

	/**
	 * @return higher versions first
	 * @throws ESException
	 */
	private List<Version> getAllVersions(ProjectHistory project, int from, int to, boolean tollerant)
		throws ESException {
		if (to < from) {
			return getAllVersions(project, to, from, tollerant);
		}
		final EList<Version> versions = project.getVersions();
		final int globalhead = versions.size() - 1;

		int start = to;
		int end = from;

		if (!tollerant && (from < 0 || to < 0 || from > globalhead || to > globalhead)) {
			throw new InvalidVersionSpecException(Messages.HistorySubInterfaceImpl_InvalidVersionSpec);
		}

		start = Math.min(globalhead, to);
		end = Math.max(0, from);

		if (start < 0 || start > globalhead || end < 0 || end > globalhead) {
			throw new InvalidVersionSpecException(Messages.HistorySubInterfaceImpl_InvalidVersionSpec);
		}

		if (start == end) {
			return Arrays.asList(versions.get(start));
		}

		// saftey check
		if (Math.abs(start - end) > Math.abs(to - from)) {
			throw new InvalidVersionSpecException(Messages.HistorySubInterfaceImpl_InvalidVersionSpec);
		}
		final ArrayList<Version> result = new ArrayList<Version>();
		for (int i = start; i >= end; i--) {
			result.add(versions.get(i));
		}
		return result;
	}

	private Collection<Version> addForwardVersions(ProjectHistory project, Version version, int limit,
		boolean includeIncoming, boolean includeOutgoing) {
		if (limit == 0) {
			return Collections.emptyList();
		}
		final SortedSet<Version> result = new TreeSet<Version>(new VersionComparator(false));
		Version currentVersion = version;
		while (currentVersion != null && result.size() < limit) {
			if (includeOutgoing && currentVersion.getBranchedVersions().size() > 0) {
				result.addAll(currentVersion.getBranchedVersions());
			}
			if (includeIncoming && currentVersion.getMergedFromVersion().size() > 0) {
				result.addAll(currentVersion.getMergedFromVersion());
			}

			currentVersion = currentVersion.getNextVersion();
			if (currentVersion != null) {
				result.add(currentVersion);
			}
		}

		if (limit > 0 && result.size() > limit) {
			return new ArrayList<Version>(result).subList(0, limit);
		}
		return result;
	}

	private Collection<Version> addBackwardVersions(ProjectHistory project, Version version, int limit,
		boolean includeIncoming, boolean includeOutgoing) {
		if (limit == 0) {
			return Collections.emptyList();
		}
		final SortedSet<Version> result = new TreeSet<Version>(new VersionComparator(false));
		Version currentVersion = version;
		while (currentVersion != null && result.size() < limit) {
			if (includeOutgoing && currentVersion.getBranchedVersions().size() > 0) {
				result.addAll(currentVersion.getBranchedVersions());
			}
			if (includeIncoming && currentVersion.getMergedFromVersion().size() > 0) {
				result.addAll(currentVersion.getMergedFromVersion());
			}
			// move in tree
			if (currentVersion.getPreviousVersion() != null) {
				currentVersion = currentVersion.getPreviousVersion();
			} else if (currentVersion.getAncestorVersion() != null) {
				currentVersion = currentVersion.getAncestorVersion();
			} else {
				currentVersion = null;
			}
			// add versions
			if (currentVersion != null) {
				result.add(currentVersion);
			}
		}
		if (limit > 0 && result.size() > limit) {
			return new ArrayList<Version>(result).subList(0, limit);
		}
		return result;
	}

	// TODO LCP
	private List<AbstractOperation> filterOperationsForSelectedElements(List<ModelElementId> ids,
		HistoryInfo historyInfo) {
		final List<AbstractOperation> ops = new ArrayList<AbstractOperation>();
		if (historyInfo.getChangePackage() == null) {
			return ops;
		}
		final ESCloseableIterable<AbstractOperation> operations = historyInfo.getChangePackage().operations();
		try {
			for (final AbstractOperation operation : operations.iterable()) {
				for (final ModelElementId id : ids) {
					if (operation.getAllInvolvedModelElements().contains(id)) {
						ops.add(operation);
					}
				}
			}
		} finally {
			operations.close();
		}
		return ops;
	}

	private List<HistoryInfo> versionToHistoryInfo(ProjectId projectId, Collection<Version> versions, boolean includeCP)
		throws ESException {
		final ArrayList<HistoryInfo> result = new ArrayList<HistoryInfo>();
		for (final Version version : versions) {
			result.add(createHistoryInfo(projectId, version, includeCP));
		}
		return result;
	}

	/**
	 * Generates a history info from a version. If needed also adds the HEAD
	 * tag, which isn't persistent.
	 *
	 * @param projectId
	 *            project
	 * @param version
	 *            version
	 * @param includeChangePackage
	 * @return history info
	 * @throws ESException
	 */
	private HistoryInfo createHistoryInfo(ProjectId projectId, Version version, boolean includeChangePackage)
		throws ESException {
		final HistoryInfo history = VersioningFactory.eINSTANCE.createHistoryInfo();
		if (includeChangePackage && version.getChanges() != null) {
			history.setChangePackage(ModelUtil.clone(version.getChanges()));
		}
		history.setLogMessage(ModelUtil.clone(version.getLogMessage()));
		// Set Version References
		history.setPrimarySpec(ModelUtil.clone(version.getPrimarySpec()));
		if (version.getAncestorVersion() != null) {
			history.setPreviousSpec(ModelUtil.clone(version.getAncestorVersion().getPrimarySpec()));
		} else if (version.getPreviousVersion() != null) {
			history.setPreviousSpec(ModelUtil.clone(version.getPreviousVersion().getPrimarySpec()));
		}
		if (version.getNextVersion() != null) {
			history.getNextSpec().add(ModelUtil.clone(version.getNextVersion().getPrimarySpec()));
		}
		history.getNextSpec().addAll(addSpecs(version.getBranchedVersions()));
		history.getMergedFrom().addAll(addSpecs(version.getMergedFromVersion()));
		history.getMergedTo().addAll(addSpecs(version.getMergedToVersion()));

		setTags(projectId, version, history);
		return history;
	}

	private void setTags(ProjectId projectId, Version version, HistoryInfo history) throws ESException {
		final ProjectHistory project = getSubInterface(ProjectSubInterfaceImpl.class).getProject(projectId);

		if (version.getPrimarySpec().equals(project.getLastVersion().getPrimarySpec())) {
			history.getTagSpecs().add(Versions.createTAG("HEAD", VersionSpec.GLOBAL)); //$NON-NLS-1$
		}
		for (final BranchInfo branch : project.getBranches()) {
			if (version.getPrimarySpec().equals(branch.getHead())) {
				history.getTagSpecs().add(Versions.createTAG("HEAD: " + branch.getName(), branch.getName())); //$NON-NLS-1$
			}
		}

		history.getTagSpecs().addAll(ModelUtil.clone(version.getTagSpecs()));
	}

	private List<PrimaryVersionSpec> addSpecs(List<Version> versions) {
		final ArrayList<PrimaryVersionSpec> result = new ArrayList<PrimaryVersionSpec>();
		for (final Version version : versions) {
			result.add(ModelUtil.clone(version.getPrimarySpec()));
		}
		return result;
	}

	/**
	 * Sorts versions based on the primary version spec.
	 *
	 * @author wesendon
	 *
	 */
	private final class VersionComparator implements Comparator<Version> {
		private final boolean asc;

		VersionComparator(boolean asc) {
			this.asc = asc;
		}

		public int compare(Version o1, Version o2) {
			final PrimaryVersionSpec v1 = o1.getPrimarySpec();
			final PrimaryVersionSpec v2 = o2.getPrimarySpec();
			if (v1 == null || v2 == null) {
				throw new IllegalStateException();
			}

			if (asc) {
				return v1.compareTo(v2);
			}

			return v1.compareTo(v2) * -1;
		}
	}
}
