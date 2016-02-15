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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

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
 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningFactory
 * @model kind="package"
 * @generated
 */
public interface VersioningPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	String eNAME = "versioning"; //$NON-NLS-1$

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	String eNS_URI = "http://eclipse.org/emf/emfstore/server/model/versioning"; //$NON-NLS-1$

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	String eNS_PREFIX = "org.eclipse.emf.emfstore.internal.server.model.versioning"; //$NON-NLS-1$

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @generated
	 */
	VersioningPackage eINSTANCE = org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl
		.init();

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersionSpecImpl
	 * <em>Version Spec</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersionSpecImpl
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getVersionSpec()
	 * @generated
	 */
	int VERSION_SPEC = 3;

	/**
	 * The feature id for the '<em><b>Branch</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int VERSION_SPEC__BRANCH = 0;

	/**
	 * The number of structural features of the '<em>Version Spec</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int VERSION_SPEC_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.TagVersionSpecImpl
	 * <em>Tag Version Spec</em>}' class.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.TagVersionSpecImpl
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getTagVersionSpec()
	 * @generated
	 */
	int TAG_VERSION_SPEC = 0;

	/**
	 * The feature id for the '<em><b>Branch</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int TAG_VERSION_SPEC__BRANCH = VERSION_SPEC__BRANCH;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int TAG_VERSION_SPEC__NAME = VERSION_SPEC_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Tag Version Spec</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int TAG_VERSION_SPEC_FEATURE_COUNT = VERSION_SPEC_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.DateVersionSpecImpl
	 * <em>Date Version Spec</em>}' class.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.DateVersionSpecImpl
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getDateVersionSpec()
	 * @generated
	 */
	int DATE_VERSION_SPEC = 1;

	/**
	 * The feature id for the '<em><b>Branch</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int DATE_VERSION_SPEC__BRANCH = VERSION_SPEC__BRANCH;

	/**
	 * The feature id for the '<em><b>Date</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int DATE_VERSION_SPEC__DATE = VERSION_SPEC_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Date Version Spec</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int DATE_VERSION_SPEC_FEATURE_COUNT = VERSION_SPEC_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.PrimaryVersionSpecImpl
	 * <em>Primary Version Spec</em>}' class.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.PrimaryVersionSpecImpl
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getPrimaryVersionSpec()
	 * @generated
	 */
	int PRIMARY_VERSION_SPEC = 2;

	/**
	 * The feature id for the '<em><b>Branch</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int PRIMARY_VERSION_SPEC__BRANCH = VERSION_SPEC__BRANCH;

	/**
	 * The feature id for the '<em><b>Identifier</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int PRIMARY_VERSION_SPEC__IDENTIFIER = VERSION_SPEC_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Project State Checksum</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int PRIMARY_VERSION_SPEC__PROJECT_STATE_CHECKSUM = VERSION_SPEC_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Primary Version Spec</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int PRIMARY_VERSION_SPEC_FEATURE_COUNT = VERSION_SPEC_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.LogMessageImpl
	 * <em>Log Message</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.LogMessageImpl
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getLogMessage()
	 * @generated
	 */
	int LOG_MESSAGE = 4;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int LOG_MESSAGE__AUTHOR = 0;

	/**
	 * The feature id for the '<em><b>Message</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int LOG_MESSAGE__MESSAGE = 1;

	/**
	 * The feature id for the '<em><b>Date</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int LOG_MESSAGE__DATE = 2;

	/**
	 * The feature id for the '<em><b>Client Date</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int LOG_MESSAGE__CLIENT_DATE = 3;

	/**
	 * The number of structural features of the '<em>Log Message</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int LOG_MESSAGE_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage
	 * <em>Abstract Change Package</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getAbstractChangePackage()
	 * @generated
	 */
	int ABSTRACT_CHANGE_PACKAGE = 18;

	/**
	 * The feature id for the '<em><b>Log Message</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_CHANGE_PACKAGE__LOG_MESSAGE = 0;

	/**
	 * The number of structural features of the '<em>Abstract Change Package</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_CHANGE_PACKAGE_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.ChangePackageImpl <em>Change Package</em>}'
	 * class.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.ChangePackageImpl
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getChangePackage()
	 * @generated
	 */
	int CHANGE_PACKAGE = 5;

	/**
	 * The feature id for the '<em><b>Log Message</b></em>' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int CHANGE_PACKAGE__LOG_MESSAGE = ABSTRACT_CHANGE_PACKAGE__LOG_MESSAGE;

	/**
	 * The feature id for the '<em><b>Operations</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int CHANGE_PACKAGE__OPERATIONS = ABSTRACT_CHANGE_PACKAGE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Events</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int CHANGE_PACKAGE__EVENTS = ABSTRACT_CHANGE_PACKAGE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Version Properties</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int CHANGE_PACKAGE__VERSION_PROPERTIES = ABSTRACT_CHANGE_PACKAGE_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Change Package</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int CHANGE_PACKAGE_FEATURE_COUNT = ABSTRACT_CHANGE_PACKAGE_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.HistoryInfoImpl
	 * <em>History Info</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.HistoryInfoImpl
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getHistoryInfo()
	 * @generated
	 */
	int HISTORY_INFO = 6;

	/**
	 * The feature id for the '<em><b>Primary Spec</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int HISTORY_INFO__PRIMARY_SPEC = 0;

	/**
	 * The feature id for the '<em><b>Next Spec</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int HISTORY_INFO__NEXT_SPEC = 1;

	/**
	 * The feature id for the '<em><b>Previous Spec</b></em>' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int HISTORY_INFO__PREVIOUS_SPEC = 2;

	/**
	 * The feature id for the '<em><b>Merged From</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int HISTORY_INFO__MERGED_FROM = 3;

	/**
	 * The feature id for the '<em><b>Merged To</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int HISTORY_INFO__MERGED_TO = 4;

	/**
	 * The feature id for the '<em><b>Log Message</b></em>' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int HISTORY_INFO__LOG_MESSAGE = 5;

	/**
	 * The feature id for the '<em><b>Tag Specs</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int HISTORY_INFO__TAG_SPECS = 6;

	/**
	 * The feature id for the '<em><b>Version Properties</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int HISTORY_INFO__VERSION_PROPERTIES = 7;

	/**
	 * The feature id for the '<em><b>Change Package</b></em>' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int HISTORY_INFO__CHANGE_PACKAGE = 8;

	/**
	 * The number of structural features of the '<em>History Info</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int HISTORY_INFO_FEATURE_COUNT = 9;

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.HistoryQueryImpl
	 * <em>History Query</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.HistoryQueryImpl
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getHistoryQuery()
	 * @generated
	 */
	int HISTORY_QUERY = 7;

	/**
	 * The feature id for the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int HISTORY_QUERY__SOURCE = 0;

	/**
	 * The feature id for the '<em><b>Include Change Packages</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int HISTORY_QUERY__INCLUDE_CHANGE_PACKAGES = 1;

	/**
	 * The feature id for the '<em><b>Include All Versions</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int HISTORY_QUERY__INCLUDE_ALL_VERSIONS = 2;

	/**
	 * The number of structural features of the '<em>History Query</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int HISTORY_QUERY_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.RangeQueryImpl
	 * <em>Range Query</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.RangeQueryImpl
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getRangeQuery()
	 * @generated
	 */
	int RANGE_QUERY = 8;

	/**
	 * The feature id for the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int RANGE_QUERY__SOURCE = HISTORY_QUERY__SOURCE;

	/**
	 * The feature id for the '<em><b>Include Change Packages</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int RANGE_QUERY__INCLUDE_CHANGE_PACKAGES = HISTORY_QUERY__INCLUDE_CHANGE_PACKAGES;

	/**
	 * The feature id for the '<em><b>Include All Versions</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int RANGE_QUERY__INCLUDE_ALL_VERSIONS = HISTORY_QUERY__INCLUDE_ALL_VERSIONS;

	/**
	 * The feature id for the '<em><b>Upper Limit</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int RANGE_QUERY__UPPER_LIMIT = HISTORY_QUERY_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Lower Limit</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int RANGE_QUERY__LOWER_LIMIT = HISTORY_QUERY_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Include Incoming</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int RANGE_QUERY__INCLUDE_INCOMING = HISTORY_QUERY_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Include Outgoing</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int RANGE_QUERY__INCLUDE_OUTGOING = HISTORY_QUERY_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Range Query</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int RANGE_QUERY_FEATURE_COUNT = HISTORY_QUERY_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the ' {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.PathQueryImpl
	 * <em>Path Query</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.PathQueryImpl
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getPathQuery()
	 * @generated
	 */
	int PATH_QUERY = 9;

	/**
	 * The feature id for the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int PATH_QUERY__SOURCE = HISTORY_QUERY__SOURCE;

	/**
	 * The feature id for the '<em><b>Include Change Packages</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int PATH_QUERY__INCLUDE_CHANGE_PACKAGES = HISTORY_QUERY__INCLUDE_CHANGE_PACKAGES;

	/**
	 * The feature id for the '<em><b>Include All Versions</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int PATH_QUERY__INCLUDE_ALL_VERSIONS = HISTORY_QUERY__INCLUDE_ALL_VERSIONS;

	/**
	 * The feature id for the '<em><b>Target</b></em>' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int PATH_QUERY__TARGET = HISTORY_QUERY_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Path Query</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int PATH_QUERY_FEATURE_COUNT = HISTORY_QUERY_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.ModelElementQueryImpl
	 * <em>Model Element Query</em>}' class.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.ModelElementQueryImpl
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getModelElementQuery()
	 * @generated
	 */
	int MODEL_ELEMENT_QUERY = 10;

	/**
	 * The feature id for the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int MODEL_ELEMENT_QUERY__SOURCE = RANGE_QUERY__SOURCE;

	/**
	 * The feature id for the '<em><b>Include Change Packages</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int MODEL_ELEMENT_QUERY__INCLUDE_CHANGE_PACKAGES = RANGE_QUERY__INCLUDE_CHANGE_PACKAGES;

	/**
	 * The feature id for the '<em><b>Include All Versions</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int MODEL_ELEMENT_QUERY__INCLUDE_ALL_VERSIONS = RANGE_QUERY__INCLUDE_ALL_VERSIONS;

	/**
	 * The feature id for the '<em><b>Upper Limit</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int MODEL_ELEMENT_QUERY__UPPER_LIMIT = RANGE_QUERY__UPPER_LIMIT;

	/**
	 * The feature id for the '<em><b>Lower Limit</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int MODEL_ELEMENT_QUERY__LOWER_LIMIT = RANGE_QUERY__LOWER_LIMIT;

	/**
	 * The feature id for the '<em><b>Include Incoming</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int MODEL_ELEMENT_QUERY__INCLUDE_INCOMING = RANGE_QUERY__INCLUDE_INCOMING;

	/**
	 * The feature id for the '<em><b>Include Outgoing</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int MODEL_ELEMENT_QUERY__INCLUDE_OUTGOING = RANGE_QUERY__INCLUDE_OUTGOING;

	/**
	 * The feature id for the '<em><b>Model Elements</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int MODEL_ELEMENT_QUERY__MODEL_ELEMENTS = RANGE_QUERY_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Model Element Query</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int MODEL_ELEMENT_QUERY_FEATURE_COUNT = RANGE_QUERY_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersionImpl
	 * <em>Version</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersionImpl
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getVersion()
	 * @generated
	 */
	int VERSION = 11;

	/**
	 * The feature id for the '<em><b>Primary Spec</b></em>' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int VERSION__PRIMARY_SPEC = 0;

	/**
	 * The feature id for the '<em><b>Tag Specs</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int VERSION__TAG_SPECS = 1;

	/**
	 * The feature id for the '<em><b>Next Version</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int VERSION__NEXT_VERSION = 2;

	/**
	 * The feature id for the '<em><b>Previous Version</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int VERSION__PREVIOUS_VERSION = 3;

	/**
	 * The feature id for the '<em><b>Log Message</b></em>' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int VERSION__LOG_MESSAGE = 4;

	/**
	 * The feature id for the '<em><b>Ancestor Version</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int VERSION__ANCESTOR_VERSION = 5;

	/**
	 * The feature id for the '<em><b>Branched Versions</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int VERSION__BRANCHED_VERSIONS = 6;

	/**
	 * The feature id for the '<em><b>Merged To Version</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int VERSION__MERGED_TO_VERSION = 7;

	/**
	 * The feature id for the '<em><b>Merged From Version</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int VERSION__MERGED_FROM_VERSION = 8;

	/**
	 * The number of structural features of the '<em>Version</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int VERSION_FEATURE_COUNT = 9;

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.HeadVersionSpecImpl
	 * <em>Head Version Spec</em>}' class.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.HeadVersionSpecImpl
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getHeadVersionSpec()
	 * @generated
	 */
	int HEAD_VERSION_SPEC = 12;

	/**
	 * The feature id for the '<em><b>Branch</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int HEAD_VERSION_SPEC__BRANCH = VERSION_SPEC__BRANCH;

	/**
	 * The number of structural features of the '<em>Head Version Spec</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int HEAD_VERSION_SPEC_FEATURE_COUNT = VERSION_SPEC_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersionPropertyImpl
	 * <em>Version Property</em>}' class.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersionPropertyImpl
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getVersionProperty()
	 * @generated
	 */
	int VERSION_PROPERTY = 13;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int VERSION_PROPERTY__NAME = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int VERSION_PROPERTY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Version Property</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int VERSION_PROPERTY_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.BranchVersionSpecImpl
	 * <em>Branch Version Spec</em>}' class.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.BranchVersionSpecImpl
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getBranchVersionSpec()
	 * @generated
	 */
	int BRANCH_VERSION_SPEC = 14;

	/**
	 * The feature id for the '<em><b>Branch</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int BRANCH_VERSION_SPEC__BRANCH = VERSION_SPEC__BRANCH;

	/**
	 * The number of structural features of the '<em>Branch Version Spec</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int BRANCH_VERSION_SPEC_FEATURE_COUNT = VERSION_SPEC_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.BranchInfoImpl
	 * <em>Branch Info</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.BranchInfoImpl
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getBranchInfo()
	 * @generated
	 */
	int BRANCH_INFO = 15;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int BRANCH_INFO__NAME = 0;

	/**
	 * The feature id for the '<em><b>Head</b></em>' containment reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int BRANCH_INFO__HEAD = 1;

	/**
	 * The feature id for the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int BRANCH_INFO__SOURCE = 2;

	/**
	 * The number of structural features of the '<em>Branch Info</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int BRANCH_INFO_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.AncestorVersionSpecImpl
	 * <em>Ancestor Version Spec</em>}' class.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.AncestorVersionSpecImpl
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getAncestorVersionSpec()
	 * @generated
	 */
	int ANCESTOR_VERSION_SPEC = 16;

	/**
	 * The feature id for the '<em><b>Branch</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int ANCESTOR_VERSION_SPEC__BRANCH = VERSION_SPEC__BRANCH;

	/**
	 * The feature id for the '<em><b>Target</b></em>' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int ANCESTOR_VERSION_SPEC__TARGET = VERSION_SPEC_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Source</b></em>' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int ANCESTOR_VERSION_SPEC__SOURCE = VERSION_SPEC_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Ancestor Version Spec</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int ANCESTOR_VERSION_SPEC_FEATURE_COUNT = VERSION_SPEC_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.PagedUpdateVersionSpecImpl
	 * <em>Paged Update Version Spec</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.PagedUpdateVersionSpecImpl
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getPagedUpdateVersionSpec()
	 * @generated
	 */
	int PAGED_UPDATE_VERSION_SPEC = 17;

	/**
	 * The feature id for the '<em><b>Branch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int PAGED_UPDATE_VERSION_SPEC__BRANCH = VERSION_SPEC__BRANCH;

	/**
	 * The feature id for the '<em><b>Max Changes</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int PAGED_UPDATE_VERSION_SPEC__MAX_CHANGES = VERSION_SPEC_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Base Version Spec</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int PAGED_UPDATE_VERSION_SPEC__BASE_VERSION_SPEC = VERSION_SPEC_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Paged Update Version Spec</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int PAGED_UPDATE_VERSION_SPEC_FEATURE_COUNT = VERSION_SPEC_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.FileBasedChangePackageImpl
	 * <em>File Based Change Package</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.FileBasedChangePackageImpl
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getFileBasedChangePackage()
	 * @generated
	 */
	int FILE_BASED_CHANGE_PACKAGE = 19;

	/**
	 * The feature id for the '<em><b>Log Message</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int FILE_BASED_CHANGE_PACKAGE__LOG_MESSAGE = ABSTRACT_CHANGE_PACKAGE__LOG_MESSAGE;

	/**
	 * The feature id for the '<em><b>File Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int FILE_BASED_CHANGE_PACKAGE__FILE_PATH = ABSTRACT_CHANGE_PACKAGE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Operation Proxies</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int FILE_BASED_CHANGE_PACKAGE__OPERATION_PROXIES = ABSTRACT_CHANGE_PACKAGE_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>File Based Change Package</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int FILE_BASED_CHANGE_PACKAGE_FEATURE_COUNT = ABSTRACT_CHANGE_PACKAGE_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.OperationProxyImpl <em>Operation Proxy</em>
	 * }' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.OperationProxyImpl
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getOperationProxy()
	 * @generated
	 */
	int OPERATION_PROXY = 20;

	/**
	 * The feature id for the '<em><b>Proxies</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int OPERATION_PROXY__PROXIES = 0;

	/**
	 * The number of structural features of the '<em>Operation Proxy</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int OPERATION_PROXY_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.ChangePackageEnvelopeImpl
	 * <em>Change Package Envelope</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.ChangePackageEnvelopeImpl
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getChangePackageEnvelope()
	 * @generated
	 */
	int CHANGE_PACKAGE_ENVELOPE = 21;

	/**
	 * The feature id for the '<em><b>Fragment Index</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int CHANGE_PACKAGE_ENVELOPE__FRAGMENT_INDEX = 0;

	/**
	 * The feature id for the '<em><b>Fragment Count</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int CHANGE_PACKAGE_ENVELOPE__FRAGMENT_COUNT = 1;

	/**
	 * The feature id for the '<em><b>Fragment</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int CHANGE_PACKAGE_ENVELOPE__FRAGMENT = 2;

	/**
	 * The number of structural features of the '<em>Change Package Envelope</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int CHANGE_PACKAGE_ENVELOPE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.ChangePackageProxyImpl
	 * <em>Change Package Proxy</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.ChangePackageProxyImpl
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getChangePackageProxy()
	 * @generated
	 */
	int CHANGE_PACKAGE_PROXY = 22;

	/**
	 * The feature id for the '<em><b>Log Message</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int CHANGE_PACKAGE_PROXY__LOG_MESSAGE = ABSTRACT_CHANGE_PACKAGE__LOG_MESSAGE;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int CHANGE_PACKAGE_PROXY__ID = ABSTRACT_CHANGE_PACKAGE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Change Package Proxy</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	int CHANGE_PACKAGE_PROXY_FEATURE_COUNT = ABSTRACT_CHANGE_PACKAGE_FEATURE_COUNT + 1;

	/**
	 * Returns the meta object for class '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.TagVersionSpec
	 * <em>Tag Version Spec</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @return the meta object for class '<em>Tag Version Spec</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.TagVersionSpec
	 * @generated
	 */
	EClass getTagVersionSpec();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.TagVersionSpec#getName <em>Name</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.TagVersionSpec#getName()
	 * @see #getTagVersionSpec()
	 * @generated
	 */
	EAttribute getTagVersionSpec_Name();

	/**
	 * Returns the meta object for class '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.DateVersionSpec
	 * <em>Date Version Spec</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @return the meta object for class '<em>Date Version Spec</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.DateVersionSpec
	 * @generated
	 */
	EClass getDateVersionSpec();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.DateVersionSpec#getDate <em>Date</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Date</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.DateVersionSpec#getDate()
	 * @see #getDateVersionSpec()
	 * @generated
	 */
	EAttribute getDateVersionSpec_Date();

	/**
	 * Returns the meta object for class '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec <em>Primary Version Spec</em>
	 * }'.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @return the meta object for class '<em>Primary Version Spec</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec
	 * @generated
	 */
	EClass getPrimaryVersionSpec();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec#getIdentifier
	 * <em>Identifier</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Identifier</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec#getIdentifier()
	 * @see #getPrimaryVersionSpec()
	 * @generated
	 */
	EAttribute getPrimaryVersionSpec_Identifier();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec#getProjectStateChecksum
	 * <em>Project State Checksum</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Project State Checksum</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec#getProjectStateChecksum()
	 * @see #getPrimaryVersionSpec()
	 * @generated
	 */
	EAttribute getPrimaryVersionSpec_ProjectStateChecksum();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.emfstore.internal.server.model.versioning.VersionSpec
	 * <em>Version Spec</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Version Spec</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.VersionSpec
	 * @generated
	 */
	EClass getVersionSpec();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.VersionSpec#getBranch <em>Branch</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Branch</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.VersionSpec#getBranch()
	 * @see #getVersionSpec()
	 * @generated
	 */
	EAttribute getVersionSpec_Branch();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage
	 * <em>Log Message</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Log Message</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage
	 * @generated
	 */
	EClass getLogMessage();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage#getMessage <em>Message</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Message</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage#getMessage()
	 * @see #getLogMessage()
	 * @generated
	 */
	EAttribute getLogMessage_Message();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage#getDate <em>Date</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Date</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage#getDate()
	 * @see #getLogMessage()
	 * @generated
	 */
	EAttribute getLogMessage_Date();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage#getClientDate <em>Client Date</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Client Date</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage#getClientDate()
	 * @see #getLogMessage()
	 * @generated
	 */
	EAttribute getLogMessage_ClientDate();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage#getAuthor <em>Author</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Author</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage#getAuthor()
	 * @see #getLogMessage()
	 * @generated
	 */
	EAttribute getLogMessage_Author();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage
	 * <em>Change Package</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Change Package</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage
	 * @generated
	 */
	EClass getChangePackage();

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage#getOperations <em>Operations</em>}
	 * '.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference list '<em>Operations</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage#getOperations()
	 * @see #getChangePackage()
	 * @generated
	 */
	EReference getChangePackage_Operations();

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage#getEvents <em>Events</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference list '<em>Events</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage#getEvents()
	 * @see #getChangePackage()
	 * @generated
	 */
	EReference getChangePackage_Events();

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage#getVersionProperties
	 * <em>Version Properties</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @return the meta object for the containment reference list ' <em>Version Properties</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage#getVersionProperties()
	 * @see #getChangePackage()
	 * @generated
	 */
	EReference getChangePackage_VersionProperties();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo
	 * <em>History Info</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>History Info</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo
	 * @generated
	 */
	EClass getHistoryInfo();

	/**
	 * Returns the meta object for the containment reference '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo#getPrimarySpec <em>Primary Spec</em>
	 * }'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference '<em>Primary Spec</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo#getPrimarySpec()
	 * @see #getHistoryInfo()
	 * @generated
	 */
	EReference getHistoryInfo_PrimarySpec();

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo#getNextSpec <em>Next Spec</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference list '<em>Next Spec</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo#getNextSpec()
	 * @see #getHistoryInfo()
	 * @generated
	 */
	EReference getHistoryInfo_NextSpec();

	/**
	 * Returns the meta object for the containment reference '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo#getPreviousSpec
	 * <em>Previous Spec</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference '<em>Previous Spec</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo#getPreviousSpec()
	 * @see #getHistoryInfo()
	 * @generated
	 */
	EReference getHistoryInfo_PreviousSpec();

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo#getMergedFrom <em>Merged From</em>}
	 * '.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference list '<em>Merged From</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo#getMergedFrom()
	 * @see #getHistoryInfo()
	 * @generated
	 */
	EReference getHistoryInfo_MergedFrom();

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo#getMergedTo <em>Merged To</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference list '<em>Merged To</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo#getMergedTo()
	 * @see #getHistoryInfo()
	 * @generated
	 */
	EReference getHistoryInfo_MergedTo();

	/**
	 * Returns the meta object for the containment reference '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo#getLogMessage <em>Log Message</em>}
	 * '.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference '<em>Log Message</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo#getLogMessage()
	 * @see #getHistoryInfo()
	 * @generated
	 */
	EReference getHistoryInfo_LogMessage();

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo#getTagSpecs <em>Tag Specs</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference list '<em>Tag Specs</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo#getTagSpecs()
	 * @see #getHistoryInfo()
	 * @generated
	 */
	EReference getHistoryInfo_TagSpecs();

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo#getVersionProperties
	 * <em>Version Properties</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @return the meta object for the containment reference list ' <em>Version Properties</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo#getVersionProperties()
	 * @see #getHistoryInfo()
	 * @generated
	 */
	EReference getHistoryInfo_VersionProperties();

	/**
	 * Returns the meta object for the containment reference '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo#getChangePackage
	 * <em>Change Package</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference '<em>Change Package</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo#getChangePackage()
	 * @see #getHistoryInfo()
	 * @generated
	 */
	EReference getHistoryInfo_ChangePackage();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryQuery
	 * <em>History Query</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>History Query</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryQuery
	 * @generated
	 */
	EClass getHistoryQuery();

	/**
	 * Returns the meta object for the containment reference '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryQuery#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference '<em>Source</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryQuery#getSource()
	 * @see #getHistoryQuery()
	 * @generated
	 */
	EReference getHistoryQuery_Source();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryQuery#isIncludeChangePackages
	 * <em>Include Change Packages</em>}'.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Include Change Packages</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryQuery#isIncludeChangePackages()
	 * @see #getHistoryQuery()
	 * @generated
	 */
	EAttribute getHistoryQuery_IncludeChangePackages();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryQuery#isIncludeAllVersions
	 * <em>Include All Versions</em>}'.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Include All Versions</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryQuery#isIncludeAllVersions()
	 * @see #getHistoryQuery()
	 * @generated
	 */
	EAttribute getHistoryQuery_IncludeAllVersions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.emfstore.internal.server.model.versioning.RangeQuery
	 * <em>Range Query</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Range Query</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.RangeQuery
	 * @generated
	 */
	EClass getRangeQuery();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.RangeQuery#getUpperLimit <em>Upper Limit</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Upper Limit</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.RangeQuery#getUpperLimit()
	 * @see #getRangeQuery()
	 * @generated
	 */
	EAttribute getRangeQuery_UpperLimit();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.RangeQuery#getLowerLimit <em>Lower Limit</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Lower Limit</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.RangeQuery#getLowerLimit()
	 * @see #getRangeQuery()
	 * @generated
	 */
	EAttribute getRangeQuery_LowerLimit();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.RangeQuery#isIncludeIncoming
	 * <em>Include Incoming</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @return the meta object for the attribute '<em>Include Incoming</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.RangeQuery#isIncludeIncoming()
	 * @see #getRangeQuery()
	 * @generated
	 */
	EAttribute getRangeQuery_IncludeIncoming();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.RangeQuery#isIncludeOutgoing
	 * <em>Include Outgoing</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @return the meta object for the attribute '<em>Include Outgoing</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.RangeQuery#isIncludeOutgoing()
	 * @see #getRangeQuery()
	 * @generated
	 */
	EAttribute getRangeQuery_IncludeOutgoing();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.emfstore.internal.server.model.versioning.PathQuery
	 * <em>Path Query</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Path Query</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.PathQuery
	 * @generated
	 */
	EClass getPathQuery();

	/**
	 * Returns the meta object for the containment reference '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.PathQuery#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference '<em>Target</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.PathQuery#getTarget()
	 * @see #getPathQuery()
	 * @generated
	 */
	EReference getPathQuery_Target();

	/**
	 * Returns the meta object for class '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.ModelElementQuery
	 * <em>Model Element Query</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @return the meta object for class '<em>Model Element Query</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.ModelElementQuery
	 * @generated
	 */
	EClass getModelElementQuery();

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.ModelElementQuery#getModelElements
	 * <em>Model Elements</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference list '<em>Model Elements</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.ModelElementQuery#getModelElements()
	 * @see #getModelElementQuery()
	 * @generated
	 */
	EReference getModelElementQuery_ModelElements();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.emfstore.internal.server.model.versioning.Version
	 * <em>Version</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Version</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.Version
	 * @generated
	 */
	EClass getVersion();

	/**
	 * Returns the meta object for the containment reference '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.Version#getPrimarySpec <em>Primary Spec</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference '<em>Primary Spec</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.Version#getPrimarySpec()
	 * @see #getVersion()
	 * @generated
	 */
	EReference getVersion_PrimarySpec();

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.Version#getTagSpecs <em>Tag Specs</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference list '<em>Tag Specs</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.Version#getTagSpecs()
	 * @see #getVersion()
	 * @generated
	 */
	EReference getVersion_TagSpecs();

	/**
	 * Returns the meta object for the reference '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.Version#getNextVersion <em>Next Version</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the reference '<em>Next Version</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.Version#getNextVersion()
	 * @see #getVersion()
	 * @generated
	 */
	EReference getVersion_NextVersion();

	/**
	 * Returns the meta object for the reference '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.Version#getPreviousVersion
	 * <em>Previous Version</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @return the meta object for the reference '<em>Previous Version</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.Version#getPreviousVersion()
	 * @see #getVersion()
	 * @generated
	 */
	EReference getVersion_PreviousVersion();

	/**
	 * Returns the meta object for the containment reference '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.Version#getLogMessage <em>Log Message</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference '<em>Log Message</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.Version#getLogMessage()
	 * @see #getVersion()
	 * @generated
	 */
	EReference getVersion_LogMessage();

	/**
	 * Returns the meta object for the reference '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.Version#getAncestorVersion
	 * <em>Ancestor Version</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @return the meta object for the reference '<em>Ancestor Version</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.Version#getAncestorVersion()
	 * @see #getVersion()
	 * @generated
	 */
	EReference getVersion_AncestorVersion();

	/**
	 * Returns the meta object for the reference list '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.Version#getBranchedVersions
	 * <em>Branched Versions</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @return the meta object for the reference list ' <em>Branched Versions</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.Version#getBranchedVersions()
	 * @see #getVersion()
	 * @generated
	 */
	EReference getVersion_BranchedVersions();

	/**
	 * Returns the meta object for the reference list '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.Version#getMergedToVersion
	 * <em>Merged To Version</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @return the meta object for the reference list ' <em>Merged To Version</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.Version#getMergedToVersion()
	 * @see #getVersion()
	 * @generated
	 */
	EReference getVersion_MergedToVersion();

	/**
	 * Returns the meta object for the reference list '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.Version#getMergedFromVersion
	 * <em>Merged From Version</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @return the meta object for the reference list ' <em>Merged From Version</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.Version#getMergedFromVersion()
	 * @see #getVersion()
	 * @generated
	 */
	EReference getVersion_MergedFromVersion();

	/**
	 * Returns the meta object for class '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.HeadVersionSpec
	 * <em>Head Version Spec</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @return the meta object for class '<em>Head Version Spec</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.HeadVersionSpec
	 * @generated
	 */
	EClass getHeadVersionSpec();

	/**
	 * Returns the meta object for class '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.VersionProperty
	 * <em>Version Property</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @return the meta object for class '<em>Version Property</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.VersionProperty
	 * @generated
	 */
	EClass getVersionProperty();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.VersionProperty#getName <em>Name</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.VersionProperty#getName()
	 * @see #getVersionProperty()
	 * @generated
	 */
	EAttribute getVersionProperty_Name();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.VersionProperty#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.VersionProperty#getValue()
	 * @see #getVersionProperty()
	 * @generated
	 */
	EAttribute getVersionProperty_Value();

	/**
	 * Returns the meta object for class '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.BranchVersionSpec
	 * <em>Branch Version Spec</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @return the meta object for class '<em>Branch Version Spec</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.BranchVersionSpec
	 * @generated
	 */
	EClass getBranchVersionSpec();

	/**
	 * Returns the meta object for class '{@link org.eclipse.emf.emfstore.internal.server.model.versioning.BranchInfo
	 * <em>Branch Info</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Branch Info</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.BranchInfo
	 * @generated
	 */
	EClass getBranchInfo();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.BranchInfo#getName <em>Name</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.BranchInfo#getName()
	 * @see #getBranchInfo()
	 * @generated
	 */
	EAttribute getBranchInfo_Name();

	/**
	 * Returns the meta object for the containment reference '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.BranchInfo#getHead <em>Head</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference '<em>Head</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.BranchInfo#getHead()
	 * @see #getBranchInfo()
	 * @generated
	 */
	EReference getBranchInfo_Head();

	/**
	 * Returns the meta object for the containment reference '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.BranchInfo#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference '<em>Source</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.BranchInfo#getSource()
	 * @see #getBranchInfo()
	 * @generated
	 */
	EReference getBranchInfo_Source();

	/**
	 * Returns the meta object for class '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.AncestorVersionSpec
	 * <em>Ancestor Version Spec</em>}'.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @return the meta object for class '<em>Ancestor Version Spec</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AncestorVersionSpec
	 * @generated
	 */
	EClass getAncestorVersionSpec();

	/**
	 * Returns the meta object for the containment reference '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.AncestorVersionSpec#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference '<em>Target</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AncestorVersionSpec#getTarget()
	 * @see #getAncestorVersionSpec()
	 * @generated
	 */
	EReference getAncestorVersionSpec_Target();

	/**
	 * Returns the meta object for the containment reference '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.AncestorVersionSpec#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference '<em>Source</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AncestorVersionSpec#getSource()
	 * @see #getAncestorVersionSpec()
	 * @generated
	 */
	EReference getAncestorVersionSpec_Source();

	/**
	 * Returns the meta object for class '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.PagedUpdateVersionSpec
	 * <em>Paged Update Version Spec</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Paged Update Version Spec</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.PagedUpdateVersionSpec
	 * @generated
	 */
	EClass getPagedUpdateVersionSpec();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.PagedUpdateVersionSpec#getMaxChanges
	 * <em>Max Changes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Max Changes</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.PagedUpdateVersionSpec#getMaxChanges()
	 * @see #getPagedUpdateVersionSpec()
	 * @generated
	 */
	EAttribute getPagedUpdateVersionSpec_MaxChanges();

	/**
	 * Returns the meta object for the containment reference '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.PagedUpdateVersionSpec#getBaseVersionSpec
	 * <em>Base Version Spec</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference '<em>Base Version Spec</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.PagedUpdateVersionSpec#getBaseVersionSpec()
	 * @see #getPagedUpdateVersionSpec()
	 * @generated
	 */
	EReference getPagedUpdateVersionSpec_BaseVersionSpec();

	/**
	 * Returns the meta object for class '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage
	 * <em>Abstract Change Package</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Abstract Change Package</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage
	 * @generated
	 */
	EClass getAbstractChangePackage();

	/**
	 * Returns the meta object for the containment reference '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#getLogMessage
	 * <em>Log Message</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference '<em>Log Message</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage#getLogMessage()
	 * @see #getAbstractChangePackage()
	 * @generated
	 */
	EReference getAbstractChangePackage_LogMessage();

	/**
	 * Returns the meta object for class '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage
	 * <em>File Based Change Package</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>File Based Change Package</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage
	 * @generated
	 */
	EClass getFileBasedChangePackage();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage#getFilePath
	 * <em>File Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>File Path</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage#getFilePath()
	 * @see #getFileBasedChangePackage()
	 * @generated
	 */
	EAttribute getFileBasedChangePackage_FilePath();

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage#getOperationProxies
	 * <em>Operation Proxies</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference list '<em>Operation Proxies</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage#getOperationProxies()
	 * @see #getFileBasedChangePackage()
	 * @generated
	 */
	EReference getFileBasedChangePackage_OperationProxies();

	/**
	 * Returns the meta object for class '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.OperationProxy <em>Operation Proxy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Operation Proxy</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.OperationProxy
	 * @generated
	 */
	EClass getOperationProxy();

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.OperationProxy#getProxies <em>Proxies</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for the containment reference list '<em>Proxies</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.OperationProxy#getProxies()
	 * @see #getOperationProxy()
	 * @generated
	 */
	EReference getOperationProxy_Proxies();

	/**
	 * Returns the meta object for class '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageEnvelope
	 * <em>Change Package Envelope</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Change Package Envelope</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageEnvelope
	 * @generated
	 */
	EClass getChangePackageEnvelope();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageEnvelope#getFragmentIndex
	 * <em>Fragment Index</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Fragment Index</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageEnvelope#getFragmentIndex()
	 * @see #getChangePackageEnvelope()
	 * @generated
	 */
	EAttribute getChangePackageEnvelope_FragmentIndex();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageEnvelope#getFragmentCount
	 * <em>Fragment Count</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Fragment Count</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageEnvelope#getFragmentCount()
	 * @see #getChangePackageEnvelope()
	 * @generated
	 */
	EAttribute getChangePackageEnvelope_FragmentCount();

	/**
	 * Returns the meta object for the attribute list '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageEnvelope#getFragment
	 * <em>Fragment</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute list '<em>Fragment</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageEnvelope#getFragment()
	 * @see #getChangePackageEnvelope()
	 * @generated
	 */
	EAttribute getChangePackageEnvelope_Fragment();

	/**
	 * Returns the meta object for class '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageProxy <em>Change Package Proxy</em>
	 * }'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for class '<em>Change Package Proxy</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageProxy
	 * @generated
	 */
	EClass getChangePackageProxy();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageProxy#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageProxy#getId()
	 * @see #getChangePackageProxy()
	 * @generated
	 */
	EAttribute getChangePackageProxy_Id();

	/**
	 * Returns the factory that creates the instances of the model. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	VersioningFactory getVersioningFactory();

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
		 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.TagVersionSpecImpl
		 * <em>Tag Version Spec</em>}' class.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.TagVersionSpecImpl
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getTagVersionSpec()
		 * @generated
		 */
		EClass TAG_VERSION_SPEC = eINSTANCE.getTagVersionSpec();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute TAG_VERSION_SPEC__NAME = eINSTANCE.getTagVersionSpec_Name();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.DateVersionSpecImpl
		 * <em>Date Version Spec</em>}' class.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.DateVersionSpecImpl
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getDateVersionSpec()
		 * @generated
		 */
		EClass DATE_VERSION_SPEC = eINSTANCE.getDateVersionSpec();

		/**
		 * The meta object literal for the '<em><b>Date</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute DATE_VERSION_SPEC__DATE = eINSTANCE.getDateVersionSpec_Date();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.PrimaryVersionSpecImpl
		 * <em>Primary Version Spec</em>}' class.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.PrimaryVersionSpecImpl
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getPrimaryVersionSpec()
		 * @generated
		 */
		EClass PRIMARY_VERSION_SPEC = eINSTANCE.getPrimaryVersionSpec();

		/**
		 * The meta object literal for the '<em><b>Identifier</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute PRIMARY_VERSION_SPEC__IDENTIFIER = eINSTANCE.getPrimaryVersionSpec_Identifier();

		/**
		 * The meta object literal for the '<em><b>Project State Checksum</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute PRIMARY_VERSION_SPEC__PROJECT_STATE_CHECKSUM = eINSTANCE
			.getPrimaryVersionSpec_ProjectStateChecksum();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersionSpecImpl <em>Version Spec</em>}'
		 * class.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersionSpecImpl
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getVersionSpec()
		 * @generated
		 */
		EClass VERSION_SPEC = eINSTANCE.getVersionSpec();

		/**
		 * The meta object literal for the '<em><b>Branch</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute VERSION_SPEC__BRANCH = eINSTANCE.getVersionSpec_Branch();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.LogMessageImpl <em>Log Message</em>}'
		 * class.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.LogMessageImpl
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getLogMessage()
		 * @generated
		 */
		EClass LOG_MESSAGE = eINSTANCE.getLogMessage();

		/**
		 * The meta object literal for the '<em><b>Message</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute LOG_MESSAGE__MESSAGE = eINSTANCE.getLogMessage_Message();

		/**
		 * The meta object literal for the '<em><b>Date</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute LOG_MESSAGE__DATE = eINSTANCE.getLogMessage_Date();

		/**
		 * The meta object literal for the '<em><b>Client Date</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute LOG_MESSAGE__CLIENT_DATE = eINSTANCE.getLogMessage_ClientDate();

		/**
		 * The meta object literal for the '<em><b>Author</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute LOG_MESSAGE__AUTHOR = eINSTANCE.getLogMessage_Author();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.ChangePackageImpl
		 * <em>Change Package</em>}' class.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.ChangePackageImpl
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getChangePackage()
		 * @generated
		 */
		EClass CHANGE_PACKAGE = eINSTANCE.getChangePackage();

		/**
		 * The meta object literal for the '<em><b>Operations</b></em>' containment reference list feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference CHANGE_PACKAGE__OPERATIONS = eINSTANCE.getChangePackage_Operations();

		/**
		 * The meta object literal for the '<em><b>Events</b></em>' containment reference list feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference CHANGE_PACKAGE__EVENTS = eINSTANCE.getChangePackage_Events();

		/**
		 * The meta object literal for the '<em><b>Version Properties</b></em>' containment reference list feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference CHANGE_PACKAGE__VERSION_PROPERTIES = eINSTANCE.getChangePackage_VersionProperties();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.HistoryInfoImpl <em>History Info</em>}'
		 * class.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.HistoryInfoImpl
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getHistoryInfo()
		 * @generated
		 */
		EClass HISTORY_INFO = eINSTANCE.getHistoryInfo();

		/**
		 * The meta object literal for the '<em><b>Primary Spec</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference HISTORY_INFO__PRIMARY_SPEC = eINSTANCE.getHistoryInfo_PrimarySpec();

		/**
		 * The meta object literal for the '<em><b>Next Spec</b></em>' containment reference list feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference HISTORY_INFO__NEXT_SPEC = eINSTANCE.getHistoryInfo_NextSpec();

		/**
		 * The meta object literal for the '<em><b>Previous Spec</b></em>' containment reference feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference HISTORY_INFO__PREVIOUS_SPEC = eINSTANCE.getHistoryInfo_PreviousSpec();

		/**
		 * The meta object literal for the '<em><b>Merged From</b></em>' containment reference list feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference HISTORY_INFO__MERGED_FROM = eINSTANCE.getHistoryInfo_MergedFrom();

		/**
		 * The meta object literal for the '<em><b>Merged To</b></em>' containment reference list feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference HISTORY_INFO__MERGED_TO = eINSTANCE.getHistoryInfo_MergedTo();

		/**
		 * The meta object literal for the '<em><b>Log Message</b></em>' containment reference feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference HISTORY_INFO__LOG_MESSAGE = eINSTANCE.getHistoryInfo_LogMessage();

		/**
		 * The meta object literal for the '<em><b>Tag Specs</b></em>' containment reference list feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference HISTORY_INFO__TAG_SPECS = eINSTANCE.getHistoryInfo_TagSpecs();

		/**
		 * The meta object literal for the '<em><b>Version Properties</b></em>' containment reference list feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference HISTORY_INFO__VERSION_PROPERTIES = eINSTANCE.getHistoryInfo_VersionProperties();

		/**
		 * The meta object literal for the '<em><b>Change Package</b></em>' containment reference feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference HISTORY_INFO__CHANGE_PACKAGE = eINSTANCE.getHistoryInfo_ChangePackage();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.HistoryQueryImpl <em>History Query</em>
		 * }' class.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.HistoryQueryImpl
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getHistoryQuery()
		 * @generated
		 */
		EClass HISTORY_QUERY = eINSTANCE.getHistoryQuery();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' containment reference feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference HISTORY_QUERY__SOURCE = eINSTANCE.getHistoryQuery_Source();

		/**
		 * The meta object literal for the ' <em><b>Include Change Packages</b></em>' attribute feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute HISTORY_QUERY__INCLUDE_CHANGE_PACKAGES = eINSTANCE.getHistoryQuery_IncludeChangePackages();

		/**
		 * The meta object literal for the '<em><b>Include All Versions</b></em> ' attribute feature. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute HISTORY_QUERY__INCLUDE_ALL_VERSIONS = eINSTANCE.getHistoryQuery_IncludeAllVersions();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.RangeQueryImpl <em>Range Query</em>}'
		 * class.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.RangeQueryImpl
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getRangeQuery()
		 * @generated
		 */
		EClass RANGE_QUERY = eINSTANCE.getRangeQuery();

		/**
		 * The meta object literal for the '<em><b>Upper Limit</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute RANGE_QUERY__UPPER_LIMIT = eINSTANCE.getRangeQuery_UpperLimit();

		/**
		 * The meta object literal for the '<em><b>Lower Limit</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute RANGE_QUERY__LOWER_LIMIT = eINSTANCE.getRangeQuery_LowerLimit();

		/**
		 * The meta object literal for the '<em><b>Include Incoming</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute RANGE_QUERY__INCLUDE_INCOMING = eINSTANCE.getRangeQuery_IncludeIncoming();

		/**
		 * The meta object literal for the '<em><b>Include Outgoing</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute RANGE_QUERY__INCLUDE_OUTGOING = eINSTANCE.getRangeQuery_IncludeOutgoing();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.PathQueryImpl <em>Path Query</em>}'
		 * class.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.PathQueryImpl
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getPathQuery()
		 * @generated
		 */
		EClass PATH_QUERY = eINSTANCE.getPathQuery();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' containment reference feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference PATH_QUERY__TARGET = eINSTANCE.getPathQuery_Target();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.ModelElementQueryImpl
		 * <em>Model Element Query</em>}' class.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.ModelElementQueryImpl
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getModelElementQuery()
		 * @generated
		 */
		EClass MODEL_ELEMENT_QUERY = eINSTANCE.getModelElementQuery();

		/**
		 * The meta object literal for the '<em><b>Model Elements</b></em>' containment reference list feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference MODEL_ELEMENT_QUERY__MODEL_ELEMENTS = eINSTANCE.getModelElementQuery_ModelElements();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersionImpl
		 * <em>Version</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersionImpl
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getVersion()
		 * @generated
		 */
		EClass VERSION = eINSTANCE.getVersion();

		/**
		 * The meta object literal for the '<em><b>Primary Spec</b></em>' containment reference feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference VERSION__PRIMARY_SPEC = eINSTANCE.getVersion_PrimarySpec();

		/**
		 * The meta object literal for the '<em><b>Tag Specs</b></em>' containment reference list feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference VERSION__TAG_SPECS = eINSTANCE.getVersion_TagSpecs();

		/**
		 * The meta object literal for the '<em><b>Next Version</b></em>' reference feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference VERSION__NEXT_VERSION = eINSTANCE.getVersion_NextVersion();

		/**
		 * The meta object literal for the '<em><b>Previous Version</b></em>' reference feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference VERSION__PREVIOUS_VERSION = eINSTANCE.getVersion_PreviousVersion();

		/**
		 * The meta object literal for the '<em><b>Log Message</b></em>' containment reference feature.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @generated
		 */
		EReference VERSION__LOG_MESSAGE = eINSTANCE.getVersion_LogMessage();

		/**
		 * The meta object literal for the '<em><b>Ancestor Version</b></em>' reference feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference VERSION__ANCESTOR_VERSION = eINSTANCE.getVersion_AncestorVersion();

		/**
		 * The meta object literal for the '<em><b>Branched Versions</b></em>' reference list feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference VERSION__BRANCHED_VERSIONS = eINSTANCE.getVersion_BranchedVersions();

		/**
		 * The meta object literal for the '<em><b>Merged To Version</b></em>' reference list feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference VERSION__MERGED_TO_VERSION = eINSTANCE.getVersion_MergedToVersion();

		/**
		 * The meta object literal for the '<em><b>Merged From Version</b></em>' reference list feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference VERSION__MERGED_FROM_VERSION = eINSTANCE.getVersion_MergedFromVersion();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.HeadVersionSpecImpl
		 * <em>Head Version Spec</em>}' class.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.HeadVersionSpecImpl
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getHeadVersionSpec()
		 * @generated
		 */
		EClass HEAD_VERSION_SPEC = eINSTANCE.getHeadVersionSpec();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersionPropertyImpl
		 * <em>Version Property</em>}' class.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersionPropertyImpl
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getVersionProperty()
		 * @generated
		 */
		EClass VERSION_PROPERTY = eINSTANCE.getVersionProperty();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute VERSION_PROPERTY__NAME = eINSTANCE.getVersionProperty_Name();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute VERSION_PROPERTY__VALUE = eINSTANCE.getVersionProperty_Value();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.BranchVersionSpecImpl
		 * <em>Branch Version Spec</em>}' class.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.BranchVersionSpecImpl
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getBranchVersionSpec()
		 * @generated
		 */
		EClass BRANCH_VERSION_SPEC = eINSTANCE.getBranchVersionSpec();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.BranchInfoImpl <em>Branch Info</em>}'
		 * class.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.BranchInfoImpl
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getBranchInfo()
		 * @generated
		 */
		EClass BRANCH_INFO = eINSTANCE.getBranchInfo();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute BRANCH_INFO__NAME = eINSTANCE.getBranchInfo_Name();

		/**
		 * The meta object literal for the '<em><b>Head</b></em>' containment reference feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference BRANCH_INFO__HEAD = eINSTANCE.getBranchInfo_Head();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' containment reference feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference BRANCH_INFO__SOURCE = eINSTANCE.getBranchInfo_Source();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.AncestorVersionSpecImpl
		 * <em>Ancestor Version Spec</em>}' class.
		 * <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.AncestorVersionSpecImpl
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getAncestorVersionSpec()
		 * @generated
		 */
		EClass ANCESTOR_VERSION_SPEC = eINSTANCE.getAncestorVersionSpec();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' containment reference feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference ANCESTOR_VERSION_SPEC__TARGET = eINSTANCE.getAncestorVersionSpec_Target();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' containment reference feature.
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference ANCESTOR_VERSION_SPEC__SOURCE = eINSTANCE.getAncestorVersionSpec_Source();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.PagedUpdateVersionSpecImpl
		 * <em>Paged Update Version Spec</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.PagedUpdateVersionSpecImpl
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getPagedUpdateVersionSpec()
		 * @generated
		 */
		EClass PAGED_UPDATE_VERSION_SPEC = eINSTANCE.getPagedUpdateVersionSpec();

		/**
		 * The meta object literal for the '<em><b>Max Changes</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute PAGED_UPDATE_VERSION_SPEC__MAX_CHANGES = eINSTANCE.getPagedUpdateVersionSpec_MaxChanges();

		/**
		 * The meta object literal for the '<em><b>Base Version Spec</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference PAGED_UPDATE_VERSION_SPEC__BASE_VERSION_SPEC = eINSTANCE.getPagedUpdateVersionSpec_BaseVersionSpec();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage
		 * <em>Abstract Change Package</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getAbstractChangePackage()
		 * @generated
		 */
		EClass ABSTRACT_CHANGE_PACKAGE = eINSTANCE.getAbstractChangePackage();

		/**
		 * The meta object literal for the '<em><b>Log Message</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference ABSTRACT_CHANGE_PACKAGE__LOG_MESSAGE = eINSTANCE.getAbstractChangePackage_LogMessage();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.FileBasedChangePackageImpl
		 * <em>File Based Change Package</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.FileBasedChangePackageImpl
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getFileBasedChangePackage()
		 * @generated
		 */
		EClass FILE_BASED_CHANGE_PACKAGE = eINSTANCE.getFileBasedChangePackage();

		/**
		 * The meta object literal for the '<em><b>File Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute FILE_BASED_CHANGE_PACKAGE__FILE_PATH = eINSTANCE.getFileBasedChangePackage_FilePath();

		/**
		 * The meta object literal for the '<em><b>Operation Proxies</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference FILE_BASED_CHANGE_PACKAGE__OPERATION_PROXIES = eINSTANCE
			.getFileBasedChangePackage_OperationProxies();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.OperationProxyImpl
		 * <em>Operation Proxy</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.OperationProxyImpl
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getOperationProxy()
		 * @generated
		 */
		EClass OPERATION_PROXY = eINSTANCE.getOperationProxy();

		/**
		 * The meta object literal for the '<em><b>Proxies</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EReference OPERATION_PROXY__PROXIES = eINSTANCE.getOperationProxy_Proxies();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.ChangePackageEnvelopeImpl
		 * <em>Change Package Envelope</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.ChangePackageEnvelopeImpl
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getChangePackageEnvelope()
		 * @generated
		 */
		EClass CHANGE_PACKAGE_ENVELOPE = eINSTANCE.getChangePackageEnvelope();

		/**
		 * The meta object literal for the '<em><b>Fragment Index</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute CHANGE_PACKAGE_ENVELOPE__FRAGMENT_INDEX = eINSTANCE.getChangePackageEnvelope_FragmentIndex();

		/**
		 * The meta object literal for the '<em><b>Fragment Count</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute CHANGE_PACKAGE_ENVELOPE__FRAGMENT_COUNT = eINSTANCE.getChangePackageEnvelope_FragmentCount();

		/**
		 * The meta object literal for the '<em><b>Fragment</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute CHANGE_PACKAGE_ENVELOPE__FRAGMENT = eINSTANCE.getChangePackageEnvelope_Fragment();

		/**
		 * The meta object literal for the '
		 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.ChangePackageProxyImpl
		 * <em>Change Package Proxy</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 *
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.ChangePackageProxyImpl
		 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersioningPackageImpl#getChangePackageProxy()
		 * @generated
		 */
		EClass CHANGE_PACKAGE_PROXY = eINSTANCE.getChangePackageProxy();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 *
		 * @generated
		 */
		EAttribute CHANGE_PACKAGE_PROXY__ID = eINSTANCE.getChangePackageProxy_Id();

	}

} // VersioningPackage