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
package org.eclipse.emf.emfstore.internal.server.model.versioning.operations.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.emfstore.internal.common.model.IdEObjectCollection;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiAttributeOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.OperationsFactory;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.OperationsPackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.UnkownFeatureException;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.UnsetType;

/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Multi Attribute Operation</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>
 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.operations.impl.MultiAttributeOperationImpl#isAdd
 * <em>Add</em>}</li>
 * <li>
 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.operations.impl.MultiAttributeOperationImpl#getIndexes
 * <em>Indexes</em>}</li>
 * <li>
 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.operations.impl.MultiAttributeOperationImpl#getReferencedValues
 * <em>Referenced Values</em>}</li>
 * </ul>
 *
 * @generated
 */
public class MultiAttributeOperationImpl extends FeatureOperationImpl implements MultiAttributeOperation {
	/**
	 * The default value of the '{@link #isAdd() <em>Add</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #isAdd()
	 * @generated
	 * @ordered
	 */
	protected static final boolean ADD_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isAdd() <em>Add</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #isAdd()
	 * @generated
	 * @ordered
	 */
	protected boolean add = ADD_EDEFAULT;

	/**
	 * The cached value of the '{@link #getIndexes() <em>Indexes</em>}' attribute list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getIndexes()
	 * @generated
	 * @ordered
	 */
	protected EList<Integer> indexes;

	/**
	 * The cached value of the '{@link #getReferencedValues() <em>Referenced Values</em>}' attribute list.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @see #getReferencedValues()
	 * @generated
	 * @ordered
	 */
	protected EList<Object> referencedValues;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected MultiAttributeOperationImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return OperationsPackage.Literals.MULTI_ATTRIBUTE_OPERATION;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public boolean isAdd() {
		return add;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setAdd(boolean newAdd) {
		final boolean oldAdd = add;
		add = newAdd;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, OperationsPackage.MULTI_ATTRIBUTE_OPERATION__ADD,
				oldAdd, add));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EList<Integer> getIndexes() {
		if (indexes == null) {
			indexes = new EDataTypeUniqueEList<Integer>(Integer.class, this,
				OperationsPackage.MULTI_ATTRIBUTE_OPERATION__INDEXES);
		}
		return indexes;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EList<Object> getReferencedValues() {
		if (referencedValues == null) {
			referencedValues = new EDataTypeEList<Object>(Object.class, this,
				OperationsPackage.MULTI_ATTRIBUTE_OPERATION__REFERENCED_VALUES);
		}
		return referencedValues;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case OperationsPackage.MULTI_ATTRIBUTE_OPERATION__ADD:
			return isAdd();
		case OperationsPackage.MULTI_ATTRIBUTE_OPERATION__INDEXES:
			return getIndexes();
		case OperationsPackage.MULTI_ATTRIBUTE_OPERATION__REFERENCED_VALUES:
			return getReferencedValues();
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
		case OperationsPackage.MULTI_ATTRIBUTE_OPERATION__ADD:
			setAdd((Boolean) newValue);
			return;
		case OperationsPackage.MULTI_ATTRIBUTE_OPERATION__INDEXES:
			getIndexes().clear();
			getIndexes().addAll((Collection<? extends Integer>) newValue);
			return;
		case OperationsPackage.MULTI_ATTRIBUTE_OPERATION__REFERENCED_VALUES:
			getReferencedValues().clear();
			getReferencedValues().addAll((Collection<? extends Object>) newValue);
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
		case OperationsPackage.MULTI_ATTRIBUTE_OPERATION__ADD:
			setAdd(ADD_EDEFAULT);
			return;
		case OperationsPackage.MULTI_ATTRIBUTE_OPERATION__INDEXES:
			getIndexes().clear();
			return;
		case OperationsPackage.MULTI_ATTRIBUTE_OPERATION__REFERENCED_VALUES:
			getReferencedValues().clear();
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
		case OperationsPackage.MULTI_ATTRIBUTE_OPERATION__ADD:
			return add != ADD_EDEFAULT;
		case OperationsPackage.MULTI_ATTRIBUTE_OPERATION__INDEXES:
			return indexes != null && !indexes.isEmpty();
		case OperationsPackage.MULTI_ATTRIBUTE_OPERATION__REFERENCED_VALUES:
			return referencedValues != null && !referencedValues.isEmpty();
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
		result.append(" (add: "); //$NON-NLS-1$
		result.append(add);
		result.append(", indexes: "); //$NON-NLS-1$
		result.append(indexes);
		result.append(", referencedValues: "); //$NON-NLS-1$
		result.append(referencedValues);
		result.append(')');
		return result.toString();
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation#apply(org.eclipse.emf.emfstore.internal.common.model.IdEObjectCollection)
	 */
	@SuppressWarnings("unchecked")
	public void apply(IdEObjectCollection project) {
		final EObject modelElement = project.getModelElement(getModelElementId());
		if (modelElement == null) {
			return;
		}

		if (getReferencedValues().size() != getIndexes().size()) {
			// fail silently, invalid operation
			return;
		}

		try {
			final EAttribute feature = (EAttribute) getFeature(modelElement);
			final EList<Object> list = (EList<Object>) modelElement.eGet(feature);

			switch (getUnset().getValue()) {
			case UnsetType.IS_UNSET_VALUE:
				modelElement.eUnset(feature);
				break;
			case UnsetType.NONE_VALUE:
			case UnsetType.WAS_UNSET_VALUE:
				if (isAdd()) {

					for (int i = 0; i < getReferencedValues().size(); i++) {
						final Object value = getReferencedValues().get(i);
						final int index = getIndexes().get(i);
						if (feature.isUnique() && list.contains(value)) {
							// silently skip adding value since it is already contained, but should be unique
							continue;
						}
						if (index > -1 && list.size() >= index) {
							list.add(index, value);
						} else {
							list.add(value);
						}
					}
				} else {
					for (int i = getIndexes().size() - 1; i >= 0; i--) {
						final int index = getIndexes().get(i);
						if (index >= 0 && list.size() > index) {
							list.remove(index);
						}
					}
					if (getIndexes().size() == 0 && getUnset().getValue() == UnsetType.WAS_UNSET_VALUE) {
						modelElement.eSet(feature, list);
					}
				}
				break;
			}

		} catch (final UnkownFeatureException e) {
			return;
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.operations.impl.FeatureOperationImpl#reverse()
	 */
	@Override
	public AbstractOperation reverse() {
		final MultiAttributeOperation operation = OperationsFactory.eINSTANCE.createMultiAttributeOperation();
		super.reverse(operation);
		operation.setAdd(!isAdd());
		operation.getReferencedValues().addAll(getReferencedValues());
		operation.getIndexes().addAll(getIndexes());

		setUnsetForReverseOperation(operation);

		return operation;
	}

} // MultiAttributeOperationImpl