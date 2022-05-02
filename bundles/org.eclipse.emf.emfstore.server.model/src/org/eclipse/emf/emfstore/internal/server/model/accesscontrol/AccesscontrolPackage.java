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
package org.eclipse.emf.emfstore.internal.server.model.accesscontrol;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.emfstore.internal.common.model.ModelPackage;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains
 * accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 *
 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.AccesscontrolFactory
 * @model kind="package"
 * @generated
 */
public interface AccesscontrolPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	String eNAME = "accesscontrol"; //$NON-NLS-1$

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	String eNS_URI = "http://eclipse.org/emf/emfstore/server/model/accesscontrol"; //$NON-NLS-1$

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	String eNS_PREFIX = "org.eclipse.emf.emfstore.internal.server.model.accesscontrol"; //$NON-NLS-1$

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @generated
	 */
	AccesscontrolPackage eINSTANCE = org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.AccesscontrolPackageImpl
		.init();

	/**
	 * The meta object id for the '{@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.ACUserImpl
	 * <em>AC User</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.ACUserImpl
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.AccesscontrolPackageImpl#getACUser()
	 * @generated
	 */
	int AC_USER = 0;

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.ACOrgUnitImpl
	 * <em>AC Org Unit</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.ACOrgUnitImpl
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.AccesscontrolPackageImpl#getACOrgUnit()
	 * @generated
	 */
	int AC_ORG_UNIT = 1;

	/**
	 * The feature id for the '<em><b>Identifier</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_ORG_UNIT__IDENTIFIER = ModelPackage.IDENTIFIABLE_ELEMENT__IDENTIFIER;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_ORG_UNIT__NAME = ModelPackage.IDENTIFIABLE_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Roles</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_ORG_UNIT__ROLES = ModelPackage.IDENTIFIABLE_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_ORG_UNIT__DESCRIPTION = ModelPackage.IDENTIFIABLE_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_ORG_UNIT__PROPERTIES = ModelPackage.IDENTIFIABLE_ELEMENT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Created By</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_ORG_UNIT__CREATED_BY = ModelPackage.IDENTIFIABLE_ELEMENT_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>AC Org Unit</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_ORG_UNIT_FEATURE_COUNT = ModelPackage.IDENTIFIABLE_ELEMENT_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Identifier</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_USER__IDENTIFIER = AC_ORG_UNIT__IDENTIFIER;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_USER__NAME = AC_ORG_UNIT__NAME;

	/**
	 * The feature id for the '<em><b>Roles</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_USER__ROLES = AC_ORG_UNIT__ROLES;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_USER__DESCRIPTION = AC_ORG_UNIT__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_USER__PROPERTIES = AC_ORG_UNIT__PROPERTIES;

	/**
	 * The feature id for the '<em><b>Created By</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_USER__CREATED_BY = AC_ORG_UNIT__CREATED_BY;

	/**
	 * The feature id for the '<em><b>First Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_USER__FIRST_NAME = AC_ORG_UNIT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Last Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_USER__LAST_NAME = AC_ORG_UNIT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Effective Groups</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_USER__EFFECTIVE_GROUPS = AC_ORG_UNIT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Password</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_USER__PASSWORD = AC_ORG_UNIT_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>AC User</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_USER_FEATURE_COUNT = AC_ORG_UNIT_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.ACGroupImpl
	 * <em>AC Group</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.ACGroupImpl
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.AccesscontrolPackageImpl#getACGroup()
	 * @generated
	 */
	int AC_GROUP = 2;

	/**
	 * The feature id for the '<em><b>Identifier</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_GROUP__IDENTIFIER = AC_ORG_UNIT__IDENTIFIER;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_GROUP__NAME = AC_ORG_UNIT__NAME;

	/**
	 * The feature id for the '<em><b>Roles</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_GROUP__ROLES = AC_ORG_UNIT__ROLES;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_GROUP__DESCRIPTION = AC_ORG_UNIT__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_GROUP__PROPERTIES = AC_ORG_UNIT__PROPERTIES;

	/**
	 * The feature id for the '<em><b>Created By</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_GROUP__CREATED_BY = AC_ORG_UNIT__CREATED_BY;

	/**
	 * The feature id for the '<em><b>Members</b></em>' reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_GROUP__MEMBERS = AC_ORG_UNIT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>AC Group</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_GROUP_FEATURE_COUNT = AC_ORG_UNIT_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.ACOrgUnitIdImpl <em>AC Org Unit Id</em>}
	 * ' class.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.ACOrgUnitIdImpl
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.AccesscontrolPackageImpl#getACOrgUnitId()
	 * @generated
	 */
	int AC_ORG_UNIT_ID = 3;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_ORG_UNIT_ID__ID = ModelPackage.UNIQUE_IDENTIFIER__ID;

	/**
	 * The number of structural features of the '<em>AC Org Unit Id</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int AC_ORG_UNIT_ID_FEATURE_COUNT = ModelPackage.UNIQUE_IDENTIFIER_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.OrgUnitPropertyImpl
	 * <em>Org Unit Property</em>}' class.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.OrgUnitPropertyImpl
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.AccesscontrolPackageImpl#getOrgUnitProperty()
	 * @generated
	 */
	int ORG_UNIT_PROPERTY = 4;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int ORG_UNIT_PROPERTY__NAME = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int ORG_UNIT_PROPERTY__VALUE = 1;

	/**
	 * The feature id for the '<em><b>Project</b></em>' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int ORG_UNIT_PROPERTY__PROJECT = 2;

	/**
	 * The number of structural features of the '<em>Org Unit Property</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int ORG_UNIT_PROPERTY_FEATURE_COUNT = 3;

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser
	 * <em>AC User</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>AC User</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser
	 * @generated
	 */
	EClass getACUser();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser#getFirstName <em>First Name</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>First Name</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser#getFirstName()
	 * @see #getACUser()
	 * @generated
	 */
	EAttribute getACUser_FirstName();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser#getLastName <em>Last Name</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Last Name</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser#getLastName()
	 * @see #getACUser()
	 * @generated
	 */
	EAttribute getACUser_LastName();

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser#getEffectiveGroups
	 * <em>Effective Groups</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @return the meta object for the containment reference list ' <em>Effective Groups</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser#getEffectiveGroups()
	 * @see #getACUser()
	 * @generated
	 */
	EReference getACUser_EffectiveGroups();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser#getPassword <em>Password</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Password</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser#getPassword()
	 * @see #getACUser()
	 * @generated
	 */
	EAttribute getACUser_Password();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit
	 * <em>AC Org Unit</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>AC Org Unit</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit
	 * @generated
	 */
	EClass getACOrgUnit();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit#getName <em>Name</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit#getName()
	 * @see #getACOrgUnit()
	 * @generated
	 */
	EAttribute getACOrgUnit_Name();

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit#getRoles <em>Roles</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference list '<em>Roles</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit#getRoles()
	 * @see #getACOrgUnit()
	 * @generated
	 */
	EReference getACOrgUnit_Roles();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit#getDescription <em>Description</em>
	 * }'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit#getDescription()
	 * @see #getACOrgUnit()
	 * @generated
	 */
	EAttribute getACOrgUnit_Description();

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit#getProperties <em>Properties</em>}
	 * '.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference list '<em>Properties</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit#getProperties()
	 * @see #getACOrgUnit()
	 * @generated
	 */
	EReference getACOrgUnit_Properties();

	/**
	 * Returns the meta object for the attribute
	 * '{@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit#getCreatedBy <em>Created
	 * By</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Created By</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit#getCreatedBy()
	 * @see #getACOrgUnit()
	 * @generated
	 */
	EAttribute getACOrgUnit_CreatedBy();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup
	 * <em>AC Group</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>AC Group</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup
	 * @generated
	 */
	EClass getACGroup();

	/**
	 * Returns the meta object for the reference list '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup#getMembers <em>Members</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the reference list '<em>Members</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup#getMembers()
	 * @see #getACGroup()
	 * @generated
	 */
	EReference getACGroup_Members();

	/**
	 * Returns the meta object for class '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId <em>AC Org Unit Id</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>AC Org Unit Id</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId
	 * @generated
	 */
	EClass getACOrgUnitId();

	/**
	 * Returns the meta object for class '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.OrgUnitProperty
	 * <em>Org Unit Property</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @return the meta object for class '<em>Org Unit Property</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.OrgUnitProperty
	 * @generated
	 */
	EClass getOrgUnitProperty();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.OrgUnitProperty#getName <em>Name</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.OrgUnitProperty#getName()
	 * @see #getOrgUnitProperty()
	 * @generated
	 */
	EAttribute getOrgUnitProperty_Name();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.OrgUnitProperty#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.OrgUnitProperty#getValue()
	 * @see #getOrgUnitProperty()
	 * @generated
	 */
	EAttribute getOrgUnitProperty_Value();

	/**
	 * Returns the meta object for the containment reference '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.OrgUnitProperty#getProject <em>Project</em>}
	 * '.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference '<em>Project</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.OrgUnitProperty#getProject()
	 * @see #getOrgUnitProperty()
	 * @generated
	 */
	EReference getOrgUnitProperty_Project();

	/**
	 * Returns the factory that creates the instances of the model. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	AccesscontrolFactory getAccesscontrolFactory();

	/**
	 * <!-- begin-user-doc --> Defines literals for the meta objects that
	 * represent
	 * <ul>
	 * <li>each class,</li>
	 * <li>each feature of each class,</li>
	 * <li>each enum,</li>
	 * <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.ACUserImpl
		 * <em>AC User</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.ACUserImpl
		 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.AccesscontrolPackageImpl#getACUser()
		 * @generated
		 */
		EClass AC_USER = eINSTANCE.getACUser();

		/**
		 * The meta object literal for the '<em><b>First Name</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute AC_USER__FIRST_NAME = eINSTANCE.getACUser_FirstName();

		/**
		 * The meta object literal for the '<em><b>Last Name</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute AC_USER__LAST_NAME = eINSTANCE.getACUser_LastName();

		/**
		 * The meta object literal for the '<em><b>Effective Groups</b></em>' containment reference list feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference AC_USER__EFFECTIVE_GROUPS = eINSTANCE.getACUser_EffectiveGroups();

		/**
		 * The meta object literal for the '<em><b>Password</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute AC_USER__PASSWORD = eINSTANCE.getACUser_Password();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.ACOrgUnitImpl <em>AC Org Unit</em>}'
		 * class.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.ACOrgUnitImpl
		 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.AccesscontrolPackageImpl#getACOrgUnit()
		 * @generated
		 */
		EClass AC_ORG_UNIT = eINSTANCE.getACOrgUnit();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute AC_ORG_UNIT__NAME = eINSTANCE.getACOrgUnit_Name();

		/**
		 * The meta object literal for the '<em><b>Roles</b></em>' containment reference list feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference AC_ORG_UNIT__ROLES = eINSTANCE.getACOrgUnit_Roles();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute AC_ORG_UNIT__DESCRIPTION = eINSTANCE.getACOrgUnit_Description();

		/**
		 * The meta object literal for the '<em><b>Properties</b></em>' containment reference list feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference AC_ORG_UNIT__PROPERTIES = eINSTANCE.getACOrgUnit_Properties();

		/**
		 * The meta object literal for the '<em><b>Created By</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute AC_ORG_UNIT__CREATED_BY = eINSTANCE.getACOrgUnit_CreatedBy();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.ACGroupImpl
		 * <em>AC Group</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.ACGroupImpl
		 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.AccesscontrolPackageImpl#getACGroup()
		 * @generated
		 */
		EClass AC_GROUP = eINSTANCE.getACGroup();

		/**
		 * The meta object literal for the '<em><b>Members</b></em>' reference list feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference AC_GROUP__MEMBERS = eINSTANCE.getACGroup_Members();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.ACOrgUnitIdImpl
		 * <em>AC Org Unit Id</em>}' class.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.ACOrgUnitIdImpl
		 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.AccesscontrolPackageImpl#getACOrgUnitId()
		 * @generated
		 */
		EClass AC_ORG_UNIT_ID = eINSTANCE.getACOrgUnitId();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.OrgUnitPropertyImpl
		 * <em>Org Unit Property</em>}' class.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.OrgUnitPropertyImpl
		 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.impl.AccesscontrolPackageImpl#getOrgUnitProperty()
		 * @generated
		 */
		EClass ORG_UNIT_PROPERTY = eINSTANCE.getOrgUnitProperty();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute ORG_UNIT_PROPERTY__NAME = eINSTANCE.getOrgUnitProperty_Name();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute ORG_UNIT_PROPERTY__VALUE = eINSTANCE.getOrgUnitProperty_Value();

		/**
		 * The meta object literal for the '<em><b>Project</b></em>' containment reference feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference ORG_UNIT_PROPERTY__PROJECT = eINSTANCE.getOrgUnitProperty_Project();

	}

} // AccesscontrolPackage