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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.emfstore.internal.common.model.ModelPackage;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.AccesscontrolFactory;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.AccesscontrolPackage;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.OrgUnitProperty;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.RolesPackage;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.impl.RolesPackageImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.ModelPackageImpl;
import org.eclipse.emf.emfstore.internal.server.model.url.UrlPackage;
import org.eclipse.emf.emfstore.internal.server.model.url.impl.UrlPackageImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.events.EventsPackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.events.impl.EventsPackageImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.events.server.ServerPackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.events.server.impl.ServerPackageImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.OperationsPackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.impl.OperationsPackageImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.semantic.SemanticPackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.semantic.impl.SemanticPackageImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!--
 * end-user-doc -->
 *
 * @generated
 */
public class AccesscontrolPackageImpl extends EPackageImpl implements AccesscontrolPackage {
	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	private EClass acUserEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	private EClass acOrgUnitEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	private EClass acGroupEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	private EClass acOrgUnitIdEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	private EClass orgUnitPropertyEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
	 * EPackage.Registry} by the
	 * package package URI value.
	 * <p>
	 * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
	 * performs initialization of the package, or returns the registered package, if one already exists. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.emf.emfstore.internal.server.model.accesscontrol.AccesscontrolPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private AccesscontrolPackageImpl() {
		super(eNS_URI, AccesscontrolFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model,
	 * and for any others upon which it depends.
	 * <p>
	 * This method is used to initialize {@link AccesscontrolPackage#eINSTANCE} when that field is accessed. Clients
	 * should not invoke it directly. Instead, they should simply access that field to obtain the package. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static AccesscontrolPackage init() {
		if (isInited) {
			return (AccesscontrolPackage) EPackage.Registry.INSTANCE.getEPackage(AccesscontrolPackage.eNS_URI);
		}

		// Obtain or create and register package
		final AccesscontrolPackageImpl theAccesscontrolPackage = (AccesscontrolPackageImpl) (EPackage.Registry.INSTANCE
			.get(eNS_URI) instanceof AccesscontrolPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI)
				: new AccesscontrolPackageImpl());

		isInited = true;

		// Initialize simple dependencies
		ModelPackage.eINSTANCE.eClass();

		// Obtain or create and register interdependencies
		final ModelPackageImpl theModelPackage_1 = (ModelPackageImpl) (EPackage.Registry.INSTANCE.getEPackage(
			org.eclipse.emf.emfstore.internal.server.model.ModelPackage.eNS_URI) instanceof ModelPackageImpl
				? EPackage.Registry.INSTANCE
					.getEPackage(org.eclipse.emf.emfstore.internal.server.model.ModelPackage.eNS_URI)
				: org.eclipse.emf.emfstore.internal.server.model.ModelPackage.eINSTANCE);
		final VersioningPackageImpl theVersioningPackage = (VersioningPackageImpl) (EPackage.Registry.INSTANCE
			.getEPackage(VersioningPackage.eNS_URI) instanceof VersioningPackageImpl
				? EPackage.Registry.INSTANCE.getEPackage(VersioningPackage.eNS_URI) : VersioningPackage.eINSTANCE);
		final OperationsPackageImpl theOperationsPackage = (OperationsPackageImpl) (EPackage.Registry.INSTANCE
			.getEPackage(OperationsPackage.eNS_URI) instanceof OperationsPackageImpl
				? EPackage.Registry.INSTANCE.getEPackage(OperationsPackage.eNS_URI) : OperationsPackage.eINSTANCE);
		final SemanticPackageImpl theSemanticPackage = (SemanticPackageImpl) (EPackage.Registry.INSTANCE
			.getEPackage(SemanticPackage.eNS_URI) instanceof SemanticPackageImpl
				? EPackage.Registry.INSTANCE.getEPackage(SemanticPackage.eNS_URI) : SemanticPackage.eINSTANCE);
		final EventsPackageImpl theEventsPackage = (EventsPackageImpl) (EPackage.Registry.INSTANCE
			.getEPackage(EventsPackage.eNS_URI) instanceof EventsPackageImpl
				? EPackage.Registry.INSTANCE.getEPackage(EventsPackage.eNS_URI) : EventsPackage.eINSTANCE);
		final ServerPackageImpl theServerPackage = (ServerPackageImpl) (EPackage.Registry.INSTANCE
			.getEPackage(ServerPackage.eNS_URI) instanceof ServerPackageImpl
				? EPackage.Registry.INSTANCE.getEPackage(ServerPackage.eNS_URI) : ServerPackage.eINSTANCE);
		final RolesPackageImpl theRolesPackage = (RolesPackageImpl) (EPackage.Registry.INSTANCE
			.getEPackage(RolesPackage.eNS_URI) instanceof RolesPackageImpl
				? EPackage.Registry.INSTANCE.getEPackage(RolesPackage.eNS_URI) : RolesPackage.eINSTANCE);
		final UrlPackageImpl theUrlPackage = (UrlPackageImpl) (EPackage.Registry.INSTANCE
			.getEPackage(UrlPackage.eNS_URI) instanceof UrlPackageImpl
				? EPackage.Registry.INSTANCE.getEPackage(UrlPackage.eNS_URI) : UrlPackage.eINSTANCE);

		// Create package meta-data objects
		theAccesscontrolPackage.createPackageContents();
		theModelPackage_1.createPackageContents();
		theVersioningPackage.createPackageContents();
		theOperationsPackage.createPackageContents();
		theSemanticPackage.createPackageContents();
		theEventsPackage.createPackageContents();
		theServerPackage.createPackageContents();
		theRolesPackage.createPackageContents();
		theUrlPackage.createPackageContents();

		// Initialize created meta-data
		theAccesscontrolPackage.initializePackageContents();
		theModelPackage_1.initializePackageContents();
		theVersioningPackage.initializePackageContents();
		theOperationsPackage.initializePackageContents();
		theSemanticPackage.initializePackageContents();
		theEventsPackage.initializePackageContents();
		theServerPackage.initializePackageContents();
		theRolesPackage.initializePackageContents();
		theUrlPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theAccesscontrolPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(AccesscontrolPackage.eNS_URI, theAccesscontrolPackage);
		return theAccesscontrolPackage;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EClass getACUser() {
		return acUserEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EAttribute getACUser_FirstName() {
		return (EAttribute) acUserEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EAttribute getACUser_LastName() {
		return (EAttribute) acUserEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EReference getACUser_EffectiveGroups() {
		return (EReference) acUserEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EAttribute getACUser_Password() {
		return (EAttribute) acUserEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EClass getACOrgUnit() {
		return acOrgUnitEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EAttribute getACOrgUnit_Name() {
		return (EAttribute) acOrgUnitEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EReference getACOrgUnit_Roles() {
		return (EReference) acOrgUnitEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EAttribute getACOrgUnit_Description() {
		return (EAttribute) acOrgUnitEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EReference getACOrgUnit_Properties() {
		return (EReference) acOrgUnitEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EAttribute getACOrgUnit_CreatedBy() {
		return (EAttribute) acOrgUnitEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EClass getACGroup() {
		return acGroupEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EReference getACGroup_Members() {
		return (EReference) acGroupEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EClass getACOrgUnitId() {
		return acOrgUnitIdEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EClass getOrgUnitProperty() {
		return orgUnitPropertyEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EAttribute getOrgUnitProperty_Name() {
		return (EAttribute) orgUnitPropertyEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EAttribute getOrgUnitProperty_Value() {
		return (EAttribute) orgUnitPropertyEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EReference getOrgUnitProperty_Project() {
		return (EReference) orgUnitPropertyEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public AccesscontrolFactory getAccesscontrolFactory() {
		return (AccesscontrolFactory) getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package. This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) {
			return;
		}
		isCreated = true;

		// Create classes and their features
		acUserEClass = createEClass(AC_USER);
		createEAttribute(acUserEClass, AC_USER__FIRST_NAME);
		createEAttribute(acUserEClass, AC_USER__LAST_NAME);
		createEReference(acUserEClass, AC_USER__EFFECTIVE_GROUPS);
		createEAttribute(acUserEClass, AC_USER__PASSWORD);

		acOrgUnitEClass = createEClass(AC_ORG_UNIT);
		createEAttribute(acOrgUnitEClass, AC_ORG_UNIT__NAME);
		createEReference(acOrgUnitEClass, AC_ORG_UNIT__ROLES);
		createEAttribute(acOrgUnitEClass, AC_ORG_UNIT__DESCRIPTION);
		createEReference(acOrgUnitEClass, AC_ORG_UNIT__PROPERTIES);
		createEAttribute(acOrgUnitEClass, AC_ORG_UNIT__CREATED_BY);

		acGroupEClass = createEClass(AC_GROUP);
		createEReference(acGroupEClass, AC_GROUP__MEMBERS);

		acOrgUnitIdEClass = createEClass(AC_ORG_UNIT_ID);

		orgUnitPropertyEClass = createEClass(ORG_UNIT_PROPERTY);
		createEAttribute(orgUnitPropertyEClass, ORG_UNIT_PROPERTY__NAME);
		createEAttribute(orgUnitPropertyEClass, ORG_UNIT_PROPERTY__VALUE);
		createEReference(orgUnitPropertyEClass, ORG_UNIT_PROPERTY__PROJECT);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model. This
	 * method is guarded to have no affect on any invocation but its first. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) {
			return;
		}
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		final RolesPackage theRolesPackage = (RolesPackage) EPackage.Registry.INSTANCE
			.getEPackage(RolesPackage.eNS_URI);
		final ModelPackage theModelPackage = (ModelPackage) EPackage.Registry.INSTANCE
			.getEPackage(ModelPackage.eNS_URI);
		final org.eclipse.emf.emfstore.internal.server.model.ModelPackage theModelPackage_1 = (org.eclipse.emf.emfstore.internal.server.model.ModelPackage) EPackage.Registry.INSTANCE
			.getEPackage(org.eclipse.emf.emfstore.internal.server.model.ModelPackage.eNS_URI);

		// Add subpackages
		getESubpackages().add(theRolesPackage);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		acUserEClass.getESuperTypes().add(getACOrgUnit());
		acOrgUnitEClass.getESuperTypes().add(theModelPackage.getIdentifiableElement());
		acGroupEClass.getESuperTypes().add(getACOrgUnit());
		acOrgUnitIdEClass.getESuperTypes().add(theModelPackage.getUniqueIdentifier());

		// Initialize classes and features; add operations and parameters
		initEClass(acUserEClass, ACUser.class, "ACUser", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getACUser_FirstName(), ecorePackage.getEString(), "firstName", null, 0, 1, ACUser.class, //$NON-NLS-1$
			!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getACUser_LastName(), ecorePackage.getEString(), "lastName", null, 0, 1, ACUser.class, //$NON-NLS-1$
			!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getACUser_EffectiveGroups(), getACGroup(), null, "effectiveGroups", null, 0, -1, //$NON-NLS-1$
			ACUser.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
			IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getACUser_Password(), ecorePackage.getEString(), "password", null, 0, 1, ACUser.class, //$NON-NLS-1$
			!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(acOrgUnitEClass, ACOrgUnit.class, "ACOrgUnit", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
			IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getACOrgUnit_Name(), ecorePackage.getEString(), "name", null, 1, 1, ACOrgUnit.class, //$NON-NLS-1$
			!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getACOrgUnit_Roles(), theRolesPackage.getRole(), null, "roles", null, 0, -1, ACOrgUnit.class, //$NON-NLS-1$
			!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
			!IS_DERIVED, IS_ORDERED);
		initEAttribute(getACOrgUnit_Description(), ecorePackage.getEString(), "description", null, 0, 1, //$NON-NLS-1$
			ACOrgUnit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
			IS_ORDERED);
		initEReference(getACOrgUnit_Properties(), getOrgUnitProperty(), null, "properties", null, 0, -1, //$NON-NLS-1$
			ACOrgUnit.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
			!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getACOrgUnit_CreatedBy(), ecorePackage.getEString(), "createdBy", null, 0, 1, ACOrgUnit.class, //$NON-NLS-1$
			!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		addEOperation(acOrgUnitEClass, getACOrgUnitId(), "getId", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

		initEClass(acGroupEClass, ACGroup.class, "ACGroup", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(getACGroup_Members(), getACOrgUnit(), null, "members", null, 0, -1, ACGroup.class, //$NON-NLS-1$
			!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
			!IS_DERIVED, IS_ORDERED);
		getACGroup_Members().getEKeys().add(theModelPackage.getIdentifiableElement_Identifier());

		initEClass(acOrgUnitIdEClass, ACOrgUnitId.class, "ACOrgUnitId", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
			IS_GENERATED_INSTANCE_CLASS);

		initEClass(orgUnitPropertyEClass, OrgUnitProperty.class, "OrgUnitProperty", !IS_ABSTRACT, !IS_INTERFACE, //$NON-NLS-1$
			IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getOrgUnitProperty_Name(), ecorePackage.getEString(), "name", null, 0, 1, OrgUnitProperty.class, //$NON-NLS-1$
			!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getOrgUnitProperty_Value(), ecorePackage.getEString(), "value", null, 0, 1, //$NON-NLS-1$
			OrgUnitProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
			!IS_DERIVED, IS_ORDERED);
		initEReference(getOrgUnitProperty_Project(), theModelPackage_1.getProjectId(), null, "project", null, 0, 1, //$NON-NLS-1$
			OrgUnitProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES,
			!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		getOrgUnitProperty_Project().getEKeys().add(theModelPackage.getUniqueIdentifier_Id());
	}

} // AccesscontrolPackageImpl