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
package org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.emfstore.internal.common.model.impl.IdentifiableElementImpl;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.AccesscontrolFactory;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.AccesscontrolPackage;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.OrgUnitProperty;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.Role;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESOrgUnitImpl;
import org.eclipse.emf.emfstore.server.model.ESOrgUnit;

/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>AC Org Unit</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.ACOrgUnitImpl#getName
 * <em>Name</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.ACOrgUnitImpl#getRoles
 * <em>Roles</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.ACOrgUnitImpl#getDescription
 * <em>Description</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.ACOrgUnitImpl#getProperties
 * <em>Properties</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.ACOrgUnitImpl#getCreatedBy <em>Created
 * By</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ACOrgUnitImpl<E extends ESOrgUnit> extends IdentifiableElementImpl implements ACOrgUnit<E> {

	/**
	 * @generated NOT
	 */
	private E apiImpl;

	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getRoles() <em>Roles</em>}' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getRoles()
	 * @generated
	 * @ordered
	 */
	protected EList<Role> roles;

	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected String description = DESCRIPTION_EDEFAULT;

	/**
	 * The cached value of the '{@link #getProperties() <em>Properties</em>}' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getProperties()
	 * @generated
	 * @ordered
	 */
	protected EList<OrgUnitProperty> properties;

	/**
	 * The default value of the '{@link #getCreatedBy() <em>Created By</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getCreatedBy()
	 * @generated
	 * @ordered
	 */
	protected static final String CREATED_BY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCreatedBy() <em>Created By</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getCreatedBy()
	 * @generated
	 * @ordered
	 */
	protected String createdBy = CREATED_BY_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected ACOrgUnitImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return AccesscontrolPackage.Literals.AC_ORG_UNIT;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setName(String newName) {
		final String oldName = name;
		name = newName;
		if (eNotificationRequired()) {
			eNotify(
				new ENotificationImpl(this, Notification.SET, AccesscontrolPackage.AC_ORG_UNIT__NAME, oldName, name));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EList<Role> getRoles() {
		if (roles == null) {
			roles = new EObjectContainmentEList.Resolving<Role>(Role.class, this,
				AccesscontrolPackage.AC_ORG_UNIT__ROLES);
		}
		return roles;
	}

	// begin of custom code

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit#getId()
	 * @generated NOT
	 */
	public ACOrgUnitId getId() {
		if (identifier == null) {
			throw new IllegalStateException("ACOrgunit does not have an identifier"); //$NON-NLS-1$
		}
		final ACOrgUnitId orgUnitId = AccesscontrolFactory.eINSTANCE.createACOrgUnitId();
		orgUnitId.setId(identifier);
		return orgUnitId;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @generated NOT
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ACOrgUnit) {
			return getId().equals(((ACOrgUnit) obj).getId());
		}
		return super.equals(obj);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getId().getId().hashCode();
	}

	// end of custom code

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case AccesscontrolPackage.AC_ORG_UNIT__ROLES:
			return ((InternalEList<?>) getRoles()).basicRemove(otherEnd, msgs);
		case AccesscontrolPackage.AC_ORG_UNIT__PROPERTIES:
			return ((InternalEList<?>) getProperties()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setDescription(String newDescription) {
		final String oldDescription = description;
		description = newDescription;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, AccesscontrolPackage.AC_ORG_UNIT__DESCRIPTION,
				oldDescription, description));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EList<OrgUnitProperty> getProperties() {
		if (properties == null) {
			properties = new EObjectContainmentEList.Resolving<OrgUnitProperty>(OrgUnitProperty.class, this,
				AccesscontrolPackage.AC_ORG_UNIT__PROPERTIES);
		}
		return properties;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setCreatedBy(String newCreatedBy) {
		final String oldCreatedBy = createdBy;
		createdBy = newCreatedBy;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, AccesscontrolPackage.AC_ORG_UNIT__CREATED_BY,
				oldCreatedBy, createdBy));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case AccesscontrolPackage.AC_ORG_UNIT__NAME:
			return getName();
		case AccesscontrolPackage.AC_ORG_UNIT__ROLES:
			return getRoles();
		case AccesscontrolPackage.AC_ORG_UNIT__DESCRIPTION:
			return getDescription();
		case AccesscontrolPackage.AC_ORG_UNIT__PROPERTIES:
			return getProperties();
		case AccesscontrolPackage.AC_ORG_UNIT__CREATED_BY:
			return getCreatedBy();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case AccesscontrolPackage.AC_ORG_UNIT__NAME:
			setName((String) newValue);
			return;
		case AccesscontrolPackage.AC_ORG_UNIT__ROLES:
			getRoles().clear();
			getRoles().addAll((Collection<? extends Role>) newValue);
			return;
		case AccesscontrolPackage.AC_ORG_UNIT__DESCRIPTION:
			setDescription((String) newValue);
			return;
		case AccesscontrolPackage.AC_ORG_UNIT__PROPERTIES:
			getProperties().clear();
			getProperties().addAll((Collection<? extends OrgUnitProperty>) newValue);
			return;
		case AccesscontrolPackage.AC_ORG_UNIT__CREATED_BY:
			setCreatedBy((String) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case AccesscontrolPackage.AC_ORG_UNIT__NAME:
			setName(NAME_EDEFAULT);
			return;
		case AccesscontrolPackage.AC_ORG_UNIT__ROLES:
			getRoles().clear();
			return;
		case AccesscontrolPackage.AC_ORG_UNIT__DESCRIPTION:
			setDescription(DESCRIPTION_EDEFAULT);
			return;
		case AccesscontrolPackage.AC_ORG_UNIT__PROPERTIES:
			getProperties().clear();
			return;
		case AccesscontrolPackage.AC_ORG_UNIT__CREATED_BY:
			setCreatedBy(CREATED_BY_EDEFAULT);
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case AccesscontrolPackage.AC_ORG_UNIT__NAME:
			return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
		case AccesscontrolPackage.AC_ORG_UNIT__ROLES:
			return roles != null && !roles.isEmpty();
		case AccesscontrolPackage.AC_ORG_UNIT__DESCRIPTION:
			return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
		case AccesscontrolPackage.AC_ORG_UNIT__PROPERTIES:
			return properties != null && !properties.isEmpty();
		case AccesscontrolPackage.AC_ORG_UNIT__CREATED_BY:
			return CREATED_BY_EDEFAULT == null ? createdBy != null : !CREATED_BY_EDEFAULT.equals(createdBy);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) {
			return super.toString();
		}

		final StringBuffer result = new StringBuffer(super.toString());
		result.append(" (name: "); //$NON-NLS-1$
		result.append(name);
		result.append(", description: "); //$NON-NLS-1$
		result.append(description);
		result.append(", createdBy: "); //$NON-NLS-1$
		result.append(createdBy);
		result.append(')');
		return result.toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.api.APIDelegate#toAPI()
	 *
	 * @generated NOT
	 */
	public E toAPI() {
		if (apiImpl == null) {
			apiImpl = createAPI();
		}
		return apiImpl;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.api.APIDelegate#createAPI()
	 *
	 * @generated NOT
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public E createAPI() {
		return (E) new ESOrgUnitImpl(this);
	}

} // ACOrgUnitImpl
