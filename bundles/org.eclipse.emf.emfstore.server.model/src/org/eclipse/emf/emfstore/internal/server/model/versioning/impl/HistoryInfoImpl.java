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

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESHistoryInfoImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo;
import org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.TagVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersionProperty;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>History Info</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.HistoryInfoImpl#getPrimarySpec
 * <em>Primary Spec</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.HistoryInfoImpl#getNextSpec
 * <em>Next Spec</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.HistoryInfoImpl#getPreviousSpec
 * <em>Previous Spec</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.HistoryInfoImpl#getMergedFrom
 * <em>Merged From</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.HistoryInfoImpl#getMergedTo
 * <em>Merged To</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.HistoryInfoImpl#getLogMessage
 * <em>Log Message</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.HistoryInfoImpl#getTagSpecs
 * <em>Tag Specs</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.HistoryInfoImpl#getVersionProperties
 * <em>Version Properties</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.HistoryInfoImpl#getChangePackage
 * <em>Change Package</em>}</li>
 * </ul>
 *
 * @generated
 */
public class HistoryInfoImpl extends EObjectImpl implements HistoryInfo {

	/**
	 * The cached value of the '{@link #getPrimarySpec() <em>Primary Spec</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getPrimarySpec()
	 * @generated
	 * @ordered
	 */
	protected PrimaryVersionSpec primarySpec;

	/**
	 * @generated NOT
	 */
	private ESHistoryInfoImpl apiImpl;

	/**
	 * The cached value of the '{@link #getNextSpec() <em>Next Spec</em>}' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getNextSpec()
	 * @generated
	 * @ordered
	 */
	protected EList<PrimaryVersionSpec> nextSpec;

	/**
	 * The cached value of the '{@link #getPreviousSpec() <em>Previous Spec</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getPreviousSpec()
	 * @generated
	 * @ordered
	 */
	protected PrimaryVersionSpec previousSpec;

	/**
	 * The cached value of the '{@link #getMergedFrom() <em>Merged From</em>}' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getMergedFrom()
	 * @generated
	 * @ordered
	 */
	protected EList<PrimaryVersionSpec> mergedFrom;

	/**
	 * The cached value of the '{@link #getMergedTo() <em>Merged To</em>}' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getMergedTo()
	 * @generated
	 * @ordered
	 */
	protected EList<PrimaryVersionSpec> mergedTo;

	/**
	 * The cached value of the '{@link #getLogMessage() <em>Log Message</em>}' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getLogMessage()
	 * @generated
	 * @ordered
	 */
	protected LogMessage logMessage;

	/**
	 * The cached value of the '{@link #getTagSpecs() <em>Tag Specs</em>}' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getTagSpecs()
	 * @generated
	 * @ordered
	 */
	protected EList<TagVersionSpec> tagSpecs;

	/**
	 * The cached value of the '{@link #getVersionProperties()
	 * <em>Version Properties</em>}' containment reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getVersionProperties()
	 * @generated
	 * @ordered
	 */
	protected EList<VersionProperty> versionProperties;

	/**
	 * The cached value of the '{@link #getChangePackage() <em>Change Package</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getChangePackage()
	 * @generated
	 * @ordered
	 */
	protected AbstractChangePackage changePackage;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected HistoryInfoImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return VersioningPackage.Literals.HISTORY_INFO;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public PrimaryVersionSpec getPrimarySpec() {
		if (primarySpec != null && primarySpec.eIsProxy()) {
			final InternalEObject oldPrimarySpec = (InternalEObject) primarySpec;
			primarySpec = (PrimaryVersionSpec) eResolveProxy(oldPrimarySpec);
			if (primarySpec != oldPrimarySpec) {
				final InternalEObject newPrimarySpec = (InternalEObject) primarySpec;
				NotificationChain msgs = oldPrimarySpec.eInverseRemove(this,
					EOPPOSITE_FEATURE_BASE - VersioningPackage.HISTORY_INFO__PRIMARY_SPEC, null, null);
				if (newPrimarySpec.eInternalContainer() == null) {
					msgs = newPrimarySpec.eInverseAdd(this,
						EOPPOSITE_FEATURE_BASE - VersioningPackage.HISTORY_INFO__PRIMARY_SPEC, null, msgs);
				}
				if (msgs != null) {
					msgs.dispatch();
				}
				if (eNotificationRequired()) {
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
						VersioningPackage.HISTORY_INFO__PRIMARY_SPEC, oldPrimarySpec, primarySpec));
				}
			}
		}
		return primarySpec;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public PrimaryVersionSpec basicGetPrimarySpec() {
		return primarySpec;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public NotificationChain basicSetPrimarySpec(PrimaryVersionSpec newPrimarySpec, NotificationChain msgs) {
		final PrimaryVersionSpec oldPrimarySpec = primarySpec;
		primarySpec = newPrimarySpec;
		if (eNotificationRequired()) {
			final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
				VersioningPackage.HISTORY_INFO__PRIMARY_SPEC, oldPrimarySpec, newPrimarySpec);
			if (msgs == null) {
				msgs = notification;
			} else {
				msgs.add(notification);
			}
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setPrimarySpec(PrimaryVersionSpec newPrimarySpec) {
		if (newPrimarySpec != primarySpec) {
			NotificationChain msgs = null;
			if (primarySpec != null) {
				msgs = ((InternalEObject) primarySpec).eInverseRemove(this,
					EOPPOSITE_FEATURE_BASE - VersioningPackage.HISTORY_INFO__PRIMARY_SPEC, null, msgs);
			}
			if (newPrimarySpec != null) {
				msgs = ((InternalEObject) newPrimarySpec).eInverseAdd(this,
					EOPPOSITE_FEATURE_BASE - VersioningPackage.HISTORY_INFO__PRIMARY_SPEC, null, msgs);
			}
			msgs = basicSetPrimarySpec(newPrimarySpec, msgs);
			if (msgs != null) {
				msgs.dispatch();
			}
		} else if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, VersioningPackage.HISTORY_INFO__PRIMARY_SPEC,
				newPrimarySpec, newPrimarySpec));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EList<PrimaryVersionSpec> getNextSpec() {
		if (nextSpec == null) {
			nextSpec = new EObjectContainmentEList.Resolving<PrimaryVersionSpec>(PrimaryVersionSpec.class, this,
				VersioningPackage.HISTORY_INFO__NEXT_SPEC);
		}
		return nextSpec;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public PrimaryVersionSpec getPreviousSpec() {
		if (previousSpec != null && previousSpec.eIsProxy()) {
			final InternalEObject oldPreviousSpec = (InternalEObject) previousSpec;
			previousSpec = (PrimaryVersionSpec) eResolveProxy(oldPreviousSpec);
			if (previousSpec != oldPreviousSpec) {
				final InternalEObject newPreviousSpec = (InternalEObject) previousSpec;
				NotificationChain msgs = oldPreviousSpec.eInverseRemove(this,
					EOPPOSITE_FEATURE_BASE - VersioningPackage.HISTORY_INFO__PREVIOUS_SPEC, null, null);
				if (newPreviousSpec.eInternalContainer() == null) {
					msgs = newPreviousSpec.eInverseAdd(this,
						EOPPOSITE_FEATURE_BASE - VersioningPackage.HISTORY_INFO__PREVIOUS_SPEC, null, msgs);
				}
				if (msgs != null) {
					msgs.dispatch();
				}
				if (eNotificationRequired()) {
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
						VersioningPackage.HISTORY_INFO__PREVIOUS_SPEC, oldPreviousSpec, previousSpec));
				}
			}
		}
		return previousSpec;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public PrimaryVersionSpec basicGetPreviousSpec() {
		return previousSpec;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public NotificationChain basicSetPreviousSpec(PrimaryVersionSpec newPreviousSpec, NotificationChain msgs) {
		final PrimaryVersionSpec oldPreviousSpec = previousSpec;
		previousSpec = newPreviousSpec;
		if (eNotificationRequired()) {
			final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
				VersioningPackage.HISTORY_INFO__PREVIOUS_SPEC, oldPreviousSpec, newPreviousSpec);
			if (msgs == null) {
				msgs = notification;
			} else {
				msgs.add(notification);
			}
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setPreviousSpec(PrimaryVersionSpec newPreviousSpec) {
		if (newPreviousSpec != previousSpec) {
			NotificationChain msgs = null;
			if (previousSpec != null) {
				msgs = ((InternalEObject) previousSpec).eInverseRemove(this,
					EOPPOSITE_FEATURE_BASE - VersioningPackage.HISTORY_INFO__PREVIOUS_SPEC, null, msgs);
			}
			if (newPreviousSpec != null) {
				msgs = ((InternalEObject) newPreviousSpec).eInverseAdd(this,
					EOPPOSITE_FEATURE_BASE - VersioningPackage.HISTORY_INFO__PREVIOUS_SPEC, null, msgs);
			}
			msgs = basicSetPreviousSpec(newPreviousSpec, msgs);
			if (msgs != null) {
				msgs.dispatch();
			}
		} else if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, VersioningPackage.HISTORY_INFO__PREVIOUS_SPEC,
				newPreviousSpec, newPreviousSpec));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EList<PrimaryVersionSpec> getMergedFrom() {
		if (mergedFrom == null) {
			mergedFrom = new EObjectContainmentEList.Resolving<PrimaryVersionSpec>(PrimaryVersionSpec.class, this,
				VersioningPackage.HISTORY_INFO__MERGED_FROM);
		}
		return mergedFrom;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EList<PrimaryVersionSpec> getMergedTo() {
		if (mergedTo == null) {
			mergedTo = new EObjectContainmentEList.Resolving<PrimaryVersionSpec>(PrimaryVersionSpec.class, this,
				VersioningPackage.HISTORY_INFO__MERGED_TO);
		}
		return mergedTo;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public LogMessage getLogMessage() {
		if (logMessage != null && logMessage.eIsProxy()) {
			final InternalEObject oldLogMessage = (InternalEObject) logMessage;
			logMessage = (LogMessage) eResolveProxy(oldLogMessage);
			if (logMessage != oldLogMessage) {
				final InternalEObject newLogMessage = (InternalEObject) logMessage;
				NotificationChain msgs = oldLogMessage.eInverseRemove(this,
					EOPPOSITE_FEATURE_BASE - VersioningPackage.HISTORY_INFO__LOG_MESSAGE, null, null);
				if (newLogMessage.eInternalContainer() == null) {
					msgs = newLogMessage.eInverseAdd(this,
						EOPPOSITE_FEATURE_BASE - VersioningPackage.HISTORY_INFO__LOG_MESSAGE, null, msgs);
				}
				if (msgs != null) {
					msgs.dispatch();
				}
				if (eNotificationRequired()) {
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
						VersioningPackage.HISTORY_INFO__LOG_MESSAGE, oldLogMessage, logMessage));
				}
			}
		}
		return logMessage;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public LogMessage basicGetLogMessage() {
		return logMessage;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public NotificationChain basicSetLogMessage(LogMessage newLogMessage, NotificationChain msgs) {
		final LogMessage oldLogMessage = logMessage;
		logMessage = newLogMessage;
		if (eNotificationRequired()) {
			final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
				VersioningPackage.HISTORY_INFO__LOG_MESSAGE, oldLogMessage, newLogMessage);
			if (msgs == null) {
				msgs = notification;
			} else {
				msgs.add(notification);
			}
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setLogMessage(LogMessage newLogMessage) {
		if (newLogMessage != logMessage) {
			NotificationChain msgs = null;
			if (logMessage != null) {
				msgs = ((InternalEObject) logMessage).eInverseRemove(this,
					EOPPOSITE_FEATURE_BASE - VersioningPackage.HISTORY_INFO__LOG_MESSAGE, null, msgs);
			}
			if (newLogMessage != null) {
				msgs = ((InternalEObject) newLogMessage).eInverseAdd(this,
					EOPPOSITE_FEATURE_BASE - VersioningPackage.HISTORY_INFO__LOG_MESSAGE, null, msgs);
			}
			msgs = basicSetLogMessage(newLogMessage, msgs);
			if (msgs != null) {
				msgs.dispatch();
			}
		} else if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, VersioningPackage.HISTORY_INFO__LOG_MESSAGE,
				newLogMessage, newLogMessage));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EList<TagVersionSpec> getTagSpecs() {
		if (tagSpecs == null) {
			tagSpecs = new EObjectContainmentEList.Resolving<TagVersionSpec>(TagVersionSpec.class, this,
				VersioningPackage.HISTORY_INFO__TAG_SPECS);
		}
		return tagSpecs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EList<VersionProperty> getVersionProperties() {
		if (versionProperties == null) {
			versionProperties = new EObjectContainmentEList.Resolving<VersionProperty>(VersionProperty.class, this,
				VersioningPackage.HISTORY_INFO__VERSION_PROPERTIES);
		}
		return versionProperties;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public AbstractChangePackage getChangePackage() {
		if (changePackage != null && changePackage.eIsProxy()) {
			final InternalEObject oldChangePackage = (InternalEObject) changePackage;
			changePackage = (AbstractChangePackage) eResolveProxy(oldChangePackage);
			if (changePackage != oldChangePackage) {
				final InternalEObject newChangePackage = (InternalEObject) changePackage;
				NotificationChain msgs = oldChangePackage.eInverseRemove(this,
					EOPPOSITE_FEATURE_BASE - VersioningPackage.HISTORY_INFO__CHANGE_PACKAGE, null, null);
				if (newChangePackage.eInternalContainer() == null) {
					msgs = newChangePackage.eInverseAdd(this,
						EOPPOSITE_FEATURE_BASE - VersioningPackage.HISTORY_INFO__CHANGE_PACKAGE, null, msgs);
				}
				if (msgs != null) {
					msgs.dispatch();
				}
				if (eNotificationRequired()) {
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
						VersioningPackage.HISTORY_INFO__CHANGE_PACKAGE, oldChangePackage, changePackage));
				}
			}
		}
		return changePackage;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public AbstractChangePackage basicGetChangePackage() {
		return changePackage;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public NotificationChain basicSetChangePackage(AbstractChangePackage newChangePackage, NotificationChain msgs) {
		final AbstractChangePackage oldChangePackage = changePackage;
		changePackage = newChangePackage;
		if (eNotificationRequired()) {
			final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
				VersioningPackage.HISTORY_INFO__CHANGE_PACKAGE, oldChangePackage, newChangePackage);
			if (msgs == null) {
				msgs = notification;
			} else {
				msgs.add(notification);
			}
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setChangePackage(AbstractChangePackage newChangePackage) {
		if (newChangePackage != changePackage) {
			NotificationChain msgs = null;
			if (changePackage != null) {
				msgs = ((InternalEObject) changePackage).eInverseRemove(this,
					EOPPOSITE_FEATURE_BASE - VersioningPackage.HISTORY_INFO__CHANGE_PACKAGE, null, msgs);
			}
			if (newChangePackage != null) {
				msgs = ((InternalEObject) newChangePackage).eInverseAdd(this,
					EOPPOSITE_FEATURE_BASE - VersioningPackage.HISTORY_INFO__CHANGE_PACKAGE, null, msgs);
			}
			msgs = basicSetChangePackage(newChangePackage, msgs);
			if (msgs != null) {
				msgs.dispatch();
			}
		} else if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, VersioningPackage.HISTORY_INFO__CHANGE_PACKAGE,
				newChangePackage, newChangePackage));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case VersioningPackage.HISTORY_INFO__PRIMARY_SPEC:
			return basicSetPrimarySpec(null, msgs);
		case VersioningPackage.HISTORY_INFO__NEXT_SPEC:
			return ((InternalEList<?>) getNextSpec()).basicRemove(otherEnd, msgs);
		case VersioningPackage.HISTORY_INFO__PREVIOUS_SPEC:
			return basicSetPreviousSpec(null, msgs);
		case VersioningPackage.HISTORY_INFO__MERGED_FROM:
			return ((InternalEList<?>) getMergedFrom()).basicRemove(otherEnd, msgs);
		case VersioningPackage.HISTORY_INFO__MERGED_TO:
			return ((InternalEList<?>) getMergedTo()).basicRemove(otherEnd, msgs);
		case VersioningPackage.HISTORY_INFO__LOG_MESSAGE:
			return basicSetLogMessage(null, msgs);
		case VersioningPackage.HISTORY_INFO__TAG_SPECS:
			return ((InternalEList<?>) getTagSpecs()).basicRemove(otherEnd, msgs);
		case VersioningPackage.HISTORY_INFO__VERSION_PROPERTIES:
			return ((InternalEList<?>) getVersionProperties()).basicRemove(otherEnd, msgs);
		case VersioningPackage.HISTORY_INFO__CHANGE_PACKAGE:
			return basicSetChangePackage(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case VersioningPackage.HISTORY_INFO__PRIMARY_SPEC:
			if (resolve) {
				return getPrimarySpec();
			}
			return basicGetPrimarySpec();
		case VersioningPackage.HISTORY_INFO__NEXT_SPEC:
			return getNextSpec();
		case VersioningPackage.HISTORY_INFO__PREVIOUS_SPEC:
			if (resolve) {
				return getPreviousSpec();
			}
			return basicGetPreviousSpec();
		case VersioningPackage.HISTORY_INFO__MERGED_FROM:
			return getMergedFrom();
		case VersioningPackage.HISTORY_INFO__MERGED_TO:
			return getMergedTo();
		case VersioningPackage.HISTORY_INFO__LOG_MESSAGE:
			if (resolve) {
				return getLogMessage();
			}
			return basicGetLogMessage();
		case VersioningPackage.HISTORY_INFO__TAG_SPECS:
			return getTagSpecs();
		case VersioningPackage.HISTORY_INFO__VERSION_PROPERTIES:
			return getVersionProperties();
		case VersioningPackage.HISTORY_INFO__CHANGE_PACKAGE:
			if (resolve) {
				return getChangePackage();
			}
			return basicGetChangePackage();
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
		case VersioningPackage.HISTORY_INFO__PRIMARY_SPEC:
			setPrimarySpec((PrimaryVersionSpec) newValue);
			return;
		case VersioningPackage.HISTORY_INFO__NEXT_SPEC:
			getNextSpec().clear();
			getNextSpec().addAll((Collection<? extends PrimaryVersionSpec>) newValue);
			return;
		case VersioningPackage.HISTORY_INFO__PREVIOUS_SPEC:
			setPreviousSpec((PrimaryVersionSpec) newValue);
			return;
		case VersioningPackage.HISTORY_INFO__MERGED_FROM:
			getMergedFrom().clear();
			getMergedFrom().addAll((Collection<? extends PrimaryVersionSpec>) newValue);
			return;
		case VersioningPackage.HISTORY_INFO__MERGED_TO:
			getMergedTo().clear();
			getMergedTo().addAll((Collection<? extends PrimaryVersionSpec>) newValue);
			return;
		case VersioningPackage.HISTORY_INFO__LOG_MESSAGE:
			setLogMessage((LogMessage) newValue);
			return;
		case VersioningPackage.HISTORY_INFO__TAG_SPECS:
			getTagSpecs().clear();
			getTagSpecs().addAll((Collection<? extends TagVersionSpec>) newValue);
			return;
		case VersioningPackage.HISTORY_INFO__VERSION_PROPERTIES:
			getVersionProperties().clear();
			getVersionProperties().addAll((Collection<? extends VersionProperty>) newValue);
			return;
		case VersioningPackage.HISTORY_INFO__CHANGE_PACKAGE:
			setChangePackage((AbstractChangePackage) newValue);
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
		case VersioningPackage.HISTORY_INFO__PRIMARY_SPEC:
			setPrimarySpec((PrimaryVersionSpec) null);
			return;
		case VersioningPackage.HISTORY_INFO__NEXT_SPEC:
			getNextSpec().clear();
			return;
		case VersioningPackage.HISTORY_INFO__PREVIOUS_SPEC:
			setPreviousSpec((PrimaryVersionSpec) null);
			return;
		case VersioningPackage.HISTORY_INFO__MERGED_FROM:
			getMergedFrom().clear();
			return;
		case VersioningPackage.HISTORY_INFO__MERGED_TO:
			getMergedTo().clear();
			return;
		case VersioningPackage.HISTORY_INFO__LOG_MESSAGE:
			setLogMessage((LogMessage) null);
			return;
		case VersioningPackage.HISTORY_INFO__TAG_SPECS:
			getTagSpecs().clear();
			return;
		case VersioningPackage.HISTORY_INFO__VERSION_PROPERTIES:
			getVersionProperties().clear();
			return;
		case VersioningPackage.HISTORY_INFO__CHANGE_PACKAGE:
			setChangePackage((AbstractChangePackage) null);
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
		case VersioningPackage.HISTORY_INFO__PRIMARY_SPEC:
			return primarySpec != null;
		case VersioningPackage.HISTORY_INFO__NEXT_SPEC:
			return nextSpec != null && !nextSpec.isEmpty();
		case VersioningPackage.HISTORY_INFO__PREVIOUS_SPEC:
			return previousSpec != null;
		case VersioningPackage.HISTORY_INFO__MERGED_FROM:
			return mergedFrom != null && !mergedFrom.isEmpty();
		case VersioningPackage.HISTORY_INFO__MERGED_TO:
			return mergedTo != null && !mergedTo.isEmpty();
		case VersioningPackage.HISTORY_INFO__LOG_MESSAGE:
			return logMessage != null;
		case VersioningPackage.HISTORY_INFO__TAG_SPECS:
			return tagSpecs != null && !tagSpecs.isEmpty();
		case VersioningPackage.HISTORY_INFO__VERSION_PROPERTIES:
			return versionProperties != null && !versionProperties.isEmpty();
		case VersioningPackage.HISTORY_INFO__CHANGE_PACKAGE:
			return changePackage != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.api.APIDelegate#toAPI()
	 *
	 * @generated NOT
	 */
	public ESHistoryInfoImpl toAPI() {

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
	public ESHistoryInfoImpl createAPI() {
		return new ESHistoryInfoImpl(this);
	}

} // HistoryInfoImpl