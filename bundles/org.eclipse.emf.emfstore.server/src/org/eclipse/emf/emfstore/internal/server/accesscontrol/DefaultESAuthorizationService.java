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
package org.eclipse.emf.emfstore.internal.server.accesscontrol;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.internal.common.APIUtil;
import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.eclipse.emf.emfstore.internal.server.core.MonitorProvider;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.exceptions.SessionTimedOutException;
import org.eclipse.emf.emfstore.internal.server.model.ProjectHistory;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.ProjectAdminRole;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.Role;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.ServerAdmin;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESGroupImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESProjectHistoryImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESUserImpl;
import org.eclipse.emf.emfstore.server.auth.ESAuthorizationService;
import org.eclipse.emf.emfstore.server.auth.ESMethod.MethodId;
import org.eclipse.emf.emfstore.server.auth.ESMethodInvocation;
import org.eclipse.emf.emfstore.server.auth.ESOrgUnitResolver;
import org.eclipse.emf.emfstore.server.auth.ESProjectAdminPrivileges;
import org.eclipse.emf.emfstore.server.auth.ESSessions;
import org.eclipse.emf.emfstore.server.model.ESGlobalProjectId;
import org.eclipse.emf.emfstore.server.model.ESGroup;
import org.eclipse.emf.emfstore.server.model.ESOrgUnit;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitId;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider;
import org.eclipse.emf.emfstore.server.model.ESProjectHistory;
import org.eclipse.emf.emfstore.server.model.ESSessionId;
import org.eclipse.emf.emfstore.server.model.ESUser;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Default implementation of the {@link ESAuthorizationService}.
 *
 * @author emueller
 *
 */
public class DefaultESAuthorizationService implements ESAuthorizationService {

	/**
	 * Contains possible access levels.
	 */
	private enum AccessLevel {
		PROJECT_READ, PROJECT_WRITE, PROJECT_ADMIN, SERVER_ADMIN, NONE
	}

	private EnumMap<MethodId, AccessLevel> accessMap;
	private ESSessions sessions;
	private ESOrgUnitResolver orgUnitResolver;
	private ESOrgUnitProvider orgUnitProvider;

	private final Predicate<Role> isServerAdminPredicate = new HasRolePredicate(ServerAdmin.class);
	private final Predicate<Role> isProjectAdminPredicate = new HasRolePredicate(ProjectAdminRole.class);

	private void initAccessMap() {
		if (accessMap != null) {
			return;
		}
		accessMap = new EnumMap<MethodId, AccessLevel>(MethodId.class);

		addAccessMapping(AccessLevel.NONE,
			MethodId.GETVERSION);

		addAccessMapping(AccessLevel.PROJECT_READ,
			MethodId.GETPROJECT,
			MethodId.GETEMFPROPERTIES,
			MethodId.GETHISTORYINFO,
			MethodId.GETCHANGES,
			MethodId.RESOLVEVERSIONSPEC,
			MethodId.DOWNLOADFILECHUNK,
			MethodId.DOWNLOADCHANGEPACKAGEFRAGMENT);

		addAccessMapping(AccessLevel.PROJECT_WRITE,
			MethodId.SETEMFPROPERTIES,
			MethodId.TRANSMITPROPERTY,
			MethodId.UPLOADFILECHUNK,
			MethodId.CREATEVERSION,
			MethodId.UPLOADCHANGEPACKAGEFRAGMENT,
			MethodId.GETBRANCHES);

		addAccessMapping(AccessLevel.PROJECT_ADMIN,
			MethodId.DELETEPROJECT,
			MethodId.REMOVETAG,
			MethodId.ADDTAG);

		addAccessMapping(AccessLevel.SERVER_ADMIN,
			MethodId.IMPORTPROJECTHISTORYTOSERVER,
			MethodId.EXPORTPROJECTHISTORYFROMSERVER,
			MethodId.REGISTEREPACKAGE);

		// TODO: extract
		if (ServerConfiguration.isProjectAdminPrivileg(ESProjectAdminPrivileges.ShareProject)) {
			addAccessMapping(AccessLevel.PROJECT_ADMIN,
				MethodId.CREATEPROJECT,
				MethodId.CREATEEMPTYPROJECT);
		} else {
			addAccessMapping(AccessLevel.SERVER_ADMIN,
				MethodId.CREATEPROJECT,
				MethodId.CREATEEMPTYPROJECT);
		}

		addAccessMapping(AccessLevel.NONE, MethodId.GETPROJECTLIST, MethodId.RESOLVEUSER);
	}

	private void addAccessMapping(AccessLevel type, MethodId... operationTypes) {
		for (final MethodId opType : operationTypes) {
			accessMap.put(opType, type);
		}
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESAuthorizationService#checkProjectAdminAccess(org.eclipse.emf.emfstore.server.model.ESSessionId,
	 *      org.eclipse.emf.emfstore.server.model.ESGlobalProjectId)
	 */
	public boolean checkProjectAdminAccess(ESSessionId sessionId, ESGlobalProjectId globalProjectId)
		throws AccessControlException {

		checkSession(sessionId);

		final ACUser user = (ACUser) getOrgUnit(sessions.resolveToOrgUnitId(sessionId));
		final Iterable<Role> roles = getAllRoles(user.getId().toAPI());

		if (Iterables.any(roles, isServerAdminPredicate)) {
			return true;
		}

		final ProjectId projectId = APIUtil.toInternal(ProjectId.class, globalProjectId);

		for (final Role role : roles) {
			if (projectId == null && ProjectAdminRole.class.isInstance(role)) {
				return false;
			}

			if (role.canAdministrate(projectId)) {
				return false;
			}
		}

		throw new AccessControlException(Messages.AccessControlImpl_Insufficient_Rights);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESAuthorizationService#checkServerAdminAccess(org.eclipse.emf.emfstore.server.model.ESSessionId)
	 */
	public void checkServerAdminAccess(ESSessionId sessionId) throws AccessControlException {
		checkSession(sessionId);
		final ACUser user = (ACUser) getOrgUnit(sessions.resolveToOrgUnitId(sessionId));

		final List<Role> rolesFromGroups = APIUtil.toInternal(orgUnitResolver.getRolesFromGroups(user.toAPI()));
		final Iterable<Role> roles = Iterables.concat(user.getRoles(), rolesFromGroups);

		if (Iterables.any(roles, isServerAdminPredicate)) {
			return;
		}

		throw new AccessControlException(Messages.AccessControlImpl_Insufficient_Rights);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESAuthorizationService#checkProjectAdminAccessForOrgUnit(org.eclipse.emf.emfstore.server.model.ESSessionId,
	 *      org.eclipse.emf.emfstore.server.model.ESOrgUnitId)
	 */
	public boolean checkProjectAdminAccessForOrgUnit(ESSessionId sessionId, ESOrgUnitId orgUnitId)
		throws AccessControlException {

		checkSession(sessionId);
		cleanupPARole(orgUnitId);

		final List<Role> allRoles = getAllRoles(orgUnitId);
		final Set<ProjectId> involvedProjects = new LinkedHashSet<ProjectId>();
		final ACUser user = (ACUser) getOrgUnit(sessions.resolveToOrgUnitId(sessionId));
		final boolean hasServerAdminRole = Iterables.any(user.getRoles(), isServerAdminPredicate);

		for (final Role role : allRoles) {
			if ((isServerAdminPredicate.apply(role) || isProjectAdminPredicate.apply(role))
				&& !hasServerAdminRole) {
				throw new AccessControlException(Messages.AccessControlImpl_Not_Allowed_To_Remove_Other_Admin);
			}
			involvedProjects.addAll(role.getProjects());
		}

		final Set<ESGlobalProjectId> globalIds = new LinkedHashSet<ESGlobalProjectId>();

		for (final ProjectId projectId : involvedProjects) {
			globalIds.add(projectId.toAPI());
		}

		return checkProjectAdminAccessForOrgUnit(
			sessionId,
			orgUnitId,
			globalIds);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESAuthorizationService#checkProjectAdminAccessForOrgUnit(org.eclipse.emf.emfstore.server.model.ESSessionId,
	 *      org.eclipse.emf.emfstore.server.model.ESOrgUnitId, java.util.Set)
	 */
	public boolean checkProjectAdminAccessForOrgUnit(ESSessionId sessionId, ESOrgUnitId orgUnitId,
		Set<ESGlobalProjectId> projectIds)
		throws AccessControlException {

		checkSession(sessionId);
		cleanupPARole(orgUnitId);
		final ACUser user = (ACUser) getOrgUnit(sessions.resolveToOrgUnitId(sessionId));

		final List<Role> allRoles = getAllRoles(user.getId().toAPI());
		if (Iterables.any(allRoles, isServerAdminPredicate)) {
			return true;
		}

		try {
			final ProjectAdminRole projectAdminRole = (ProjectAdminRole) Iterables.find(
				user.getRoles(),
				isProjectAdminPredicate);

			final Set<ProjectId> ids = new LinkedHashSet<ProjectId>();
			for (final ESGlobalProjectId projectId : projectIds) {
				ids.add(APIUtil.toInternal(ProjectId.class, projectId));
			}

			// projectAdminRole can not be null
			if (projectAdminRole.getProjects().containsAll(ids)) {
				return false;
			}

		} catch (final NoSuchElementException e) {
			throw new AccessControlException(Messages.AccessControlImpl_Insufficient_Rights);
		}

		throw new AccessControlException(Messages.AccessControlImpl_Insufficient_Rights);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESAuthorizationService#checkProjectAdminAccess(org.eclipse.emf.emfstore.server.model.ESSessionId,
	 *      org.eclipse.emf.emfstore.server.model.ESGlobalProjectId,
	 *      org.eclipse.emf.emfstore.server.auth.ESProjectAdminPrivileges)
	 */
	// TODO: second parameter is optional
	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESAuthorizationService#checkProjectAdminAccess(org.eclipse.emf.emfstore.server.model.ESSessionId,
	 *      org.eclipse.emf.emfstore.server.model.ESGlobalProjectId,
	 *      org.eclipse.emf.emfstore.server.auth.ESProjectAdminPrivileges)
	 */
	public boolean checkProjectAdminAccess(ESSessionId sessionId, ESGlobalProjectId globalProjectId,
		ESProjectAdminPrivileges privileg) throws AccessControlException {

		checkSession(sessionId);
		final ACUser user = (ACUser) getOrgUnit(sessions.resolveToOrgUnitId(sessionId));
		final Iterable<Role> roles = getAllRoles(user.getId().toAPI());

		if (Iterables.any(roles, isServerAdminPredicate)) {
			return true;
		}

		for (final Role role : roles) {

			if (!ProjectAdminRole.class.isInstance(role)) {
				continue;
			}

			if (!ServerConfiguration.isProjectAdminPrivileg(privileg)) {
				throw new AccessControlException(Messages.AccessControlImpl_PARole_Missing_Privilege);
			}

			if (globalProjectId == null) {
				return false;
			}

			final ProjectAdminRole projectAdminRole = ProjectAdminRole.class.cast(role);
			final ProjectId projectId = APIUtil.toInternal(ProjectId.class, globalProjectId);

			if (!projectAdminRole.canAdministrate(projectId)) {
				throw new AccessControlException(Messages.AccessControlImpl_PARole_Missing_Privilege);
			}

			return false;
		}

		throw new AccessControlException(Messages.AccessControlImpl_Insufficient_Rights);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESAuthorizationService#checkReadAccess(org.eclipse.emf.emfstore.server.model.ESSessionId,
	 *      org.eclipse.emf.emfstore.server.model.ESGlobalProjectId, java.util.Set)
	 */
	public void checkReadAccess(ESSessionId sessionId, ESGlobalProjectId globalProjectId, Set<EObject> modelElements)
		throws AccessControlException {

		checkSession(sessionId);
		final ACUser user = (ACUser) getOrgUnit(sessions.resolveToOrgUnitId(sessionId));

		final List<Role> internalRoles = APIUtil.toInternal(orgUnitResolver.getRolesFromGroups(user.toAPI()));
		final Iterable<Role> roles = Iterables.concat(user.getRoles(), internalRoles);

		final ProjectId internalAPI = APIUtil.toInternal(ProjectId.class, globalProjectId);

		// MK: remove access control simplification
		if (!canRead(roles, internalAPI, null)) {
			throw new AccessControlException(Messages.AccessControlImpl_Insufficient_Rights);
			// for (ModelElement modelElement : modelElements) {
			// if (!canRead(roles, projectId, modelElement)) {
			// throw new AccessControlException();
			// }
		}
	}

	/**
	 * Check if the given list of roles can write to the model element in the
	 * project.
	 *
	 * @param roles
	 *            a list of roles
	 * @param projectId
	 *            a project id
	 * @param modelElement
	 *            a model element
	 * @return true if one of the roles can write
	 */
	private boolean canWrite(Iterable<Role> roles, ProjectId projectId, EObject modelElement) {
		for (final Role role : roles) {
			if (role.canModify(projectId, modelElement)
				|| role.canCreate(projectId, modelElement)
				|| role.canDelete(projectId, modelElement)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the given list of roles can read the model element in the
	 * project.
	 *
	 * @param roles
	 *            a list of roles
	 * @param projectId
	 *            a project id
	 * @param modelElement
	 *            a model element
	 * @return true if one of the roles can read
	 * @throws AccessControlException
	 */
	private boolean canRead(Iterable<Role> roles, ProjectId projectId, EObject modelElement)
		throws AccessControlException {

		for (final Role role : roles) {
			if (role.canRead(projectId, modelElement)) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESAuthorizationService#checkWriteAccess(org.eclipse.emf.emfstore.server.model.ESSessionId,
	 *      org.eclipse.emf.emfstore.server.model.ESGlobalProjectId, java.util.Set)
	 */
	public void checkWriteAccess(ESSessionId sessionId, ESGlobalProjectId globalProjectId, Set<EObject> modelElements)
		throws AccessControlException {
		checkSession(sessionId);

		final ACUser user = (ACUser) getOrgUnit(sessions.resolveToOrgUnitId(sessionId));
		final List<Role> internalRoles = APIUtil.toInternal(orgUnitResolver.getRolesFromGroups(user.toAPI()));
		final Iterable<Role> roles = Iterables.concat(user.getRoles(), internalRoles);

		// MK: remove access control simplification
		final ProjectId projectId = APIUtil.toInternal(ProjectId.class, globalProjectId);

		if (!canWrite(roles, projectId, null)) {
			throw new AccessControlException(Messages.AccessControlImpl_Insufficient_Rights);
		}
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESAuthorizationService#checkAccess(org.eclipse.emf.emfstore.server.auth.ESMethodInvocation)
	 */
	public void checkAccess(ESMethodInvocation op) throws AccessControlException {
		initAccessMap();
		final AccessLevel accessType = accessMap.get(op.getType());
		if (accessType == null) {
			// no access type means "no access"
			throw new AccessControlException(Messages.AccessControlImpl_No_Access);
		}
		switch (accessType) {
		case PROJECT_READ:
			ProjectId projectId = getProjectIdFromParameters(op);
			checkReadAccess(
				op.getSessionId(),
				projectId == null ? null : projectId.toAPI(),
				null);
			break;
		case PROJECT_WRITE:
			projectId = getProjectIdFromParameters(op);
			checkWriteAccess(
				op.getSessionId(),
				projectId == null ? null : projectId.toAPI(),
				null);
			break;
		case PROJECT_ADMIN:
			projectId = getProjectIdFromParameters(op);
			checkProjectAdminAccess(
				op.getSessionId(),
				projectId == null ? null : projectId.toAPI());
			break;
		case SERVER_ADMIN:
			checkServerAdminAccess(op.getSessionId());
			break;
		case NONE:
			break;
		default:
			throw new AccessControlException(Messages.AccessControlImpl_Unknown_Access_Type);
		}
	}

	private ProjectId getProjectIdFromParameters(ESMethodInvocation op) {
		for (final Object obj : op.getParameters()) {
			if (obj instanceof ProjectId) {
				return (ProjectId) obj;
			}
		}
		return null;
		// throw new IllegalArgumentException("the operation MUST have a project id");
	}

	/**
	 * Removes any orphan {@link ProjectId}s from the given {@link ProjectAdminRole}, i.e.
	 * all invalid {@link ProjectId}s will be removed.
	 *
	 * @param orgUnitId
	 *
	 * @param paRole the {@link ProjectAdminRole} to be cleaned up
	 *
	 * @throws AccessControlException in case the requested orgUnit does not exist
	 */
	private void cleanupPARole(ESOrgUnitId esOrgUnitId) throws AccessControlException {

		ProjectAdminRole paRole = null;
		// final ACOrgUnitId orgUnitId = ESOrgUnitIdImpl.class.cast(esOrgUnitId).toInternalAPI();
		final List<Role> roles = getAllRoles(esOrgUnitId);

		for (final Role role : roles) {
			if (ProjectAdminRole.class.isInstance(role)) {
				paRole = (ProjectAdminRole) role;
				break;
			}
		}

		if (paRole == null) {
			return;
		}

		final List<ESProjectHistory> externalProjects = orgUnitProvider.getProjects();
		final List<ProjectHistory> projects = new ArrayList<ProjectHistory>();
		for (final ESProjectHistory projectHistory : externalProjects) {
			projects.add(
				ESProjectHistoryImpl.class.cast(projectHistory).toInternalAPI());
		}

		final Set<ProjectId> validProjectIds = new LinkedHashSet<ProjectId>();
		final Set<ProjectId> invalidProjectIdsOfRole = new LinkedHashSet<ProjectId>();
		for (final ProjectHistory projectHistory : projects) {
			validProjectIds.add(projectHistory.getProjectId());
		}
		for (final ProjectId projectId : paRole.getProjects()) {
			if (!validProjectIds.contains(projectId)) {
				invalidProjectIdsOfRole.add(projectId);
			}
		}
		paRole.getProjects().removeAll(invalidProjectIdsOfRole);
		if (paRole.getProjects().size() == 0) {
			getOrgUnit(esOrgUnitId).getRoles().remove(paRole);
		}
	}

	private List<Role> getAllRoles(ESOrgUnitId orgUnitId) throws AccessControlException {

		final ACOrgUnit<?> internalOrgUnit = getOrgUnit(orgUnitId);
		final ESOrgUnit orgUnit = internalOrgUnit.toAPI();
		final List<ACGroup> groups = APIUtil.toInternal(orgUnitResolver.getGroups(orgUnit));
		final ArrayList<Role> roles = new ArrayList<Role>();
		for (final ACGroup group : groups) {
			roles.addAll(group.getRoles());
		}
		roles.addAll(internalOrgUnit.getRoles());
		return roles;
	}

	private ACOrgUnit<?> getOrgUnit(ESOrgUnitId orgUnitId) throws AccessControlException {

		Preconditions.checkNotNull(orgUnitId, "orgUnitId must not be null"); //$NON-NLS-1$
		final ACOrgUnitId internalId = APIUtil.toInternal(ACOrgUnitId.class, orgUnitId);

		synchronized (MonitorProvider.getInstance().getMonitor()) {
			for (final ESUser user : orgUnitProvider.getUsers()) {
				final ACUser internalAPI = (ACUser) ESUserImpl.class.cast(user).toInternalAPI();
				if (internalAPI.getId().equals(internalId)) {
					return internalAPI;
				}
			}
			for (final ESGroup group : orgUnitProvider.getGroups()) {
				final ACGroup internalAPI = (ACGroup) ESGroupImpl.class.cast(group).toInternalAPI();
				if (internalAPI.getId().equals(internalId)) {
					return internalAPI;
				}
			}
			throw new AccessControlException(Messages.AccessControlImpl_Given_OrgUnit_Does_Not_Exist);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESAuthorizationService#init(org.eclipse.emf.emfstore.server.auth.ESSessions,
	 *      org.eclipse.emf.emfstore.server.auth.ESOrgUnitResolver,
	 *      org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider)
	 */
	public void init(ESSessions sessions, ESOrgUnitResolver orgUnitResolverServive, ESOrgUnitProvider orgUnitProvider) {
		this.sessions = sessions;
		orgUnitResolver = orgUnitResolverServive;
		this.orgUnitProvider = orgUnitProvider;
	}

	private void checkSession(ESSessionId sessionId) throws SessionTimedOutException {
		sessions.isValid(sessionId);
	}
}
