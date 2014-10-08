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
package org.eclipse.emf.emfstore.internal.server.model.versioning.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AncestorVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.BranchInfo;
import org.eclipse.emf.emfstore.internal.server.model.versioning.BranchVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.DateVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.HeadVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo;
import org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ModelElementQuery;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PagedUpdateVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PathQuery;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.RangeQuery;
import org.eclipse.emf.emfstore.internal.server.model.versioning.TagVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.Version;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersionProperty;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningFactory;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!--
 * end-user-doc -->
 * 
 * @generated
 */
public class VersioningFactoryImpl extends EFactoryImpl implements VersioningFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	public static VersioningFactory init() {
		try
		{
			final VersioningFactory theVersioningFactory = (VersioningFactory) EPackage.Registry.INSTANCE
				.getEFactory("http://eclipse.org/emf/emfstore/server/model/versioning"); //$NON-NLS-1$
			if (theVersioningFactory != null)
			{
				return theVersioningFactory;
			}
		} catch (final Exception exception)
		{
			EcorePlugin.INSTANCE.log(exception);
		}
		return new VersioningFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	public VersioningFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID())
		{
		case VersioningPackage.TAG_VERSION_SPEC:
			return createTagVersionSpec();
		case VersioningPackage.DATE_VERSION_SPEC:
			return createDateVersionSpec();
		case VersioningPackage.PRIMARY_VERSION_SPEC:
			return createPrimaryVersionSpec();
		case VersioningPackage.LOG_MESSAGE:
			return createLogMessage();
		case VersioningPackage.CHANGE_PACKAGE:
			return createChangePackage();
		case VersioningPackage.HISTORY_INFO:
			return createHistoryInfo();
		case VersioningPackage.RANGE_QUERY:
			return createRangeQuery();
		case VersioningPackage.PATH_QUERY:
			return createPathQuery();
		case VersioningPackage.MODEL_ELEMENT_QUERY:
			return createModelElementQuery();
		case VersioningPackage.VERSION:
			return createVersion();
		case VersioningPackage.HEAD_VERSION_SPEC:
			return createHeadVersionSpec();
		case VersioningPackage.VERSION_PROPERTY:
			return createVersionProperty();
		case VersioningPackage.BRANCH_VERSION_SPEC:
			return createBranchVersionSpec();
		case VersioningPackage.BRANCH_INFO:
			return createBranchInfo();
		case VersioningPackage.ANCESTOR_VERSION_SPEC:
			return createAncestorVersionSpec();
		case VersioningPackage.PAGED_UPDATE_VERSION_SPEC:
			return createPagedUpdateVersionSpec();
		default:
			throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public TagVersionSpec createTagVersionSpec() {
		final TagVersionSpecImpl tagVersionSpec = new TagVersionSpecImpl();
		return tagVersionSpec;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public DateVersionSpec createDateVersionSpec() {
		final DateVersionSpecImpl dateVersionSpec = new DateVersionSpecImpl();
		return dateVersionSpec;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public PrimaryVersionSpec createPrimaryVersionSpec() {
		final PrimaryVersionSpecImpl primaryVersionSpec = new PrimaryVersionSpecImpl();
		return primaryVersionSpec;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public LogMessage createLogMessage() {
		final LogMessageImpl logMessage = new LogMessageImpl();
		return logMessage;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ChangePackage createChangePackage() {
		final ChangePackageImpl changePackage = new ChangePackageImpl();
		return changePackage;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public HistoryInfo createHistoryInfo() {
		final HistoryInfoImpl historyInfo = new HistoryInfoImpl();
		return historyInfo;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("rawtypes")
	public RangeQuery createRangeQuery() {
		final RangeQueryImpl rangeQuery = new RangeQueryImpl();
		return rangeQuery;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public PathQuery createPathQuery() {
		final PathQueryImpl pathQuery = new PathQueryImpl();
		return pathQuery;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ModelElementQuery createModelElementQuery() {
		final ModelElementQueryImpl modelElementQuery = new ModelElementQueryImpl();
		return modelElementQuery;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Version createVersion() {
		final VersionImpl version = new VersionImpl();
		return version;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public HeadVersionSpec createHeadVersionSpec() {
		final HeadVersionSpecImpl headVersionSpec = new HeadVersionSpecImpl();
		return headVersionSpec;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public VersionProperty createVersionProperty() {
		final VersionPropertyImpl versionProperty = new VersionPropertyImpl();
		return versionProperty;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public BranchVersionSpec createBranchVersionSpec() {
		final BranchVersionSpecImpl branchVersionSpec = new BranchVersionSpecImpl();
		return branchVersionSpec;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public BranchInfo createBranchInfo() {
		final BranchInfoImpl branchInfo = new BranchInfoImpl();
		return branchInfo;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public AncestorVersionSpec createAncestorVersionSpec() {
		final AncestorVersionSpecImpl ancestorVersionSpec = new AncestorVersionSpecImpl();
		return ancestorVersionSpec;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public PagedUpdateVersionSpec createPagedUpdateVersionSpec()
	{
		final PagedUpdateVersionSpecImpl pagedUpdateVersionSpec = new PagedUpdateVersionSpecImpl();
		return pagedUpdateVersionSpec;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public VersioningPackage getVersioningPackage() {
		return (VersioningPackage) getEPackage();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static VersioningPackage getPackage() {
		return VersioningPackage.eINSTANCE;
	}

} // VersioningFactoryImpl