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
package org.eclipse.emf.emfstore.internal.server.core;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.emfstore.internal.common.APIUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.Activator;
import org.eclipse.emf.emfstore.internal.server.AdminEmfStore;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.AccessControl;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.HasRolePredicate;
import org.eclipse.emf.emfstore.internal.server.connection.xmlrpc.util.ShareProjectAdapter;
import org.eclipse.emf.emfstore.internal.server.core.helper.ACHelper;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.exceptions.FatalESException;
import org.eclipse.emf.emfstore.internal.server.exceptions.InvalidInputException;
import org.eclipse.emf.emfstore.internal.server.exceptions.StorageException;
import org.eclipse.emf.emfstore.internal.server.model.ModelFactory;
import org.eclipse.emf.emfstore.internal.server.model.ProjectHistory;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.model.ProjectInfo;
import org.eclipse.emf.emfstore.internal.server.model.ServerSpace;
import org.eclipse.emf.emfstore.internal.server.model.SessionId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.AccesscontrolFactory;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.ProjectAdminRole;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.Role;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.RolesFactory;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.RolesPackage;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.ServerAdmin;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESGroupImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESUserImpl;
import org.eclipse.emf.emfstore.server.auth.ESAuthorizationService;
import org.eclipse.emf.emfstore.server.auth.ESPasswordHashGenerator;
import org.eclipse.emf.emfstore.server.auth.ESPasswordHashGenerator.ESHashAndSalt;
import org.eclipse.emf.emfstore.server.auth.ESProjectAdminPrivileges;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.model.ESGroup;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitId;
import org.eclipse.emf.emfstore.server.model.ESSessionId;
import org.eclipse.emf.emfstore.server.model.ESUser;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

/**
 * Implementation of {@link AdminEmfStore} interface.
 *
 * @author wesendon
 */
// TODO: bring this interface in new subinterface structure and refactor it
public class AdminEmfStoreImpl extends AbstractEmfstoreInterface implements AdminEmfStore {

	/**
	 * Default constructor.
	 *
	 * @param serverSpace
	 *            the server space
	 * @param accessControl
	 *            the authorization control
	 * @throws FatalESException
	 *             in case of failure
	 */
	public AdminEmfStoreImpl(ServerSpace serverSpace,
		AccessControl accessControl)
		throws FatalESException {
		super(serverSpace, accessControl);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ACGroup> getGroups(SessionId sessionId) throws ESException {
		checkForNulls(sessionId);

		checkProjectAdminAccess(sessionId);

		final List<ACGroup> result = new ArrayList<ACGroup>();
		for (final ACGroup group : getGroups()) {
			// quickfix
			final ACGroup copy = ModelUtil.clone(group);
			clearMembersFromGroup(copy);
			result.add(copy);
		}
		return removeInvisibleOrgUnits(result, sessionId.toAPI());
	}

	private List<ACGroup> getGroups() {
		final List<ACGroup> groups = new ArrayList<ACGroup>();
		for (final ESGroup group : getAccessControl().getOrgUnitProviderService().getGroups()) {
			groups.add((ACGroup) ESGroupImpl.class.cast(group).toInternalAPI());
		}
		return groups;
	}

	private List<ACUser> getUsers() {
		final List<ACUser> users = new ArrayList<ACUser>();
		for (final ESUser user : getAccessControl().getOrgUnitProviderService().getUsers()) {
			users.add((ACUser) ESUserImpl.class.cast(user).toInternalAPI());
		}
		return users;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ACGroup> getGroups(SessionId sessionId, ACOrgUnitId orgUnitId) throws ESException {
		checkForNulls(sessionId, orgUnitId);
		getAccessControl().getAuthorizationService().checkProjectAdminAccess(
			sessionId.toAPI(),
			null);
		final List<ACGroup> result = new ArrayList<ACGroup>();
		final ACOrgUnit<?> orgUnit = getOrgUnit(orgUnitId);
		for (final ACGroup group : getGroups()) {
			if (group.getMembers().contains(orgUnit)) {
				// quickfix
				final ACGroup copy = ModelUtil.clone(group);
				clearMembersFromGroup(copy);
				result.add(copy);
			}
		}
		return removeInvisibleOrgUnits(result, sessionId.toAPI());
	}

	/**
	 * {@inheritDoc}
	 */
	public ACOrgUnitId createGroup(SessionId sessionId, String name) throws ESException {

		checkForNulls(sessionId, name);
		getAccessControl().getAuthorizationService().checkProjectAdminAccess(
			sessionId.toAPI(),
			null,
			ESProjectAdminPrivileges.CreateGroup);

		if (groupExists(name)) {
			log(sessionId, MessageFormat.format("Failed to create group ''{0}''. It already exists.", name)); //$NON-NLS-1$
			throw new InvalidInputException(Messages.AdminEmfStoreImpl_Group_Already_Exists);
		}

		final ACGroup acGroup = AccesscontrolFactory.eINSTANCE.createACGroup();
		acGroup.setName(name);
		acGroup.setDescription(StringUtils.EMPTY);
		final Optional<ACUser> parent = getUserFromSessionId(sessionId);
		if (parent.isPresent()) {
			acGroup.setCreatedBy(parent.get().getId().getId());
		}
		getAccessControl().getOrgUnitProviderService().addGroup(acGroup.toAPI());
		save();
		log(sessionId, MessageFormat.format("Created group ''{0}''", name)); //$NON-NLS-1$
		return ModelUtil.clone(acGroup.getId());
	}

	private boolean groupExists(String name) {
		for (final ACGroup group : getGroups()) {
			if (group.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeGroup(SessionId sessionId, ACOrgUnitId user, ACOrgUnitId group) throws ESException {

		checkForNulls(sessionId, user, group);

		final boolean isServerAdmin = getAccessControl().getAuthorizationService().checkProjectAdminAccess(
			sessionId.toAPI(),
			null,
			ESProjectAdminPrivileges.DeleteOrgUnit);

		if (!isServerAdmin) {
			getAccessControl().getAuthorizationService().checkProjectAdminAccessForOrgUnit(
				sessionId.toAPI(),
				group.toAPI());
		}

		final ACGroup acGroup = getGroup(group);
		final ACOrgUnit<?> acUser = getOrgUnit(user);
		acGroup.getMembers().remove(acUser);
		save();
		log(sessionId,
			MessageFormat.format("Removed user ''{0}'' from group ''{1}''", acUser.getName(), acGroup.getName())); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	public void deleteGroup(SessionId sessionId, ACOrgUnitId groupId) throws ESException {

		checkForNulls(sessionId, groupId);

		final ESAuthorizationService authorizationService = getAccessControl().getAuthorizationService();

		authorizationService.checkProjectAdminAccess(
			sessionId.toAPI(),
			null,
			ESProjectAdminPrivileges.DeleteOrgUnit);
		authorizationService.checkProjectAdminAccessForOrgUnit(
			sessionId.toAPI(),
			groupId.toAPI());

		// also check all members
		final ACGroup group = getGroup(groupId);
		for (final ACOrgUnit<?> member : group.getMembers()) {
			authorizationService.checkProjectAdminAccessForOrgUnit(
				sessionId.toAPI(),
				member.getId().toAPI());
		}

		for (final Iterator<ACGroup> iter = getGroups().iterator(); iter.hasNext();) {
			final ACGroup nextGroup = iter.next();
			final List<ACGroup> groups = getGroups(sessionId, groupId);
			if (nextGroup.getId().equals(groupId)) {
				for (final ACGroup acGroup : groups) {
					removeMember(sessionId, acGroup.getId(), nextGroup.getId());
				}
				getAccessControl().getOrgUnitProviderService().removeGroup(nextGroup.toAPI());
				EcoreUtil.delete(nextGroup);
				save();
				log(sessionId, MessageFormat.format("Deleted group ''{0}''.", group.getName())); //$NON-NLS-1$
				return;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("rawtypes")
	public List<ACOrgUnit> getMembers(SessionId sessionId, ACOrgUnitId groupId) throws ESException {

		checkForNulls(sessionId, groupId);

		getAccessControl().getAuthorizationService().checkProjectAdminAccess(
			sessionId.toAPI(),
			null);

		// quickfix
		final List<ACOrgUnit> result = new ArrayList<ACOrgUnit>();
		for (final ACOrgUnit orgUnit : getGroup(groupId).getMembers()) {
			result.add(ModelUtil.clone(orgUnit));
		}
		clearMembersFromGroups(result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addMember(SessionId sessionId, ACOrgUnitId groupId, ACOrgUnitId member) throws ESException {

		checkForNulls(sessionId, groupId, member);

		final ESAuthorizationService authorizationService = getAccessControl().getAuthorizationService();

		final boolean isServerAdmin = authorizationService.checkProjectAdminAccess(
			sessionId.toAPI(),
			null,
			ESProjectAdminPrivileges.ChangeAssignmentsOfOrgUnits);

		if (!isServerAdmin) {
			authorizationService.checkProjectAdminAccessForOrgUnit(
				sessionId.toAPI(),
				groupId.toAPI());
		}

		addToGroup(groupId, member);
		log(sessionId,
			MessageFormat.format("Added member ''{0}'' to group ''{1}''", getOrgUnit(member).getName(), //$NON-NLS-1$
				getGroup(groupId).getName()));
	}

	private void addToGroup(ACOrgUnitId group, ACOrgUnitId member) throws ESException {
		final ACGroup acGroup = getGroup(group);
		final ACOrgUnit<?> acMember = getOrgUnit(member);
		acGroup.getMembers().add(acMember);
		save();
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeMember(SessionId sessionId, ACOrgUnitId group, ACOrgUnitId member) throws ESException {

		checkForNulls(sessionId, group, member);

		final ESAuthorizationService authorizationService = getAccessControl().getAuthorizationService();

		final boolean isServerAdmin = authorizationService.checkProjectAdminAccess(
			sessionId.toAPI(),
			null,
			ESProjectAdminPrivileges.ChangeAssignmentsOfOrgUnits);

		if (!isServerAdmin) {
			authorizationService.checkProjectAdminAccessForOrgUnit(
				sessionId.toAPI(),
				group.toAPI());
		}

		removeFromGroup(group, member);
		log(sessionId,
			MessageFormat.format("Removed member ''{0}'' from group ''{1}''", getOrgUnit(member).getName(), //$NON-NLS-1$
				getGroup(group).getName()));
	}

	private void removeFromGroup(ACOrgUnitId group, ACOrgUnitId member) throws ESException {
		final ACGroup acGroup = getGroup(group);
		final ACOrgUnit<?> acMember = getOrgUnit(member);
		if (acGroup.getMembers().contains(acMember)) {
			acGroup.getMembers().remove(acMember);
			save();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("rawtypes")
	public List<ACOrgUnit> getParticipants(SessionId sessionId, ProjectId projectId) throws ESException {

		checkForNulls(sessionId);
		checkProjectAdminAccess(sessionId, projectId);

		final List<ACOrgUnit> result = new ArrayList<ACOrgUnit>();

		for (final ACOrgUnit<ESUser> orgUnit : getUsers()) {
			final List<Role> roles = orgUnit.getRoles();
			for (final Role role : roles) {
				if (isServerAdmin(role) || role.getProjects().contains(projectId)) {
					result.add(ModelUtil.clone(orgUnit));
					break;
				}
			}
		}

		for (final ACOrgUnit<ESGroup> orgUnit : getGroups()) {
			final List<Role> roles = orgUnit.getRoles();
			for (final Role role : roles) {
				if (isServerAdmin(role) || role.getProjects().contains(projectId)) {
					result.add(ModelUtil.clone(orgUnit));
					break;
				}
			}
		}

		// quickfix
		clearMembersFromGroups(result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addParticipant(SessionId sessionId, ProjectId projectId, ACOrgUnitId participantId, EClass roleClass)
		throws ESException {

		checkForNulls(sessionId, projectId, participantId, roleClass);

		final boolean isServerAdmin = getAccessControl().getAuthorizationService()
			.checkProjectAdminAccess(
				sessionId.toAPI(),
				projectId.toAPI(),
				ESProjectAdminPrivileges.AssignRoleToOrgUnit);

		if (!isServerAdmin && roleClass.equals(RolesPackage.eINSTANCE.getServerAdmin())) {
			throw new AccessControlException(
				Messages.AdminEmfStoreImpl_Not_Allowed_To_Create_Participant_With_ServerAdminRole);
		}

		projectId = getProjectId(projectId);
		final ACOrgUnit<?> orgUnit = getOrgUnit(participantId);
		final List<Role> roles = orgUnit.getRoles();
		for (final Role role : roles) {
			if (role.getProjects().contains(projectId)) {
				return;
			}
		}
		// check whether role exists
		final List<Role> roles2 = orgUnit.getRoles();
		for (final Role role : roles2) {
			if (areEqual(role, roleClass)) {
				role.getProjects().add(ModelUtil.clone(projectId));
				save();
				log(sessionId,
					MessageFormat.format("Added participant ''{0}'' with role ''{1}'' to project ''{2}'' (ID: {3})", //$NON-NLS-1$
						getOrgUnit(participantId).getName(), roleClass.getName(), getProjectName(projectId),
						projectId.getId()));
				return;
			}
		}

		final Role newRole = createRoleFromEClass(roleClass);

		newRole.getProjects().add(ModelUtil.clone(projectId));
		orgUnit.getRoles().add(newRole);
		save();
		log(sessionId,
			MessageFormat.format("Added participant ''{0}'' with role ''{1}'' to project ''{2}'' (ID: {3})", //$NON-NLS-1$
				getOrgUnit(participantId).getName(), roleClass.getName(), getProjectName(projectId),
				projectId.getId()));
	}

	/**
	 * {@inheritDoc}
	 */
	public void addInitialParticipant(SessionId sessionId, ProjectId projectId, ACOrgUnitId participantId,
		EClass roleClass) throws ESException {

		checkForNulls(sessionId, projectId, participantId, roleClass);
		checkSession(sessionId);

		// check if requested role is the server administrator role, which we never allow to be assigned via this call
		if (isServerAdminRole(roleClass)) {
			throw new AccessControlException(Messages.AdminEmfStoreImpl_Not_Allowed_To_Assign_ServerAdminRole);
		}

		final SessionId session = resolveSessionById(sessionId.getId());
		final ACUser resolvedUser = resolveUserBySessionId(session.getId());

		if (!resolvedUser.getId().equals(participantId)) {
			throw new AccessControlException(Messages.AdminEmfStoreImpl_OnlyAllowedForRequstingUser);
		}

		// Checks if the user is a project administrator and whether the ShareProject privilege
		// has been set in the es.properties. This method will throw an exception
		// if the user is either not a project administrator or the ShareProject privilege has not been set.
		checkProjectAdminAccess(session, ESProjectAdminPrivileges.ShareProject);

		// check if requesting session did actually share a project before
		checkIfSessionIsAssociatedWithProject(session, projectId);

		projectId = getProjectId(projectId);
		final ACOrgUnit<?> orgUnit = getOrgUnit(participantId);

		for (final Role role : orgUnit.getRoles()) {
			if (areEqual(role, roleClass)) {
				role.getProjects().add(ModelUtil.clone(projectId));
				save();
				log(sessionId,
					MessageFormat.format(
						"Added initial participant ''{0}'' with role ''{1}'' to project ''{2}'' (ID: {3})", //$NON-NLS-1$
						orgUnit.getName(), roleClass.getName(), getProjectName(projectId), projectId.getId()));
				return;
			}
		}

		// If we get until here, the user has the privileges to create the project and be the initial participant.
		// Because the corresponding role was not found in the user''s roles, it must be part of one of the user''s
		// groups. To avoid the whole group becoming participants of the project, create the role for the user and add
		// the project id.
		final Role newRole = createRoleFromEClass(roleClass);

		newRole.getProjects().add(ModelUtil.clone(projectId));
		orgUnit.getRoles().add(newRole);
		save();
		log(sessionId,
			MessageFormat.format("Added initial participant ''{0}'' with role ''{1}'' to project ''{2}'' (ID: {3})", //$NON-NLS-1$
				orgUnit.getName(), roleClass.getName(), getProjectName(projectId), projectId.getId()));
	}

	private static void checkIfSessionIsAssociatedWithProject(SessionId sessionId, ProjectId projectId)
		throws AccessControlException {

		final EList<Adapter> eAdapters = sessionId.eAdapters();
		for (final Adapter adapter : eAdapters) {
			if (ShareProjectAdapter.class.isInstance(adapter)) {
				final ShareProjectAdapter shareAdapter = (ShareProjectAdapter) adapter;
				final boolean didRemove = shareAdapter.removeProject(projectId);
				if (didRemove) {
					return;
				}
				throw new AccessControlException(Messages.AdminEmfStoreImpl_IllegalRequestToAddInitialRole);
			}
		}

		// no ShareProjectAdapter with the correct project found
		throw new AccessControlException(Messages.AdminEmfStoreImpl_IllegalRequestToAddInitialRole);
	}

	private Role createRoleFromEClass(EClass roleClass) {
		return (Role) RolesPackage.eINSTANCE.getEFactoryInstance().create(
			(EClass) RolesPackage.eINSTANCE.getEClassifier(roleClass.getName()));
	}

	private ProjectId getProjectId(ProjectId projectId) throws ESException {
		for (final ProjectHistory projectHistory : getServerSpace().getProjects()) {
			if (projectHistory.getProjectId().equals(projectId)) {
				return projectHistory.getProjectId();
			}
		}
		throw new ESException(Messages.AdminEmfStoreImpl_Unknown_ProjectID);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeParticipant(SessionId sessionId, ProjectId projectId, ACOrgUnitId participantId)
		throws ESException {
		checkForNulls(sessionId, projectId, participantId);

		final boolean isServerAdmin = getAccessControl().getAuthorizationService().checkProjectAdminAccess(
			sessionId.toAPI(),
			projectId.toAPI(),
			ESProjectAdminPrivileges.AssignRoleToOrgUnit);

		final ACOrgUnit<?> orgUnit = getOrgUnit(participantId);
		projectId = getProjectId(projectId);

		final List<Role> roles = orgUnit.getRoles();
		for (final Role role : roles) {
			if (role.getProjects().contains(projectId)) {
				if (!isServerAdmin && role.canAdministrate(projectId)) {
					throw new AccessControlException(Messages.AdminEmfStoreImpl_RemovePA_Violation_1
						+ Messages.AdminEmfStoreImpl_RemovePA_Violation_2);
				}
				role.getProjects().remove(projectId);
				save();
				log(sessionId, MessageFormat.format("Removed participant ''{0}'' from project ''{1}'' (ID: {2})", //$NON-NLS-1$
					orgUnit.getName(), getProjectName(projectId), projectId.getId()));
				return;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Role getRole(SessionId sessionId, ProjectId projectId, ACOrgUnitId orgUnitId) throws ESException {

		checkForNulls(sessionId, projectId, orgUnitId);
		getAccessControl().getAuthorizationService().checkProjectAdminAccess(
			sessionId.toAPI(),
			projectId.toAPI());
		projectId = getProjectId(projectId);

		final ACOrgUnit<?> oUnit = getOrgUnit(orgUnitId);
		final List<Role> roles = oUnit.getRoles();
		Role nonServerAdminRole = null;
		for (final Role role : roles) {
			if (isServerAdmin(role)) {
				return role;
			}
			if (nonServerAdminRole == null && role.getProjects().contains(projectId)) {
				nonServerAdminRole = role;
			}
		}
		if (nonServerAdminRole != null) {
			return nonServerAdminRole;
		}
		throw new ESException(Messages.AdminEmfStoreImpl_Could_Not_Find_OrgUnit);
	}

	/**
	 * {@inheritDoc}
	 */
	public void changeRole(final SessionId sessionId, final ProjectId projectId, final ACOrgUnitId orgUnitId,
		final EClass roleClass) throws ESException {

		checkForNulls(sessionId, projectId, orgUnitId, roleClass);
		checkSession(sessionId);

		final SessionId session = resolveSessionById(sessionId.getId());

		checkProjectAdminAccess(session, projectId, ESProjectAdminPrivileges.AssignRoleToOrgUnit);
		final boolean isServerAdmin = checkProjectAdminAccessForOrgUnit(session, projectId, orgUnitId);

		// trying to assign server administrator role although caller has no server role
		if (!isServerAdmin && isServerAdminRole(roleClass)) {
			throw new AccessControlException(Messages.AdminEmfStoreImpl_Not_Allowed_To_Assign_ServerAdminRole);
		}

		final ProjectId resolvedProjectId = getProjectId(projectId);
		final ACOrgUnit<?> orgUnit = getOrgUnit(orgUnitId);

		// if user was server admin, remove this role
		// there may be more roles for this project, e.g. ProjectAdmin in case the project
		// was created by this user
		final Role serverAdminRole = getServerAdminRole(orgUnit);
		removeRole(isServerAdmin, resolvedProjectId, orgUnit, serverAdminRole);

		// remove old role first
		final Role role = getProjectRole(resolvedProjectId, orgUnit);
		removeRole(isServerAdmin, resolvedProjectId, orgUnit, role);

		if (isServerAdminRole(roleClass)) {
			orgUnit.getRoles().add(RolesFactory.eINSTANCE.createServerAdmin());
			save();
			log(sessionId, MessageFormat.format(
				"Changed role of OrgUnit ''{0}'' in project ''{1}'' (ID: {2}) to ''{3}''.", orgUnit.getName(), //$NON-NLS-1$
				getProjectName(resolvedProjectId), resolvedProjectId.getId(), roleClass.getName()));
			return;
		}

		// add project to role if it exists
		final List<Role> roles = orgUnit.getRoles();
		for (final Role r : roles) {
			if (r.eClass().getName().equals(roleClass.getName())) {
				r.getProjects().add(ModelUtil.clone(resolvedProjectId));
				save();
				log(sessionId, MessageFormat.format(
					"Changed role of OrgUnit ''{0}'' in project ''{1}'' (ID: {2}) to ''{3}''.", orgUnit.getName(), //$NON-NLS-1$
					getProjectName(resolvedProjectId), resolvedProjectId.getId(), roleClass.getName()));
				return;
			}
		}

		// create role if does not exists
		final Role newRole = createRoleFromEClass(roleClass);
		newRole.getProjects().add(ModelUtil.clone(resolvedProjectId));
		orgUnit.getRoles().add(newRole);
		save();
		log(sessionId, MessageFormat.format(
			"Changed role of OrgUnit ''{0}'' in project ''{1}'' (ID: {2}) to ''{3}''.", orgUnit.getName(), //$NON-NLS-1$
			getProjectName(resolvedProjectId), resolvedProjectId.getId(), roleClass.getName()));
	}

	protected void removeRole(final boolean isServerAdmin, final ProjectId resolvedProjectId,
		final ACOrgUnit<?> orgUnit, final Role role) throws AccessControlException {
		if (role != null) {

			if (!isServerAdmin && role.canAdministrate(resolvedProjectId)) {
				throw new AccessControlException(
					Messages.AdminEmfStoreImpl_RemovePA_Violation_1
						+ Messages.AdminEmfStoreImpl_RemovePA_Violation_2);
			}

			role.getProjects().remove(resolvedProjectId);
			if (role.getProjects().isEmpty()) {
				orgUnit.getRoles().remove(role);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void assignRole(SessionId sessionId, ACOrgUnitId orgUnitId, EClass roleClass)
		throws ESException {

		checkForNulls(sessionId, orgUnitId, roleClass);

		final ESAuthorizationService authorizationService = getAccessControl().getAuthorizationService();
		authorizationService.checkProjectAdminAccess(
			sessionId.toAPI(),
			null,
			ESProjectAdminPrivileges.AssignRoleToOrgUnit);
		final boolean isServerAdmin = authorizationService.checkProjectAdminAccessForOrgUnit(
			sessionId.toAPI(),
			orgUnitId.toAPI());

		if (!isServerAdmin && isServerAdminRole(roleClass)) {
			throw new AccessControlException("A project admin is not allowed to assign a server admin role"); //$NON-NLS-1$
		}

		final ACOrgUnit<?> orgUnit = getOrgUnit(orgUnitId);

		// check if org unit alrady has role
		final List<Role> roles = orgUnit.getRoles();
		for (final Role role : roles) {
			if (areEqual(role, roleClass)) {
				return;
			}
		}

		final Role newRole = createRoleFromEClass(roleClass);

		orgUnit.getRoles().add(newRole);
		save();
		log(sessionId,
			MessageFormat.format("Assigned role ''{0}'' to OrgUnit ''{1}''.", roleClass.getName(), orgUnit.getName())); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ACUser> getUsers(SessionId sessionId) throws ESException {
		checkForNulls(sessionId);
		getAccessControl().getAuthorizationService().checkProjectAdminAccess(
			sessionId.toAPI(),
			null);
		final List<ACUser> result = new ArrayList<ACUser>();
		for (final ACUser user : getUsers()) {
			result.add(user);
		}
		return removeInvisibleOrgUnits(result, sessionId.toAPI());
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("rawtypes")
	public List<ACOrgUnit> getOrgUnits(SessionId sessionId) throws ESException {
		checkForNulls(sessionId);
		checkSession(sessionId);

		checkProjectAdminAccess(sessionId);

		final List<ACOrgUnit> result = new ArrayList<ACOrgUnit>();

		for (final ACOrgUnit<ESUser> user : getUsers()) {
			result.add(ModelUtil.clone(user));
		}

		for (final ACOrgUnit<ESGroup> group : getGroups()) {
			result.add(ModelUtil.clone(group));
		}

		// quickfix
		clearMembersFromGroups(result);
		return removeInvisibleOrgUnits(result, sessionId.toAPI());
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ProjectInfo> getProjectInfos(SessionId sessionId) throws ESException {
		checkForNulls(sessionId);
		final List<ProjectInfo> result = new ArrayList<ProjectInfo>();
		for (final ProjectHistory projectHistory : getServerSpace().getProjects()) {
			try {
				getAccessControl().getAuthorizationService().checkProjectAdminAccess(
					sessionId.toAPI(),
					projectHistory.getProjectId().toAPI());
				result.add(getProjectInfo(projectHistory));
			} catch (final AccessControlException ace) {
				// ignore
			}
		}
		return result;
	}

	private String getProjectName(ProjectId projectId) {
		for (final ProjectHistory projectHistory : getServerSpace().getProjects()) {
			if (projectId.equals(projectHistory.getProjectId())) {
				return projectHistory.getProjectName();
			}
		}
		return "<unknown>"; //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	public ACOrgUnitId createUser(SessionId sessionId, String name) throws ESException {
		checkForNulls(sessionId, name);
		getAccessControl().getAuthorizationService().checkProjectAdminAccess(
			sessionId.toAPI(),
			null,
			ESProjectAdminPrivileges.CreateUser);

		if (userExists(name)) {
			throw new InvalidInputException("Username ''" + name + "'' already exists."); //$NON-NLS-1$ //$NON-NLS-2$
		}
		final ACUser acUser = AccesscontrolFactory.eINSTANCE.createACUser();
		acUser.setName(name);
		acUser.setDescription(StringUtils.EMPTY);
		final Optional<ACUser> parent = getUserFromSessionId(sessionId);
		if (parent.isPresent()) {
			acUser.setCreatedBy(parent.get().getId().getId());
		}
		getAccessControl().getOrgUnitProviderService().addUser(acUser.toAPI());
		save();
		log(sessionId,
			MessageFormat.format("Created new user with name ''{0}'' and ID ''{1}''", name, acUser.getId().getId())); //$NON-NLS-1$
		return ModelUtil.clone(acUser.getId());
	}

	private Optional<ACUser> getUserFromSessionId(SessionId sessionId) {
		try {
			final ESOrgUnitId orgUnitId = getAccessControl().getSessions().resolveToOrgUnitId(sessionId.toAPI());
			if (orgUnitId == null) {
				return Optional.absent();
			}
			final ACOrgUnitId internalId = APIUtil.toInternal(ACOrgUnitId.class, orgUnitId);
			for (final ESUser user : getAccessControl().getOrgUnitProviderService().getUsers()) {
				final ACUser internalAPI = (ACUser) ESUserImpl.class.cast(user).toInternalAPI();
				if (internalAPI.getId().equals(internalId)) {
					return Optional.of(internalAPI);
				}
			}
			return Optional.absent();
		} catch (final AccessControlException ex) {
			return Optional.absent();
		}
	}

	private boolean userExists(String name) {
		for (final ACUser user : getUsers()) {
			if (user.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void deleteUser(SessionId sessionId, ACOrgUnitId userId) throws ESException {
		checkForNulls(sessionId, userId);
		final ESAuthorizationService authorizationService = getAccessControl().getAuthorizationService();
		authorizationService.checkProjectAdminAccessForOrgUnit(
			sessionId.toAPI(),
			userId.toAPI());
		authorizationService.checkProjectAdminAccess(
			sessionId.toAPI(),
			null,
			ESProjectAdminPrivileges.DeleteOrgUnit);
		ACUser userToDelete = null;
		for (final Iterator<ACUser> iter = getUsers().iterator(); iter.hasNext();) {
			final ACUser user = iter.next();

			/* check if we were created by the deleted user */
			if (user.getCreatedBy() != null && user.getCreatedBy().equals(userId.getId())) {
				user.setCreatedBy(null);
			}

			/* check if we are the deleted user */
			if (user.getId().equals(userId)) {
				userToDelete = user;
			}
		}
		for (final ACGroup group : getGroups()) {
			if (group.getCreatedBy() != null && group.getCreatedBy().equals(userId.getId())) {
				group.setCreatedBy(null);
			}
		}
		/* perform deletion */
		if (userToDelete != null) {
			final List<ACGroup> groups = getGroups(sessionId, userId);
			for (final ACGroup acGroup : groups) {
				removeMember(sessionId, acGroup.getId(), userId);
			}
			getAccessControl().getOrgUnitProviderService().removeUser(userToDelete.toAPI());
			// TODO: move ecore delete into ServerSpace#deleteUser implementation
			EcoreUtil.delete(userToDelete);
			save();
			log(sessionId, MessageFormat.format("Deleted user ''{0}''", userToDelete.getName())); //$NON-NLS-1$
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void changeOrgUnit(SessionId sessionId, ACOrgUnitId orgUnitId, String name, String description)
		throws ESException {
		checkForNulls(sessionId, orgUnitId, name, description);
		getAccessControl().getAuthorizationService().checkProjectAdminAccessForOrgUnit(
			sessionId.toAPI(),
			orgUnitId.toAPI());
		final ACOrgUnit<?> orgUnit = getOrgUnit(orgUnitId);
		final String oldName = orgUnit.getName();
		orgUnit.setName(name);
		orgUnit.setDescription(description);
		save();
		log(sessionId,
			MessageFormat.format("Renamed OrgUnit ''{0}'' to ''{1}'' and updated its description.", oldName, name)); //$NON-NLS-1$
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.AdminEmfStore#changeUser(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId, java.lang.String,
	 *      java.lang.String)
	 */
	public void changeUser(SessionId sessionId, ACOrgUnitId userId, String name, String password) throws ESException {

		checkForNulls(sessionId, userId, name, password);
		checkSession(sessionId);

		final ESAuthorizationService authorizationService = getAccessControl().getAuthorizationService();
		final ESSessionId resolvedSession = getAccessControl().getSessions().resolveSessionById(sessionId.getId());
		final SessionId session = APIUtil.toInternal(SessionId.class, resolvedSession);

		final ACOrgUnit<?> orgUnit = getOrgUnit(userId);
		final ACUser requestingUser = resolveUserBySessionId(sessionId.getId());

		if (orgUnit.equals(requestingUser)) {
			updateUser(sessionId, userId, name, password);
			return;
		}

		final boolean isServerAdmin = checkProjectAdminAccess(session, ESProjectAdminPrivileges.ChangeUserPassword);

		if (!isServerAdmin) {
			authorizationService.checkProjectAdminAccessForOrgUnit(
				session.toAPI(),
				userId.toAPI());
		}

		updateUser(sessionId, userId, name, password);

	}

	private void updateUser(SessionId sessionId, ACOrgUnitId userId, String name, String password) throws ESException {

		final ACUser user = (ACUser) getOrgUnit(userId);
		final ESPasswordHashGenerator passwordHashGenerator = AccessControl.getESPasswordHashGenerator();

		final String oldName = user.getName();
		if (!checkUserNameChanged(oldName, name) && user.getPassword() != null) {
			/*
			 * when the user name does not change only the password is updated
			 * -> check if password is really changed
			 */
			final int separatorIndex = user.getPassword().indexOf(ESHashAndSalt.SEPARATOR);
			final String hash = user.getPassword().substring(0, separatorIndex);
			final String salt = user.getPassword().substring(separatorIndex + 1);
			if (passwordHashGenerator.verifyPassword(password, hash, salt)) {
				/* no change */
				throw new ESException(Messages.AdminEmfStoreImpl_SamePassword);
			}
		}

		user.setName(name);
		final ESHashAndSalt hashAndSalt = passwordHashGenerator.hashPassword(password);
		user.setPassword(hashAndSalt.getHash() + ESHashAndSalt.SEPARATOR + hashAndSalt.getSalt());
		save();
		if (checkUserNameChanged(oldName, name)) {
			log(sessionId,
				MessageFormat.format("Renamed user ''{0}'' to ''{1}'' and updated their password.", oldName, name)); //$NON-NLS-1$
		} else {
			log(sessionId, MessageFormat.format("Updated password of user ''{0}''.", name)); //$NON-NLS-1$
		}
	}

	private boolean checkUserNameChanged(String oldName, String newName) {
		if (oldName == null) {
			return newName != null;
		}
		return !oldName.equals(newName);
	}

	/**
	 * {@inheritDoc}
	 */
	public ACOrgUnit<?> getOrgUnit(SessionId sessionId, ACOrgUnitId orgUnitId) throws ESException {
		checkForNulls(sessionId, orgUnitId);
		getAccessControl().getAuthorizationService().checkProjectAdminAccess(
			sessionId.toAPI(),
			null);
		// quickfix
		final ACOrgUnit<?> orgUnit = ModelUtil.clone(getOrgUnit(orgUnitId));
		clearMembersFromGroup(orgUnit);
		return orgUnit;
	}

	/**
	 * This method is used as fix for the containment issue of group.
	 */
	@SuppressWarnings("rawtypes")
	private void clearMembersFromGroups(Collection<ACOrgUnit> orgUnits) {
		for (final ACOrgUnit orgUnit : orgUnits) {
			clearMembersFromGroup(orgUnit);
		}
	}

	/**
	 * This method is used as fix for the containment issue of group.
	 */
	@SuppressWarnings("rawtypes")
	private void clearMembersFromGroup(ACOrgUnit orgUnit) {
		if (orgUnit instanceof ACGroup) {
			((ACGroup) orgUnit).getMembers().clear();
		}
	}

	private boolean isServerAdmin(Role role) {
		return role.eClass().getName().equals(RolesPackage.Literals.SERVER_ADMIN.getName());
	}

	private boolean isServerAdminRole(EClass role) {
		return role.getName().equals(RolesPackage.Literals.SERVER_ADMIN.getName());
	}

	private boolean areEqual(Role role, EClass roleClass) {
		return role.eClass().getName().equals(roleClass.getName());
	}

	private ProjectInfo getProjectInfo(ProjectHistory project) {
		final ProjectInfo info = ModelFactory.eINSTANCE.createProjectInfo();
		info.setName(project.getProjectName());
		info.setDescription(project.getProjectDescription());
		info.setProjectId(ModelUtil.clone(project.getProjectId()));
		info.setVersion(project.getLastVersion().getPrimarySpec());
		return info;
	}

	private ACGroup getGroup(ACOrgUnitId orgUnitId) throws ESException {
		for (final ACGroup group : getGroups()) {
			if (group.getId().equals(orgUnitId)) {
				return group;
			}
		}
		throw new ESException(Messages.AdminEmfStoreImpl_Group_Does_Not_Exist);
	}

	private ACOrgUnit<?> getOrgUnit(ACOrgUnitId orgUnitId) throws ESException {
		for (final ACOrgUnit<ESUser> unit : getUsers()) {
			if (unit.getId().equals(orgUnitId)) {
				return unit;
			}
		}
		for (final ACOrgUnit<ESGroup> unit : getGroups()) {
			if (unit.getId().equals(orgUnitId)) {
				return unit;
			}
		}
		throw new ESException(Messages.AdminEmfStoreImpl_OrgUnit_Does_Not_Exist);
	}

	private Role getServerAdminRole(ACOrgUnit<?> orgUnit) {
		final List<Role> roles = orgUnit.getRoles();
		for (final Role role : roles) {
			if (isServerAdmin(role)) {
				return role;
			}
		}
		return null;
	}

	private Role getProjectRole(ProjectId projectId, ACOrgUnit<?> orgUnit) {
		final List<Role> roles = orgUnit.getRoles();
		for (final Role role : roles) {
			if (role.getProjects().contains(projectId)) {
				return role;
			}
		}
		return null;
	}

	private void save() throws ESException {
		try {
			getAccessControl().getOrgUnitProviderService().save();
		} catch (final IOException e) {
			throw new StorageException(StorageException.NOSAVE, e);
		} catch (final NullPointerException e) {
			throw new StorageException(StorageException.NOSAVE, e);
		}
	}

	private void checkForNulls(Object... objects) throws InvalidInputException {
		for (final Object obj : objects) {
			if (obj == null) {
				throw new InvalidInputException();
			}
		}
	}

	private <T extends ACOrgUnit<?>> List<T> removeInvisibleOrgUnits(List<T> orgUnits, ESSessionId sessionId)
		throws AccessControlException {
		/*
		 * regular users can''t see any orgunits, while server admins can see all of them. Only project admins have
		 * reduced visibility.
		 */
		final ESOrgUnitId adminId = getAccessControl().getSessions().resolveToOrgUnitId(sessionId);
		final Optional<ACOrgUnit<?>> orgUnit = ACHelper.getOrgUnit(
			getAccessControl().getOrgUnitProviderService(),
			adminId);
		if (!orgUnit.isPresent()) {
			return orgUnits;
		}
		final List<Role> allRolesOfAdmin = ACHelper.getAllRoles(
			getAccessControl().getOrgUnitResolverServive(),
			orgUnit.get());
		if (Iterables.any(allRolesOfAdmin, new HasRolePredicate(ServerAdmin.class))) {
			return orgUnits;
		}
		final List<ProjectAdminRole> projectAdminRoles = new ArrayList<ProjectAdminRole>();
		for (final Role role : allRolesOfAdmin) {
			if (ProjectAdminRole.class.isInstance(role)) {
				projectAdminRoles.add((ProjectAdminRole) role);
			}
		}

		/* we are dealing with a project admin */
		final List<T> result = new ArrayList<T>();
		for (final T unit : orgUnits) {
			if (Iterables.any(ACHelper.getAllRoles(getAccessControl().getOrgUnitResolverServive(), unit),
				new HasRolePredicate(ServerAdmin.class))) {
				/* server admins should not be visible to project admin */
				continue;
			}
			/* units are visible to project admin if */
			if (wasCreatedByProjectAdmin(unit, orgUnit.get()) // the unit was created by this admin
				|| hasRoleInProjectOfProjectAdmin(unit, projectAdminRoles) // the unit has a role on at least one of the
																			// projects administered by the admin
			) {
				result.add(unit);
			}
		}
		return result;
	}

	private boolean wasCreatedByProjectAdmin(ACOrgUnit<?> orgUnit, ACOrgUnit<?> admin) {
		return admin.getId().getId().equals(orgUnit.getCreatedBy());
	}

	private boolean hasRoleInProjectOfProjectAdmin(ACOrgUnit<?> orgUnit, List<ProjectAdminRole> projectAdminRoles) {
		/* collect all administered projects */
		final Set<ProjectId> projects = new LinkedHashSet<ProjectId>();
		for (final ProjectAdminRole projectAdminRole : projectAdminRoles) {
			projects.addAll(projectAdminRole.getProjects());
		}

		for (final ProjectId projectId : projects) {
			/* check if any role of unit has access to project */
			for (final Role role : ACHelper.getAllRoles(getAccessControl().getOrgUnitResolverServive(), orgUnit)) {
				if (role.canRead(projectId, null)
					|| role.canModify(projectId, null)
					|| role.canCreate(projectId, null)
					|| role.canDelete(projectId, null)) {
					return true;
				}
			}
		}
		return false;
	}

	private void log(SessionId sessionId, String message) throws AccessControlException {
		final ACUser user = resolveUserBySessionId(sessionId.getId());
		final String logMessage = MessageFormat.format("[Username: {0} | User ID: {1}] {2}", //$NON-NLS-1$
			user.getName(), user.getIdentifier(), message);
		Activator.getDefault().logInfo(logMessage);
	}

	/**
	 * {@inheritDoc}.
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.core.AbstractEmfstoreInterface#initSubInterfaces()
	 */
	@Override
	protected void initSubInterfaces() throws FatalESException {
	}
}
