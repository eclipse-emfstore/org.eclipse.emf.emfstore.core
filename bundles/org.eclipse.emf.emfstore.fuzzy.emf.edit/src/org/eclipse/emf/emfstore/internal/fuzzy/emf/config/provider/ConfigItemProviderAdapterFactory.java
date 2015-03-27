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
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.util.ConfigAdapterFactory;

/**
 * This is the factory that is used to provide the interfaces needed to support Viewers.
 * The adapters generated by this factory convert EMF adapter notifications into calls to {@link #fireNotifyChanged
 * fireNotifyChanged}.
 * The adapters also support Eclipse property sheets.
 * Note that most of the adapters are shared among multiple instances.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class ConfigItemProviderAdapterFactory extends ConfigAdapterFactory implements ComposeableAdapterFactory,
	IChangeNotifier, IDisposable
{
	/**
	 * This keeps track of the root adapter factory that delegates to this adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected ComposedAdapterFactory parentAdapterFactory;

	/**
	 * This is used to implement {@link org.eclipse.emf.edit.provider.IChangeNotifier}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected IChangeNotifier changeNotifier = new ChangeNotifier();

	/**
	 * This keeps track of all the supported types checked by {@link #isFactoryForType isFactoryForType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected Collection<Object> supportedTypes = new ArrayList<Object>();

	/**
	 * This constructs an instance.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public ConfigItemProviderAdapterFactory()
	{
		supportedTypes.add(IEditingDomainItemProvider.class);
		supportedTypes.add(IStructuredItemContentProvider.class);
		supportedTypes.add(ITreeItemContentProvider.class);
		supportedTypes.add(IItemLabelProvider.class);
		supportedTypes.add(IItemPropertySource.class);
	}

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestConfig} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected TestConfigItemProvider testConfigItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestConfig}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Adapter createTestConfigAdapter()
	{
		if (testConfigItemProvider == null)
		{
			testConfigItemProvider = new TestConfigItemProvider(this);
		}

		return testConfigItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestRun} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected TestRunItemProvider testRunItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestRun}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Adapter createTestRunAdapter()
	{
		if (testRunItemProvider == null)
		{
			testRunItemProvider = new TestRunItemProvider(this);
		}

		return testRunItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestResult} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected TestResultItemProvider testResultItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestResult}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Adapter createTestResultAdapter()
	{
		if (testResultItemProvider == null)
		{
			testResultItemProvider = new TestResultItemProvider(this);
		}

		return testResultItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestDiff} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected TestDiffItemProvider testDiffItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestDiff}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Adapter createTestDiffAdapter()
	{
		if (testDiffItemProvider == null)
		{
			testDiffItemProvider = new TestDiffItemProvider(this);
		}

		return testDiffItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.emf.emfstore.internal.fuzzy.emf.config.DiffReport} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected DiffReportItemProvider diffReportItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.emf.emfstore.internal.fuzzy.emf.config.DiffReport}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Adapter createDiffReportAdapter()
	{
		if (diffReportItemProvider == null)
		{
			diffReportItemProvider = new DiffReportItemProvider(this);
		}

		return diffReportItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.emf.emfstore.internal.fuzzy.emf.config.Root}
	 * instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected RootItemProvider rootItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.emf.emfstore.internal.fuzzy.emf.config.Root}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Adapter createRootAdapter()
	{
		if (rootItemProvider == null)
		{
			rootItemProvider = new RootItemProvider(this);
		}

		return rootItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.emf.emfstore.internal.fuzzy.emf.config.MutatorConfig} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected MutatorConfigItemProvider mutatorConfigItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.emf.emfstore.internal.fuzzy.emf.config.MutatorConfig}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Adapter createMutatorConfigAdapter()
	{
		if (mutatorConfigItemProvider == null)
		{
			mutatorConfigItemProvider = new MutatorConfigItemProvider(this);
		}

		return mutatorConfigItemProvider;
	}

	/**
	 * This returns the root adapter factory that contains this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public ComposeableAdapterFactory getRootAdapterFactory()
	{
		return parentAdapterFactory == null ? this : parentAdapterFactory.getRootAdapterFactory();
	}

	/**
	 * This sets the composed adapter factory that contains this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setParentAdapterFactory(ComposedAdapterFactory parentAdapterFactory)
	{
		this.parentAdapterFactory = parentAdapterFactory;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object type)
	{
		return supportedTypes.contains(type) || super.isFactoryForType(type);
	}

	/**
	 * This implementation substitutes the factory itself as the key for the adapter.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Adapter adapt(Notifier notifier, Object type)
	{
		return super.adapt(notifier, this);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Object adapt(Object object, Object type)
	{
		if (isFactoryForType(type))
		{
			final Object adapter = super.adapt(object, type);
			if (!(type instanceof Class<?>) || ((Class<?>) type).isInstance(adapter))
			{
				return adapter;
			}
		}

		return null;
	}

	/**
	 * This adds a listener.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void addListener(INotifyChangedListener notifyChangedListener)
	{
		changeNotifier.addListener(notifyChangedListener);
	}

	/**
	 * This removes a listener.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void removeListener(INotifyChangedListener notifyChangedListener)
	{
		changeNotifier.removeListener(notifyChangedListener);
	}

	/**
	 * This delegates to {@link #changeNotifier} and to {@link #parentAdapterFactory}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void fireNotifyChanged(Notification notification)
	{
		changeNotifier.fireNotifyChanged(notification);

		if (parentAdapterFactory != null)
		{
			parentAdapterFactory.fireNotifyChanged(notification);
		}
	}

	/**
	 * This disposes all of the item providers created by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void dispose()
	{
		if (testConfigItemProvider != null) {
			testConfigItemProvider.dispose();
		}
		if (testRunItemProvider != null) {
			testRunItemProvider.dispose();
		}
		if (testResultItemProvider != null) {
			testResultItemProvider.dispose();
		}
		if (testDiffItemProvider != null) {
			testDiffItemProvider.dispose();
		}
		if (diffReportItemProvider != null) {
			diffReportItemProvider.dispose();
		}
		if (rootItemProvider != null) {
			rootItemProvider.dispose();
		}
		if (mutatorConfigItemProvider != null) {
			mutatorConfigItemProvider.dispose();
		}
	}

}
