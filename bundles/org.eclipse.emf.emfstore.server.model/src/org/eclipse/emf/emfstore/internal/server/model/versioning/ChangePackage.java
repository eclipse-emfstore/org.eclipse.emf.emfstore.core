/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.model.versioning;

import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.internal.common.api.APIDelegate;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.server.model.versioning.events.Event;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;
import org.eclipse.emf.emfstore.server.model.ESLogMessage;

/**
 * <!-- begin-user-doc --> A representation of the model object ' <em><b>Change Package</b></em>'.
 *
 * @extends APIDelegate<ESChangePackage>
 *          <!-- end-user-doc -->
 *
 *          <p>
 *          The following features are supported:
 *          <ul>
 *          <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage#getOperations <em>
 *          Operations</em>}</li>
 *          <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage#getEvents <em>Events
 *          </em>}</li>
 *          <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage#getLogMessage <em>Log
 *          Message</em>}</li>
 *          <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage#getVersionProperties <em>
 *          Version Properties</em>}</li>
 *          </ul>
 *          </p>
 *
 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage#getChangePackage()
 * @model
 * @generated
 */
public interface ChangePackage extends EObject, APIDelegate<ESChangePackage>, ESChangePackage {

	/**
	 * Returns the value of the '<em><b>Operations</b></em>' containment reference list.
	 * The list contents are of type
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>Operations</em>' containment reference list.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage#getChangePackage_Operations()
	 * @model containment="true" resolveProxies="true"
	 * @generated
	 */
	EList<AbstractOperation> getOperations();

	/**
	 * Returns the value of the '<em><b>Events</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.emf.emfstore.internal.server.model.versioning.events.Event}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Events</em>' containment reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>Events</em>' containment reference list.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage#getChangePackage_Events()
	 * @model containment="true" resolveProxies="true"
	 * @generated
	 */
	EList<Event> getEvents();

	/**
	 * Returns the value of the '<em><b>Log Message</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Log Message</em>' containment reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>Log Message</em>' containment reference.
	 * @see #setLogMessage(LogMessage)
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage#getChangePackage_LogMessage()
	 * @model containment="true" resolveProxies="true"
	 * @generated
	 */
	// TODO: FIXME wrong return type, but must keep API compatibility
	ESLogMessage getLogMessage();

	/**
	 * Sets the value of the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage#getLogMessage
	 * <em>Log Message</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @param value the new value of the '<em>Log Message</em>' containment reference.
	 * @see #getLogMessage()
	 * @generated
	 */
	void setLogMessage(LogMessage value);

	/**
	 * Returns the value of the '<em><b>Version Properties</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.emf.emfstore.internal.server.model.versioning.VersionProperty}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Version Properties</em>' containment reference list isn't clear, there really should
	 * be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>Version Properties</em>' containment reference list.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage#getChangePackage_VersionProperties()
	 * @model containment="true" resolveProxies="true"
	 * @generated
	 */
	EList<VersionProperty> getVersionProperties();

	/**
	 * Apply all operations in the change package to the given project.
	 *
	 * @param project
	 *            the project
	 */
	void apply(Project project);

	/**
	 * Apply all operations in the change package to the given project.
	 * Additional you can force the operations to be applied with illegal
	 * operations being ignored.
	 *
	 * @param project
	 *            the project
	 * @param force
	 *            if true, illegal Operations won't stop the other to be applied
	 */
	void apply(Project project, boolean force);

	/**
	 * Remove all operations from the change package that are masked by later
	 * operations in the same package.
	 */
	void cannonize();

	/**
	 * Returns all model elements that are involved in this change package.
	 *
	 * @return a set of model element ids
	 */
	Set<ModelElementId> getAllInvolvedModelElements();

	/**
	 * Counts the number of Leaf Operations within this change package. The
	 * method will recursively go through all composite operations.
	 *
	 * @return the number of Leaf Operations
	 */
	int getSize();

} // ChangePackage