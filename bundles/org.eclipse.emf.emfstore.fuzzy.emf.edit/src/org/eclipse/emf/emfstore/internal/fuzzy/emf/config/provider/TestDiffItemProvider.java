/**
 * Copyright (c) 2008-2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 */
package org.eclipse.emf.emfstore.internal.fuzzy.emf.config.provider;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.ConfigFactory;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.ConfigPackage;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestDiff;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestDiff} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class TestDiffItemProvider
	extends ItemProviderAdapter
	implements
	IEditingDomainItemProvider, ITreeItemContentProvider, IItemLabelProvider,
	IItemPropertySource {
	/**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public TestDiffItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * This returns the property descriptors for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
		if (itemPropertyDescriptors == null) {
			super.getPropertyDescriptors(object);

			addLastUpdatePropertyDescriptor(object);
			addConfigPropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Last Update feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected void addLastUpdatePropertyDescriptor(Object object) {
		itemPropertyDescriptors
			.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
				getResourceLocator(),
				getString("_UI_TestDiff_lastUpdate_feature"), //$NON-NLS-1$
				getString("_UI_PropertyDescriptor_description", "_UI_TestDiff_lastUpdate_feature", "_UI_TestDiff_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				ConfigPackage.Literals.TEST_DIFF__LAST_UPDATE,
				true,
				false,
				false,
				ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				null,
				null));
	}

	/**
	 * This adds a property descriptor for the Config feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected void addConfigPropertyDescriptor(Object object) {
		itemPropertyDescriptors
			.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
				getResourceLocator(),
				getString("_UI_TestDiff_config_feature"), //$NON-NLS-1$
				getString("_UI_PropertyDescriptor_description", "_UI_TestDiff_config_feature", "_UI_TestDiff_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				ConfigPackage.Literals.TEST_DIFF__CONFIG,
				true,
				false,
				true,
				null,
				null,
				null));
	}

	/**
	 * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
	 * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
	 * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
		if (childrenFeatures == null) {
			super.getChildrenFeatures(object);
			childrenFeatures.add(ConfigPackage.Literals.TEST_DIFF__OLD_RESULT);
			childrenFeatures.add(ConfigPackage.Literals.TEST_DIFF__NEW_RESULT);
		}
		return childrenFeatures;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	protected EStructuralFeature getChildFeature(Object object, Object child) {
		// Check the type of the specified child object and return the proper feature to use for
		// adding (see {@link AddCommand}) it as a child.

		return super.getChildFeature(object, child);
	}

	/**
	 * This returns TestDiff.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/TestDiff")); //$NON-NLS-1$
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String getText(Object object) {
		final Date labelValue = ((TestDiff) object).getLastUpdate();
		final String label = labelValue == null ? null : labelValue.toString();
		return label == null || label.length() == 0 ? getString("_UI_TestDiff_type") : //$NON-NLS-1$
			getString("_UI_TestDiff_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached
	 * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void notifyChanged(Notification notification) {
		updateChildren(notification);

		switch (notification.getFeatureID(TestDiff.class)) {
		case ConfigPackage.TEST_DIFF__LAST_UPDATE:
			fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
			return;
		case ConfigPackage.TEST_DIFF__OLD_RESULT:
		case ConfigPackage.TEST_DIFF__NEW_RESULT:
			fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
			return;
		}
		super.notifyChanged(notification);
	}

	/**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
	 * that can be created under this object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);

		newChildDescriptors.add(createChildParameter(ConfigPackage.Literals.TEST_DIFF__OLD_RESULT,
			ConfigFactory.eINSTANCE.createTestResult()));

		newChildDescriptors.add(createChildParameter(ConfigPackage.Literals.TEST_DIFF__NEW_RESULT,
			ConfigFactory.eINSTANCE.createTestResult()));
	}

	/**
	 * This returns the label text for {@link org.eclipse.emf.edit.command.CreateChildCommand}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String getCreateChildText(Object owner, Object feature, Object child, Collection<?> selection) {
		final Object childFeature = feature;
		final Object childObject = child;

		final boolean qualify = childFeature == ConfigPackage.Literals.TEST_DIFF__OLD_RESULT ||
			childFeature == ConfigPackage.Literals.TEST_DIFF__NEW_RESULT;

		if (qualify) {
			return getString("_UI_CreateChild_text2", //$NON-NLS-1$
				new Object[] { getTypeText(childObject), getFeatureText(childFeature), getTypeText(owner) });
		}
		return super.getCreateChildText(owner, feature, child, selection);
	}

	/**
	 * Return the resource locator for this item provider's resources.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public ResourceLocator getResourceLocator() {
		return ConfigEditPlugin.INSTANCE;
	}

}
