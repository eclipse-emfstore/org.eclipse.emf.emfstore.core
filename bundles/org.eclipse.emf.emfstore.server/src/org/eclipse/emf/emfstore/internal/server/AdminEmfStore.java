/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Wesendonk
 * Hodaie
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.model.ProjectInfo;
import org.eclipse.emf.emfstore.internal.server.model.SessionId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.Role;
import org.eclipse.emf.emfstore.server.exceptions.ESException;

/**
 * AdminEmfStore offers an interface for administrating the users and their rights.
 * 
 * @author Hodaie
 * @author Wesendonk
 */
public interface AdminEmfStore extends EMFStoreInterface {

	/**
	 * Returns a list of available project.
	 * 
	 * @param sessionId the session id for authentication
	 * @return list of project infos
	 * @throws ESException if any error in the EmfStore occurs
	 */
	List<ProjectInfo> getProjectInfos(SessionId sessionId) throws ESException;

	/**
	 * Returns all groups on the server.
	 * 
	 * @param sessionId the session id for authentication
	 * @return list of groups
	 * @throws ESException if any error in the EmfStore occurs
	 */
	List<ACGroup> getGroups(SessionId sessionId) throws ESException;

	/**
	 * Returns all users on the server.
	 * 
	 * @param sessionId the session id for authentication
	 * @return list of user
	 * @throws ESException if any error in the EmfStore occurs
	 */
	List<ACUser> getUsers(SessionId sessionId) throws ESException;

	/**
	 * Returns all orgUnits on the server.
	 * 
	 * @param sessionId the session id for authentication
	 * @return list of orgUnits
	 * @throws ESException if any error in the EmfStore occurs
	 */
	List<ACOrgUnit> getOrgUnits(SessionId sessionId) throws ESException;

	/**
	 * Returns an orgUnit with the specified orgUnitId.
	 * 
	 * @param orgUnitId the orgUnitId
	 * @param sessionId the session id for authentication
	 * @return an orgUnit
	 * @throws ESException if any error in the EmfStore occurs
	 */
	ACOrgUnit getOrgUnit(SessionId sessionId, ACOrgUnitId orgUnitId) throws ESException;

	/**
	 * Creates a group on the server.
	 * 
	 * @param sessionId the session id for authentication
	 * @param name the name for the group
	 * @return ACOrgUnitId
	 * @throws ESException if any error in the EmfStore occurs
	 */
	ACOrgUnitId createGroup(SessionId sessionId, String name) throws ESException;

	/**
	 * Deletes a group on the server.
	 * 
	 * @param sessionId the session id for authentication
	 * @param group orgUnitId of the group
	 * @throws ESException if any error in the EmfStore occurs
	 */
	void deleteGroup(SessionId sessionId, ACOrgUnitId group) throws ESException;

	/**
	 * Returns a list of all groups in which the specified user is member of.
	 * 
	 * @param sessionId the session id for authentication
	 * @param user the users orgUnitId
	 * @return a list of groups
	 * @throws ESException if any error in the EmfStore occurs
	 */
	List<ACGroup> getGroups(SessionId sessionId, ACOrgUnitId user) throws ESException;

	/**
	 * Removes a user from a group.
	 * 
	 * @param sessionId the session id for authentication
	 * @param user the user's orgUnitId
	 * @param group the group's orgUnitId
	 * @throws ESException if any error in the EmfStore occurs
	 */
	void removeGroup(SessionId sessionId, ACOrgUnitId user, ACOrgUnitId group) throws ESException;

	/**
	 * Returns all members from a group.
	 * 
	 * @param sessionId the session id for authentication
	 * @param groupId the group's orgUnitId
	 * @return a list of orgUnits
	 * @throws ESException if any error in the EmfStore occurs.
	 */
	List<ACOrgUnit> getMembers(SessionId sessionId, ACOrgUnitId groupId) throws ESException;

	/**
	 * Adds an orgUnit to a group.
	 * 
	 * @param sessionId the session id for authentication
	 * @param group the group's orgUnitId
	 * @param member the members orgUnitId
	 * @throws ESException if any error in the EmfStore occurs
	 */
	void addMember(SessionId sessionId, ACOrgUnitId group, ACOrgUnitId member) throws ESException;

	/**
	 * Removes a orgUnit from a group.
	 * 
	 * @param sessionId the session id for authentication
	 * @param group the group's orgUnitId
	 * @param member the members orgUnitId
	 * @throws ESException if any error in the EmfStore occurs
	 */
	void removeMember(SessionId sessionId, ACOrgUnitId group, ACOrgUnitId member) throws ESException;

	/**
	 * Creates a user on the server.
	 * 
	 * @param sessionId the session id for authentication
	 * @param name the user's name
	 * @return ACOrgUnitId the {@link ACOrgUnitId} of the created user
	 * @throws ESException if any error occurred while creating the user
	 */
	ACOrgUnitId createUser(SessionId sessionId, String name) throws ESException;

	/**
	 * Deletes a user from the server.
	 * 
	 * @param sessionId the session id for authentication
	 * @param user the user's orgUnitId
	 * @throws ESException if any error in the EmfStore occurs
	 */
	void deleteUser(SessionId sessionId, ACOrgUnitId user) throws ESException;

	/**
	 * Changes the orgUnit's name and description.
	 * 
	 * @param sessionId the session id for authentication.
	 * @param orgUnitId the orgUnitId
	 * @param name the new name
	 * @param description the new description
	 * @throws ESException if any error in the EmfStore occurs
	 */
	void changeOrgUnit(SessionId sessionId, ACOrgUnitId orgUnitId, String name, String description)
		throws ESException;

	/**
	 * Changes the name and password of an {@link ACUser}.
	 * 
	 * @param sessionId
	 *            the session id for authentication.
	 * @param userId
	 *            the ID of the user
	 * @param name
	 *            the new name
	 * @param password
	 *            the new password
	 * @throws ESException if any error in the EmfStore occurs
	 */
	void changeUser(SessionId sessionId, ACOrgUnitId userId, String name, String password) throws ESException;

	/**
	 * Returns all orgUnits which are attached to the given project.
	 * 
	 * @param sessionId the session id for authentication
	 * @param projectId project's id
	 * @return a list of orgUnits
	 * @throws ESException if any error in the EmfStore occurs
	 */
	List<ACOrgUnit> getParticipants(SessionId sessionId, ProjectId projectId) throws ESException;

	/**
	 * Adds an organization unit to a project.
	 * 
	 * @param sessionId
	 *            the {@link SessionId} for authentication
	 * @param projectId
	 *            the {@link ProjectId} of the project
	 * @param participantId
	 *            the {@link ACOrgUnitId} of the participant
	 * @param roleClass
	 *            the role to be assigned to the participant
	 * @throws ESException if any error in the EmfStore occurs
	 */
	void addParticipant(SessionId sessionId, ProjectId projectId, ACOrgUnitId participantId, EClass roleClass)
		throws ESException;

	/**
	 * Adds an organization unit to a project.
	 * 
	 * @param sessionId
	 *            the {@link SessionId} for authentication
	 * @param projectId
	 *            the {@link ProjectId} of the project
	 * @param participantId
	 *            the {@link ACOrgUnitId} of the participant
	 * @param roleClass
	 *            the role to be assigned to the participant
	 * @throws ESException if any error in the EmfStore occurs
	 */
	// TODO: hack
	void addInitialParticipant(SessionId sessionId, ProjectId projectId, ACOrgUnitId participantId,
		EClass roleClass) throws ESException;

	/**
	 * Removes an orgUnits from a project.
	 * 
	 * @param sessionId the session id for authentication
	 * @param projectId the project's id
	 * @param participant the orgUnit's id
	 * @throws ESException if any error in the EmfStore occurs
	 */
	void removeParticipant(SessionId sessionId, ProjectId projectId, ACOrgUnitId participant) throws ESException;

	/**
	 * Returns an orgUnit's role for a specified project.
	 * 
	 * @param sessionId the session id for authentication
	 * @param projectId the project's id
	 * @param orgUnit the orgUnit's id
	 * @return a role the user's role
	 * @throws ESException if any error in the EmfStore occurs
	 */
	Role getRole(SessionId sessionId, ProjectId projectId, ACOrgUnitId orgUnit) throws ESException;

	/**
	 * Changes the role for an orgUnit in a specified project.
	 * 
	 * @param sessionId the session id for authentication
	 * @param projectId the project's id
	 * @param orgUnit the orgUnit
	 * @param role new role for orgUnit
	 * @throws ESException if any error in the EmfStore occurs.
	 */
	void changeRole(SessionId sessionId, ProjectId projectId, ACOrgUnitId orgUnit, EClass role)
		throws ESException;

	/**
	 * Assigns a role for an orgUnit without a project.
	 * 
	 * @param sessionId
	 *            the {@link SessionId} for authentication
	 * @param orgUnitId
	 *            the ID of an organizational unit
	 * @param roleClass
	 *            the role to be assigned
	 * @throws ESException
	 *             if an exceptions occurs on the server or on transport
	 */
	void assignRole(SessionId sessionId, ACOrgUnitId orgUnitId, EClass roleClass) throws ESException;
}
