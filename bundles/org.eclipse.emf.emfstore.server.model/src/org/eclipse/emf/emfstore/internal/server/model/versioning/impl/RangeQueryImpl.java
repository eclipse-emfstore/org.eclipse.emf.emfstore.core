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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.query.ESRangeQueryImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.RangeQuery;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage;
import org.eclipse.emf.emfstore.server.model.query.ESRangeQuery;

/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Range Query</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.RangeQueryImpl#getUpperLimit <em>Upper
 * Limit</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.RangeQueryImpl#getLowerLimit <em>Lower
 * Limit</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.RangeQueryImpl#isIncludeIncoming <em>
 * Include Incoming</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.RangeQueryImpl#isIncludeOutgoing <em>
 * Include Outgoing</em>}</li>
 * </ul>
 * </p>
 *
 * @generated NOT
 *
 * @param <T> a API range query subtype
 */
public class RangeQueryImpl<T extends ESRangeQuery<?>> extends HistoryQueryImpl<T> implements RangeQuery<T> {

	/**
	 * @generated NOT
	 */
	private T apiImpl;

	/**
	 * The default value of the '{@link #getUpperLimit() <em>Upper Limit</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getUpperLimit()
	 * @generated
	 * @ordered
	 */
	protected static final int UPPER_LIMIT_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getUpperLimit() <em>Upper Limit</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getUpperLimit()
	 * @generated
	 * @ordered
	 */
	protected int upperLimit = UPPER_LIMIT_EDEFAULT;

	/**
	 * The default value of the '{@link #getLowerLimit() <em>Lower Limit</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getLowerLimit()
	 * @generated
	 * @ordered
	 */
	protected static final int LOWER_LIMIT_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getLowerLimit() <em>Lower Limit</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getLowerLimit()
	 * @generated
	 * @ordered
	 */
	protected int lowerLimit = LOWER_LIMIT_EDEFAULT;

	/**
	 * The default value of the '{@link #isIncludeIncoming() <em>Include Incoming</em>}' attribute.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @see #isIncludeIncoming()
	 * @generated
	 * @ordered
	 */
	protected static final boolean INCLUDE_INCOMING_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isIncludeIncoming() <em>Include Incoming</em>}' attribute.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @see #isIncludeIncoming()
	 * @generated
	 * @ordered
	 */
	protected boolean includeIncoming = INCLUDE_INCOMING_EDEFAULT;

	/**
	 * The default value of the '{@link #isIncludeOutgoing() <em>Include Outgoing</em>}' attribute.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @see #isIncludeOutgoing()
	 * @generated
	 * @ordered
	 */
	protected static final boolean INCLUDE_OUTGOING_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isIncludeOutgoing() <em>Include Outgoing</em>}' attribute.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @see #isIncludeOutgoing()
	 * @generated
	 * @ordered
	 */
	protected boolean includeOutgoing = INCLUDE_OUTGOING_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected RangeQueryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return VersioningPackage.Literals.RANGE_QUERY;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public int getUpperLimit() {
		return upperLimit;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setUpperLimit(int newUpperLimit) {
		final int oldUpperLimit = upperLimit;
		upperLimit = newUpperLimit;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, VersioningPackage.RANGE_QUERY__UPPER_LIMIT,
				oldUpperLimit, upperLimit));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public int getLowerLimit() {
		return lowerLimit;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setLowerLimit(int newLowerLimit) {
		final int oldLowerLimit = lowerLimit;
		lowerLimit = newLowerLimit;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, VersioningPackage.RANGE_QUERY__LOWER_LIMIT,
				oldLowerLimit, lowerLimit));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public boolean isIncludeIncoming() {
		return includeIncoming;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setIncludeIncoming(boolean newIncludeIncoming) {
		final boolean oldIncludeIncoming = includeIncoming;
		includeIncoming = newIncludeIncoming;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, VersioningPackage.RANGE_QUERY__INCLUDE_INCOMING,
				oldIncludeIncoming, includeIncoming));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public boolean isIncludeOutgoing() {
		return includeOutgoing;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setIncludeOutgoing(boolean newIncludeOutgoing) {
		final boolean oldIncludeOutgoing = includeOutgoing;
		includeOutgoing = newIncludeOutgoing;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, VersioningPackage.RANGE_QUERY__INCLUDE_OUTGOING,
				oldIncludeOutgoing, includeOutgoing));
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
		case VersioningPackage.RANGE_QUERY__UPPER_LIMIT:
			return getUpperLimit();
		case VersioningPackage.RANGE_QUERY__LOWER_LIMIT:
			return getLowerLimit();
		case VersioningPackage.RANGE_QUERY__INCLUDE_INCOMING:
			return isIncludeIncoming();
		case VersioningPackage.RANGE_QUERY__INCLUDE_OUTGOING:
			return isIncludeOutgoing();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case VersioningPackage.RANGE_QUERY__UPPER_LIMIT:
			setUpperLimit((Integer) newValue);
			return;
		case VersioningPackage.RANGE_QUERY__LOWER_LIMIT:
			setLowerLimit((Integer) newValue);
			return;
		case VersioningPackage.RANGE_QUERY__INCLUDE_INCOMING:
			setIncludeIncoming((Boolean) newValue);
			return;
		case VersioningPackage.RANGE_QUERY__INCLUDE_OUTGOING:
			setIncludeOutgoing((Boolean) newValue);
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
		case VersioningPackage.RANGE_QUERY__UPPER_LIMIT:
			setUpperLimit(UPPER_LIMIT_EDEFAULT);
			return;
		case VersioningPackage.RANGE_QUERY__LOWER_LIMIT:
			setLowerLimit(LOWER_LIMIT_EDEFAULT);
			return;
		case VersioningPackage.RANGE_QUERY__INCLUDE_INCOMING:
			setIncludeIncoming(INCLUDE_INCOMING_EDEFAULT);
			return;
		case VersioningPackage.RANGE_QUERY__INCLUDE_OUTGOING:
			setIncludeOutgoing(INCLUDE_OUTGOING_EDEFAULT);
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
		case VersioningPackage.RANGE_QUERY__UPPER_LIMIT:
			return upperLimit != UPPER_LIMIT_EDEFAULT;
		case VersioningPackage.RANGE_QUERY__LOWER_LIMIT:
			return lowerLimit != LOWER_LIMIT_EDEFAULT;
		case VersioningPackage.RANGE_QUERY__INCLUDE_INCOMING:
			return includeIncoming != INCLUDE_INCOMING_EDEFAULT;
		case VersioningPackage.RANGE_QUERY__INCLUDE_OUTGOING:
			return includeOutgoing != INCLUDE_OUTGOING_EDEFAULT;
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
		result.append(" (upperLimit: "); //$NON-NLS-1$
		result.append(upperLimit);
		result.append(", lowerLimit: "); //$NON-NLS-1$
		result.append(lowerLimit);
		result.append(", includeIncoming: "); //$NON-NLS-1$
		result.append(includeIncoming);
		result.append(", includeOutgoing: "); //$NON-NLS-1$
		result.append(includeOutgoing);
		result.append(')');
		return result.toString();
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.api.APIDelegate#toAPI()
	 *
	 * @generated NOT
	 */
	public T toAPI() {
		if (apiImpl == null) {
			apiImpl = createAPI();
		}

		return apiImpl;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.api.APIDelegate#createAPI()
	 *
	 * @generated NOT
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public T createAPI() {
		// TODO fix type warnings
		return (T) new ESRangeQueryImpl(this);
	}
} // RangeQueryImpl