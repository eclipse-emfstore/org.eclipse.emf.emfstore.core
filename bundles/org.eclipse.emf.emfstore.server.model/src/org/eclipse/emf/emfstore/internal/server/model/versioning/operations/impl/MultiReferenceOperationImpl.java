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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.emfstore.internal.common.model.IdEObjectCollection;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.impl.IdEObjectCollectionImpl;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiReferenceOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.OperationsFactory;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.OperationsPackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.UnkownFeatureException;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.UnsetType;

/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Multi Reference Operation</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>
 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.operations.impl.MultiReferenceOperationImpl#isAdd
 * <em>Add</em>}</li>
 * <li>
 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.operations.impl.MultiReferenceOperationImpl#getIndex
 * <em>Index</em>}</li>
 * <li>
 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.operations.impl.MultiReferenceOperationImpl#getReferencedModelElements
 * <em>Referenced Model Elements</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class MultiReferenceOperationImpl extends ReferenceOperationImpl implements MultiReferenceOperation {

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
	 * The default value of the '{@link #getIndex() <em>Index</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getIndex()
	 * @generated
	 * @ordered
	 */
	protected static final int INDEX_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getIndex() <em>Index</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getIndex()
	 * @generated
	 * @ordered
	 */
	protected int index = INDEX_EDEFAULT;

	/**
	 * The cached value of the '{@link #getReferencedModelElements()
	 * <em>Referenced Model Elements</em>}' containment reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getReferencedModelElements()
	 * @generated
	 * @ordered
	 */
	protected EList<ModelElementId> referencedModelElements;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected MultiReferenceOperationImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return OperationsPackage.Literals.MULTI_REFERENCE_OPERATION;
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
		boolean oldAdd = add;
		add = newAdd;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OperationsPackage.MULTI_REFERENCE_OPERATION__ADD,
				oldAdd, add));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setIndex(int newIndex) {
		int oldIndex = index;
		index = newIndex;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, OperationsPackage.MULTI_REFERENCE_OPERATION__INDEX,
				oldIndex, index));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public EList<ModelElementId> getReferencedModelElements() {
		if (referencedModelElements == null)
		{
			referencedModelElements = new EObjectContainmentEList.Resolving<ModelElementId>(ModelElementId.class, this,
				OperationsPackage.MULTI_REFERENCE_OPERATION__REFERENCED_MODEL_ELEMENTS);
		}
		return referencedModelElements;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID)
		{
		case OperationsPackage.MULTI_REFERENCE_OPERATION__REFERENCED_MODEL_ELEMENTS:
			return ((InternalEList<?>) getReferencedModelElements()).basicRemove(otherEnd, msgs);
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
		switch (featureID)
		{
		case OperationsPackage.MULTI_REFERENCE_OPERATION__ADD:
			return isAdd();
		case OperationsPackage.MULTI_REFERENCE_OPERATION__INDEX:
			return getIndex();
		case OperationsPackage.MULTI_REFERENCE_OPERATION__REFERENCED_MODEL_ELEMENTS:
			return getReferencedModelElements();
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
		switch (featureID)
		{
		case OperationsPackage.MULTI_REFERENCE_OPERATION__ADD:
			setAdd((Boolean) newValue);
			return;
		case OperationsPackage.MULTI_REFERENCE_OPERATION__INDEX:
			setIndex((Integer) newValue);
			return;
		case OperationsPackage.MULTI_REFERENCE_OPERATION__REFERENCED_MODEL_ELEMENTS:
			getReferencedModelElements().clear();
			getReferencedModelElements().addAll((Collection<? extends ModelElementId>) newValue);
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
		switch (featureID)
		{
		case OperationsPackage.MULTI_REFERENCE_OPERATION__ADD:
			setAdd(ADD_EDEFAULT);
			return;
		case OperationsPackage.MULTI_REFERENCE_OPERATION__INDEX:
			setIndex(INDEX_EDEFAULT);
			return;
		case OperationsPackage.MULTI_REFERENCE_OPERATION__REFERENCED_MODEL_ELEMENTS:
			getReferencedModelElements().clear();
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
		switch (featureID)
		{
		case OperationsPackage.MULTI_REFERENCE_OPERATION__ADD:
			return add != ADD_EDEFAULT;
		case OperationsPackage.MULTI_REFERENCE_OPERATION__INDEX:
			return index != INDEX_EDEFAULT;
		case OperationsPackage.MULTI_REFERENCE_OPERATION__REFERENCED_MODEL_ELEMENTS:
			return referencedModelElements != null && !referencedModelElements.isEmpty();
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
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (add: ");
		result.append(add);
		result.append(", index: ");
		result.append(index);
		result.append(')');
		return result.toString();
	}

	public void apply(IdEObjectCollection project) {

		EObject modelElement = project.getModelElement(getModelElementId());

		if (modelElement == null) {
			// fail silently
			return;
		}

		Map<EObject, ModelElementId> allocatables = new LinkedHashMap<EObject, ModelElementId>();
		EList<ModelElementId> referencedModelElementIds = getReferencedModelElements();
		List<EObject> referencedModelElements = new ArrayList<EObject>();

		for (ModelElementId refrencedModelElementId : referencedModelElementIds) {
			EObject referencedModelElement = project.getModelElement(refrencedModelElementId);
			if (referencedModelElement == null) {
				referencedModelElement = ((IdEObjectCollectionImpl) project).getDeletedModelElement(refrencedModelElementId);
			}
			if (referencedModelElement != null) {
				referencedModelElements.add(referencedModelElement);
				allocatables.put(referencedModelElement, refrencedModelElementId);
			}
		}
		EReference reference;
		try {
			reference = (EReference) this.getFeature(modelElement);
		} catch (UnkownFeatureException e) {
			// fail silently
			return;
		}
		Object object = modelElement.eGet(reference);
		@SuppressWarnings("unchecked")
		EList<EObject> list = (EList<EObject>) object;

		switch (getUnset().getValue()) {
		case UnsetType.IS_UNSET_VALUE:
			modelElement.eUnset(reference);
			// Fall-through because we only want to set the unset-flag of the list. The reference itself shall be
			// treated as before. If no fall-through the reference will always be removed from the project by unset, but
			// this is not intended.
			// $FALL-THROUGH$
		case UnsetType.NONE_VALUE:
		case UnsetType.WAS_UNSET_VALUE:
			if (isAdd()) {
				if (index < list.size() && index > -1) {
					int i = index;
					for (EObject m : referencedModelElements) {

						if (i < list.size()) {
							if (list.contains(m)) {
								list.move(i, m);
							} else {
								list.add(i, m);
							}
						} else {
							// index grows out of bounds
							if (list.contains(m)) {
								// if element already in list, move to the end
								list.move(list.size() - 1, m);
								i--;
							} else {
								// if element not in list, just append
								list.add(m);
							}
						}

						i++;
					}
					// list.addAll(getIndex(), referencedModelElements);
				} else {
					// if index is out of range ignore index
					for (EObject m : referencedModelElements) {
						if (list.contains(m)) {
							// move to end
							list.move(list.size() - 1, m);
						} else {
							// append
							list.add(m);
						}
					}
				}
			} else {

				for (EObject me : referencedModelElements) {
					if (list.contains(me)) {
						list.remove(me);
					}
				}

				project.allocateModelElementIds(allocatables);

				for (EObject currentElement : referencedModelElements) {
					if (reference.isContainment()) {
						project.addModelElement(currentElement);
					}
				}

				if (referencedModelElements.size() == 0 && getUnset().getValue() == UnsetType.WAS_UNSET_VALUE) {
					modelElement.eSet(reference, list);
				}
			}
			break;
		}
	}

	@Override
	public AbstractOperation reverse() {
		MultiReferenceOperation multiReferenceOperation = OperationsFactory.eINSTANCE.createMultiReferenceOperation();
		super.reverse(multiReferenceOperation);
		multiReferenceOperation.setAdd(!isAdd());
		EList<ModelElementId> copiedReferencedModelElements = multiReferenceOperation.getReferencedModelElements();
		EList<ModelElementId> modelElements = getReferencedModelElements();
		for (ModelElementId modelElementId : modelElements) {
			copiedReferencedModelElements.add(ModelUtil.clone(modelElementId));
		}
		multiReferenceOperation.setIndex(getIndex());

		setUnsetForReverseOperation(multiReferenceOperation);

		return multiReferenceOperation;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation#getOtherInvolvedModelElements()
	 */
	@Override
	public Set<ModelElementId> getOtherInvolvedModelElements() {
		Set<ModelElementId> set = new LinkedHashSet<ModelElementId>();
		set.addAll(getReferencedModelElements());
		return set;
	}

} // MultiReferenceOperationImpl