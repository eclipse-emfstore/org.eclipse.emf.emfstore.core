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
package org.eclipse.emf.emfstore.internal.server.model.versioning.operations.provider;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.ContainmentType;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.OperationsPackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.SingleReferenceOperation;

/**
 * This is the item provider adapter for a
 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.operations.SingleReferenceOperation} object. <!--
 * begin-user-doc --> <!-- end-user-doc -->
 *
 * @generated
 */
public class SingleReferenceOperationItemProvider extends ReferenceOperationItemProvider {
	/**
	 * This constructs an instance from a factory and a notifier. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public SingleReferenceOperationItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * This returns the property descriptors for the adapted class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
		if (itemPropertyDescriptors == null) {
			super.getPropertyDescriptors(object);

			addOldValuePropertyDescriptor(object);
			addNewValuePropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Old Value feature. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected void addOldValuePropertyDescriptor(Object object) {
		itemPropertyDescriptors
			.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
				getResourceLocator(), getString("_UI_SingleReferenceOperation_oldValue_feature"), //$NON-NLS-1$
				getString("_UI_PropertyDescriptor_description", "_UI_SingleReferenceOperation_oldValue_feature", //$NON-NLS-1$ //$NON-NLS-2$
					"_UI_SingleReferenceOperation_type"), //$NON-NLS-1$
				OperationsPackage.Literals.SINGLE_REFERENCE_OPERATION__OLD_VALUE, true, false, true, null, null, null));
	}

	/**
	 * This adds a property descriptor for the New Value feature. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected void addNewValuePropertyDescriptor(Object object) {
		itemPropertyDescriptors
			.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
				getResourceLocator(), getString("_UI_SingleReferenceOperation_newValue_feature"), //$NON-NLS-1$
				getString("_UI_PropertyDescriptor_description", "_UI_SingleReferenceOperation_newValue_feature", //$NON-NLS-1$ //$NON-NLS-2$
					"_UI_SingleReferenceOperation_type"), //$NON-NLS-1$
				OperationsPackage.Literals.SINGLE_REFERENCE_OPERATION__NEW_VALUE, true, false, true, null, null, null));
	}

	// begin of custom code
	/**
	 * @param object
	 *            the object
	 * @return This returns the image.
	 * @generated NOT
	 */
	@Override
	public Object getImage(Object object) {
		return super.getImage(object);
	}

	// end of custom code

	/**
	 * {@inheritDoc} This returns the label text for the adapted class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated NOT
	 */
	@Override
	public String getText(Object object) {
		if (object instanceof SingleReferenceOperation) {
			final SingleReferenceOperation op = (SingleReferenceOperation) object;
			final ModelElementId oldElement = op.getOldValue();
			final ModelElementId newElement = op.getNewValue();
			final String oldName = getModelElementClassAndName(op.getOldValue());
			final String newName = getModelElementClassAndName(op.getNewValue());
			final String elementName = getModelElementClassAndName(op.getModelElementId());

			final boolean isContainer = op.getContainmentType().equals(ContainmentType.CONTAINER);

			// changing containment means relocating the item
			if (isContainer && oldElement != null && newElement != null) {
				return Messages.SingleReferenceOperationItemProvider_Text_Moved + elementName
					+ Messages.SingleReferenceOperationItemProvider_Text_From + oldName
					+ Messages.SingleReferenceOperationItemProvider_Text_To + newName;
			} else if (isContainer && newElement != null) {
				return Messages.SingleReferenceOperationItemProvider_Text_Moved + elementName
					+ Messages.SingleReferenceOperationItemProvider_Text_To + newName;
			} else if (oldElement == null && newElement == null) {
				return Messages.SingleReferenceOperationItemProvider_Text_Unset + op.getFeatureName()
					+ Messages.SingleReferenceOperationItemProvider_Text_In + elementName;
			} else if (oldElement == null && newElement != null) {
				return Messages.SingleReferenceOperationItemProvider_Text_Set + op.getFeatureName()
					+ Messages.SingleReferenceOperationItemProvider_Text_In + elementName
					+ Messages.SingleReferenceOperationItemProvider_Text_To + newName;
			} else if (oldElement != null && newElement == null) {
				return Messages.SingleReferenceOperationItemProvider_Text_Unset + op.getFeatureName()
					+ Messages.SingleReferenceOperationItemProvider_Text_In + elementName
					+ Messages.SingleReferenceOperationItemProvider_Text_FromPreviousValue + oldName;
			} else {
				return Messages.SingleReferenceOperationItemProvider_Text_Set + op.getFeatureName()
					+ Messages.SingleReferenceOperationItemProvider_Text_In + elementName
					+ Messages.SingleReferenceOperationItemProvider_Text_From + oldName
					+ Messages.SingleReferenceOperationItemProvider_Text_To + newName;
			}
		}
		return super.getText(object);
	}

	/**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached
	 * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void notifyChanged(Notification notification) {
		updateChildren(notification);
		super.notifyChanged(notification);
	}

	/**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s
	 * describing the children that can be created under this object. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);
	}

}