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
package org.eclipse.emf.emfstore.server.model.versioning.provider;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.emfstore.server.model.versioning.util.VersioningAdapterFactory;

/**
 * This is the factory that is used to provide the interfaces needed to support Viewers. The adapters generated by this
 * factory convert EMF adapter notifications into calls to {@link #fireNotifyChanged fireNotifyChanged}. The adapters
 * also support Eclipse property sheets. Note that most of the adapters are shared among multiple instances. <!--
 * begin-user-doc --> <!-- end-user-doc -->
 * 
 * @generated
 */
public class VersioningItemProviderAdapterFactory extends VersioningAdapterFactory implements
	ComposeableAdapterFactory, IChangeNotifier, IDisposable {
	/**
	 * This keeps track of the root adapter factory that delegates to this adapter factory.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	protected ComposedAdapterFactory parentAdapterFactory;

	/**
	 * This is used to implement {@link org.eclipse.emf.edit.provider.IChangeNotifier}.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	protected IChangeNotifier changeNotifier = new ChangeNotifier();

	/**
	 * This keeps track of all the supported types checked by {@link #isFactoryForType isFactoryForType}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected Collection<Object> supportedTypes = new ArrayList<Object>();

	/**
	 * This constructs an instance.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public VersioningItemProviderAdapterFactory() {
		supportedTypes.add(IEditingDomainItemProvider.class);
		supportedTypes.add(IStructuredItemContentProvider.class);
		supportedTypes.add(ITreeItemContentProvider.class);
		supportedTypes.add(IItemLabelProvider.class);
		supportedTypes.add(IItemPropertySource.class);
	}

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.emf.emfstore.server.model.versioning.TagVersionSpec} instances.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected TagVersionSpecItemProvider tagVersionSpecItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.emf.emfstore.server.model.versioning.TagVersionSpec}.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createTagVersionSpecAdapter() {
		if (tagVersionSpecItemProvider == null) {
			tagVersionSpecItemProvider = new TagVersionSpecItemProvider(this);
		}

		return tagVersionSpecItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.emf.emfstore.server.model.versioning.DateVersionSpec} instances.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected DateVersionSpecItemProvider dateVersionSpecItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.emf.emfstore.server.model.versioning.DateVersionSpec}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createDateVersionSpecAdapter() {
		if (dateVersionSpecItemProvider == null) {
			dateVersionSpecItemProvider = new DateVersionSpecItemProvider(this);
		}

		return dateVersionSpecItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.emf.emfstore.server.model.versioning.PrimaryVersionSpec} instances.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	protected PrimaryVersionSpecItemProvider primaryVersionSpecItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.emf.emfstore.server.model.versioning.PrimaryVersionSpec}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createPrimaryVersionSpecAdapter() {
		if (primaryVersionSpecItemProvider == null) {
			primaryVersionSpecItemProvider = new PrimaryVersionSpecItemProvider(this);
		}

		return primaryVersionSpecItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.emf.emfstore.server.model.versioning.LogMessage} instances.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected LogMessageItemProvider logMessageItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.emf.emfstore.server.model.versioning.LogMessage}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createLogMessageAdapter() {
		if (logMessageItemProvider == null) {
			logMessageItemProvider = new LogMessageItemProvider(this);
		}

		return logMessageItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.emf.emfstore.server.model.versioning.ChangePackage} instances.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ChangePackageItemProvider changePackageItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.emf.emfstore.server.model.versioning.ChangePackage}.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createChangePackageAdapter() {
		if (changePackageItemProvider == null) {
			changePackageItemProvider = new ChangePackageItemProvider(this);
		}

		return changePackageItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.emf.emfstore.server.model.versioning.HistoryInfo} instances.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected HistoryInfoItemProvider historyInfoItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.emf.emfstore.server.model.versioning.HistoryInfo}.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createHistoryInfoAdapter() {
		if (historyInfoItemProvider == null) {
			historyInfoItemProvider = new HistoryInfoItemProvider(this);
		}

		return historyInfoItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.emf.emfstore.server.model.versioning.HistoryQuery} instances.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected HistoryQueryItemProvider historyQueryItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.emf.emfstore.server.model.versioning.HistoryQuery}.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createHistoryQueryAdapter() {
		if (historyQueryItemProvider == null) {
			historyQueryItemProvider = new HistoryQueryItemProvider(this);
		}

		return historyQueryItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.emf.emfstore.server.model.versioning.Version}
	 * instances.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected VersionItemProvider versionItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.emf.emfstore.server.model.versioning.Version}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createVersionAdapter() {
		if (versionItemProvider == null) {
			versionItemProvider = new VersionItemProvider(this);
		}

		return versionItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.emf.emfstore.server.model.versioning.HeadVersionSpec} instances.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected HeadVersionSpecItemProvider headVersionSpecItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.emf.emfstore.server.model.versioning.HeadVersionSpec}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createHeadVersionSpecAdapter() {
		if (headVersionSpecItemProvider == null) {
			headVersionSpecItemProvider = new HeadVersionSpecItemProvider(this);
		}

		return headVersionSpecItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.emf.emfstore.server.model.versioning.VersionProperty} instances.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected VersionPropertyItemProvider versionPropertyItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.emf.emfstore.server.model.versioning.VersionProperty}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createVersionPropertyAdapter() {
		if (versionPropertyItemProvider == null) {
			versionPropertyItemProvider = new VersionPropertyItemProvider(this);
		}

		return versionPropertyItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.emf.emfstore.server.model.versioning.BranchVersionSpec} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected BranchVersionSpecItemProvider branchVersionSpecItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.emf.emfstore.server.model.versioning.BranchVersionSpec}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter createBranchVersionSpecAdapter() {
		if (branchVersionSpecItemProvider == null) {
			branchVersionSpecItemProvider = new BranchVersionSpecItemProvider(this);
		}

		return branchVersionSpecItemProvider;
	}

	/**
	 * This returns the root adapter factory that contains this factory.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ComposeableAdapterFactory getRootAdapterFactory() {
		return parentAdapterFactory == null ? this : parentAdapterFactory.getRootAdapterFactory();
	}

	/**
	 * This sets the composed adapter factory that contains this factory.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setParentAdapterFactory(ComposedAdapterFactory parentAdapterFactory) {
		this.parentAdapterFactory = parentAdapterFactory;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object type) {
		return supportedTypes.contains(type) || super.isFactoryForType(type);
	}

	/**
	 * This implementation substitutes the factory itself as the key for the adapter.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Adapter adapt(Notifier notifier, Object type) {
		return super.adapt(notifier, this);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object adapt(Object object, Object type) {
		if (isFactoryForType(type)) {
			Object adapter = super.adapt(object, type);
			if (!(type instanceof Class<?>) || (((Class<?>) type).isInstance(adapter))) {
				return adapter;
			}
		}

		return null;
	}

	/**
	 * This adds a listener.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void addListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.addListener(notifyChangedListener);
	}

	/**
	 * This removes a listener.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void removeListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.removeListener(notifyChangedListener);
	}

	/**
	 * This delegates to {@link #changeNotifier} and to {@link #parentAdapterFactory}.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	public void fireNotifyChanged(Notification notification) {
		changeNotifier.fireNotifyChanged(notification);

		if (parentAdapterFactory != null) {
			parentAdapterFactory.fireNotifyChanged(notification);
		}
	}

	/**
	 * This disposes all of the item providers created by this factory.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void dispose() {
		if (tagVersionSpecItemProvider != null)
			tagVersionSpecItemProvider.dispose();
		if (dateVersionSpecItemProvider != null)
			dateVersionSpecItemProvider.dispose();
		if (primaryVersionSpecItemProvider != null)
			primaryVersionSpecItemProvider.dispose();
		if (logMessageItemProvider != null)
			logMessageItemProvider.dispose();
		if (changePackageItemProvider != null)
			changePackageItemProvider.dispose();
		if (historyInfoItemProvider != null)
			historyInfoItemProvider.dispose();
		if (historyQueryItemProvider != null)
			historyQueryItemProvider.dispose();
		if (versionItemProvider != null)
			versionItemProvider.dispose();
		if (headVersionSpecItemProvider != null)
			headVersionSpecItemProvider.dispose();
		if (versionPropertyItemProvider != null)
			versionPropertyItemProvider.dispose();
		if (branchVersionSpecItemProvider != null)
			branchVersionSpecItemProvider.dispose();
	}

}
