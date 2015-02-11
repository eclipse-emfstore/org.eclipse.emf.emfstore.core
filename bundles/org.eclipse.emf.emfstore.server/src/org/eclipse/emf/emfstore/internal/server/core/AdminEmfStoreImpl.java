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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.AdminEmfStore;
import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.AccessControl;
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
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.Role;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.RolesFactory;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.RolesPackage;
import org.eclipse.emf.emfstore.internal.server.model.dao.ACDAOFacade;
import org.eclipse.emf.emfstore.server.auth.ESAuthorizationService;
import org.eclipse.emf.emfstore.server.auth.ESProjectAdminPrivileges;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.model.ESGroup;
import org.eclipse.emf.emfstore.server.model.ESUser;

/**
 * Implementation of {@link AdminEmfStore} interface.
 *
 * @author wesendon
 */
// TODO: bring this interface in new subinterface structure and refactor it
public class AdminEmfStoreImpl extends AbstractEmfstoreInterface implements AdminEmfStore {

	private final ACDAOFacade daoFacade;

	/**
	 * Default constructor.
	 *
	 * @param daoFacade
	 *            provider facade for access control related DAOs
	 * @param serverSpace
	 *            the server space
	 * @param accessControl
	 *            the authorization control
	 * @throws FatalESException
	 *             in case of failure
	 */
	public AdminEmfStoreImpl(ACDAOFacade daoFacade, ServerSpace serverSpace,
		AccessControl accessControl)
		throws FatalESException {
		super(serverSpace, accessControl);
		this.daoFacade = daoFacade;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ACGroup> getGroups(SessionId sessionId) throws ESException {
		checkForNulls(sessionId);
		getAccessControl().getAuthorizationService().checkProjectAdminAccess(
			sessionId.toAPI(),
			null);
		final List<ACGroup> result = new ArrayList<ACGroup>();
		for (final ACGroup group : daoFacade.getGroups()) {
			// quickfix
			final ACGroup copy = ModelUtil.clone(group);
			clearMembersFromGroup(copy);
			result.add(copy);
		}
		return result;
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
		for (final ACGroup group : daoFacade.getGroups()) {
			if (group.getMembers().contains(orgUnit)) {
				// quickfix
				final ACGroup copy = ModelUtil.clone(group);
				clearMembersFromGroup(copy);
				result.add(copy);
			}
		}
		return result;
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
			throw new InvalidInputException(Messages.AdminEmfStoreImpl_Group_Already_Exists);
		}

		final ACGroup acGroup = AccesscontrolFactory.eINSTANCE.createACGroup();
		acGroup.setName(name);
		acGroup.setDescription(StringUtils.EMPTY);
		daoFacade.add(acGroup);
		save();
		return ModelUtil.clone(acGroup.getId());
	}

	private boolean groupExists(String name) {
		for (final ACGroup group : daoFacade.getGroups()) {
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

		getGroup(group).getMembers().remove(getOrgUnit(user));
		save();
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

		for (final Iterator<ACGroup> iter = daoFacade.getGroups().iterator(); iter.hasNext();) {
			final ACGroup nextGroup = iter.next();
			final List<ACGroup> groups = getGroups(sessionId, groupId);
			if (nextGroup.getId().equals(groupId)) {
				for (final ACGroup acGroup : groups) {
					removeMember(sessionId, acGroup.getId(), nextGroup.getId());
				}
				daoFacade.remove(nextGroup);
				EcoreUtil.delete(nextGroup);
				save();
				return;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ACOrgUnit> getMembers(SessionId sessionId, ACOrgUnitId groupId) throws ESException {

		checkForNulls(sessionId, groupId);

		getAccessControl().getAuthorizationService().checkProjectAdminAccess(
			sessionId.toAPI(),
			null);

		// quickfix
		final List<ACOrgUnit> result = new ArrayList<ACOrgUnit>();
		for (final ACOrgUnit<?> orgUnit : getGroup(groupId).getMembers()) {
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
	public List<ACOrgUnit> getParticipants(SessionId sessionId, ProjectId projectId) throws ESException {

		checkForNulls(sessionId);
		getAccessControl().getAuthorizationService().checkProjectAdminAccess(
			sessionId.toAPI(),
			projectId.toAPI());

		final List<ACOrgUnit> result = new ArrayList<ACOrgUnit>();
		for (final ACOrgUnit<ESUser> orgUnit : daoFacade.getUsers()) {

			final List<Role> roles = orgUnit.getRoles();
			for (final Role role : roles) {
				if (isServerAdmin(role) || role.getProjects().contains(projectId)) {
					result.add(ModelUtil.clone(orgUnit));
				}
			}
		}

		for (final ACOrgUnit<ESGroup> orgUnit : daoFacade.getGroups()) {
			final List<Role> roles = orgUnit.getRoles();
			for (final Role role : roles) {
				if (isServerAdmin(role) || role.getProjects().contains(projectId)) {
					result.add(ModelUtil.clone(orgUnit));
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
				return;
			}
		}

		final Role newRole = (Role) RolesPackage.eINSTANCE.getEFactoryInstance().create(
			(EClass) RolesPackage.eINSTANCE.getEClassifier(roleClass.getName()));

		newRole.getProjects().add(ModelUtil.clone(projectId));
		orgUnit.getRoles().add(newRole);
		save();
	}

	/**
	 * {@inheritDoc}
	 */
	public void addInitialParticipant(SessionId sessionId, ProjectId projectId, ACOrgUnitId participantId,
		EClass roleClass) throws ESException {

		checkForNulls(sessionId, projectId, participantId, roleClass);

		projectId = getProjectId(projectId);
		final ACOrgUnit<?> orgUnit = getOrgUnit(participantId);

		final Role newRole = (Role) RolesPackage.eINSTANCE.getEFactoryInstance().create(
			(EClass) RolesPackage.eINSTANCE.getEClassifier(roleClass.getName()));

		// if participant is server admin, return
		final List<Role> roles = orgUnit.getRoles();
		for (final Role role : roles) {
			if (isServerAdmin(role)) {
				return;
			}
		}

		final List<Role> roles2 = orgUnit.getRoles();
		for (final Role role : roles2) {
			if (areEqual(role, roleClass)) {
				role.getProjects().add(ModelUtil.clone(projectId));
				save();
				return;
			}
		}

		newRole.getProjects().add(ModelUtil.clone(projectId));
		orgUnit.getRoles().add(newRole);
		save();
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
		for (final Role role : roles) {
			if (isServerAdmin(role) || role.getProjects().contains(projectId)) {
				return role;
			}
		}
		throw new ESException(Messages.AdminEmfStoreImpl_Could_Not_Find_OrgUnit);
	}

	/**
	 * {@inheritDoc}
	 */
	public void changeRole(SessionId sessionId, ProjectId projectId, ACOrgUnitId orgUnitId, EClass roleClass)
		throws ESException {

		checkForNulls(sessionId, projectId, orgUnitId, roleClass);

		final ESAuthorizationService authorizationService = getAccessControl().getAuthorizationService();
		authorizationService.checkProjectAdminAccess(
			sessionId.toAPI(),
			projectId.toAPI());
		final boolean isServerAdmin = authorizationService.checkProjectAdminAccessForOrgUnit(
			sessionId.toAPI(),
			orgUnitId.toAPI(),
			Collections.singleton(projectId.toAPI()));

		if (!ServerConfiguration.isProjectAdminPrivileg(ESProjectAdminPrivileges.AssignRoleToOrgUnit)) {
			throw new AccessControlException(Messages.AdminEmfStoreImpl_Assign_Role_Privilege_Not_Set);
		}

		if (!isServerAdmin && isServerAdminRole(roleClass)) {
			throw new AccessControlException(Messages.AdminEmfStoreImpl_Not_Allowed_To_Assign_ServerAdminRole);
		}

		projectId = getProjectId(projectId);
		final ACOrgUnit<?> orgUnit = getOrgUnit(orgUnitId);

		// delete old role first
		final Role role = getRole(projectId, orgUnit);
		if (role != null) {

			if (!isServerAdmin && role.canAdministrate(projectId)) {
				throw new AccessControlException(
					Messages.AdminEmfStoreImpl_RemovePA_Violation_1
						+ Messages.AdminEmfStoreImpl_RemovePA_Violation_2);
			}

			role.getProjects().remove(projectId);
			if (role.getProjects().size() == 0) {
				orgUnit.getRoles().remove(role);
			}
		}

		// if server admin
		if (isServerAdminRole(roleClass)) {
			orgUnit.getRoles().add(RolesFactory.eINSTANCE.createServerAdmin());
			save();
			return;
		}
		// add project to role if it exists
		final List<Role> roles = orgUnit.getRoles();
		for (final Role r : roles) {
			if (r.eClass().getName().equals(roleClass.getName())) {
				r.getProjects().add(ModelUtil.clone(projectId));
				save();
				return;
			}
		}
		// create role if does not exists
		final Role newRole = (Role) RolesPackage.eINSTANCE.getEFactoryInstance().create(
			(EClass) RolesPackage.eINSTANCE.getEClassifier(roleClass.getName()));
		newRole.getProjects().add(ModelUtil.clone(projectId));
		orgUnit.getRoles().add(newRole);
		save();
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

		final Role newRole = (Role) RolesPackage.eINSTANCE.getEFactoryInstance().create(
			(EClass) RolesPackage.eINSTANCE.getEClassifier(roleClass.getName()));

		orgUnit.getRoles().add(newRole);
		save();
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
		for (final ACUser user : daoFacade.getUsers()) {
			result.add(user);
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ACOrgUnit> getOrgUnits(SessionId sessionId) throws ESException {
		checkForNulls(sessionId);
		getAccessControl().getAuthorizationService().checkProjectAdminAccess(
			sessionId.toAPI(),
			null);
		final List<ACOrgUnit> result = new ArrayList<ACOrgUnit>();
		for (final ACOrgUnit<ESUser> user : daoFacade.getUsers()) {
			result.add(ModelUtil.clone(user));
		}
		for (final ACOrgUnit<ESGroup> group : daoFacade.getGroups()) {
			result.add(ModelUtil.clone(group));
		}
		// quickfix
		clearMembersFromGroups(result);
		return result;
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
			throw new InvalidInputException("Username '" + name + "' already exists."); //$NON-NLS-1$ //$NON-NLS-2$
		}
		final ACUser acUser = AccesscontrolFactory.eINSTANCE.createACUser();
		acUser.setName(name);
		acUser.setDescription(StringUtils.EMPTY);
		daoFacade.add(acUser);
		save();
		return ModelUtil.clone(acUser.getId());
	}

	private boolean userExists(String name) {
		for (final ACUser user : daoFacade.getUsers()) {
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
		for (final Iterator<ACUser> iter = daoFacade.getUsers().iterator(); iter.hasNext();) {
			final ACUser user = iter.next();
			final List<ACGroup> groups = getGroups(sessionId, userId);
			if (user.getId().equals(userId)) {
				for (final ACGroup acGroup : groups) {
					removeMember(sessionId, acGroup.getId(), userId);
				}
				daoFacade.remove(user);
				// TODO: move ecore delete into ServerSpace#deleteUser implementation
				EcoreUtil.delete(user);
				save();
				return;
			}
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
		orgUnit.setName(name);
		orgUnit.setDescription(description);
		save();
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
		final ESAuthorizationService authorizationService = getAccessControl().getAuthorizationService();

		final boolean isServerAdmin = authorizationService.checkProjectAdminAccess(
			sessionId.toAPI(),
			null,
			ESProjectAdminPrivileges.ChangeUserPassword);

		if (!isServerAdmin) {
			authorizationService.checkProjectAdminAccessForOrgUnit(
				sessionId.toAPI(),
				userId.toAPI());
		}

		updateUser(userId, name, password);
	}

	private void updateUser(ACOrgUnitId userId, String name, String password) throws ESException {

		final ACUser user = (ACUser) getOrgUnit(userId);
		user.setName(name);
		user.setPassword(password);
		save();
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
	private void clearMembersFromGroups(Collection<ACOrgUnit> orgUnits) {
		for (final ACOrgUnit<?> orgUnit : orgUnits) {
			clearMembersFromGroup(orgUnit);
		}
	}

	/**
	 * This method is used as fix for the containment issue of group.
	 */
	private void clearMembersFromGroup(ACOrgUnit<?> orgUnit) {
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
		for (final ACGroup group : daoFacade.getGroups()) {
			if (group.getId().equals(orgUnitId)) {
				return group;
			}
		}
		throw new ESException(Messages.AdminEmfStoreImpl_Group_Does_Not_Exist);
	}

	private ACOrgUnit<?> getOrgUnit(ACOrgUnitId orgUnitId) throws ESException {
		for (final ACOrgUnit<ESUser> unit : daoFacade.getUsers()) {
			if (unit.getId().equals(orgUnitId)) {
				return unit;
			}
		}
		for (final ACOrgUnit<ESGroup> unit : daoFacade.getGroups()) {
			if (unit.getId().equals(orgUnitId)) {
				return unit;
			}
		}
		throw new ESException(Messages.AdminEmfStoreImpl_OrgUnit_Does_Not_Exist);
	}

	private Role getRole(ProjectId projectId, ACOrgUnit<?> orgUnit) {
		final List<Role> roles = orgUnit.getRoles();
		for (final Role role : roles) {
			if (isServerAdmin(role) || role.getProjects().contains(projectId)) {
				// return (Role) ModelUtil.clone(role);
				return role;
			}
		}
		return null;
	}

	private void save() throws ESException {
		try {
			daoFacade.save();
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

	/**
	 * {@inheritDoc}.
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.core.AbstractEmfstoreInterface#initSubInterfaces()
	 */
	@Override
	protected void initSubInterfaces() throws FatalESException {
	}
}
