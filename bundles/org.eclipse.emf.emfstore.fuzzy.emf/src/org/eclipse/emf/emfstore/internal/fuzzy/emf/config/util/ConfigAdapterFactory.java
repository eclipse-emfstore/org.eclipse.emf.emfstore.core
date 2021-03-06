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
package org.eclipse.emf.emfstore.internal.fuzzy.emf.config.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.ConfigPackage;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.DiffReport;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.MutatorConfig;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.Root;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestConfig;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestDiff;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestResult;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestRun;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 *
 * @see org.eclipse.emf.emfstore.internal.fuzzy.emf.config.ConfigPackage
 * @generated
 */
public class ConfigAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected static ConfigPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public ConfigAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = ConfigPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance
	 * object of the model.
	 * <!-- end-user-doc -->
	 *
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject) object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected ConfigSwitch<Adapter> modelSwitch = new ConfigSwitch<Adapter>() {
		@Override
		public Adapter caseTestConfig(TestConfig object) {
			return createTestConfigAdapter();
		}

		@Override
		public Adapter caseTestRun(TestRun object) {
			return createTestRunAdapter();
		}

		@Override
		public Adapter caseTestResult(TestResult object) {
			return createTestResultAdapter();
		}

		@Override
		public Adapter caseTestDiff(TestDiff object) {
			return createTestDiffAdapter();
		}

		@Override
		public Adapter caseDiffReport(DiffReport object) {
			return createDiffReportAdapter();
		}

		@Override
		public Adapter caseRoot(Root object) {
			return createRootAdapter();
		}

		@Override
		public Adapter caseMutatorConfig(MutatorConfig object) {
			return createMutatorConfigAdapter();
		}

		@Override
		public Adapter defaultCase(EObject object) {
			return createEObjectAdapter();
		}
	};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject) target);
	}

	/**
	 * Creates a new adapter for an object of class '
	 * {@link org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestConfig <em>Test Config</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestConfig
	 * @generated
	 */
	public Adapter createTestConfigAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestRun
	 * <em>Test Run</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestRun
	 * @generated
	 */
	public Adapter createTestRunAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '
	 * {@link org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestResult <em>Test Result</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestResult
	 * @generated
	 */
	public Adapter createTestResultAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestDiff
	 * <em>Test Diff</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestDiff
	 * @generated
	 */
	public Adapter createTestDiffAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '
	 * {@link org.eclipse.emf.emfstore.internal.fuzzy.emf.config.DiffReport <em>Diff Report</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see org.eclipse.emf.emfstore.internal.fuzzy.emf.config.DiffReport
	 * @generated
	 */
	public Adapter createDiffReportAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.emf.emfstore.internal.fuzzy.emf.config.Root
	 * <em>Root</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see org.eclipse.emf.emfstore.internal.fuzzy.emf.config.Root
	 * @generated
	 */
	public Adapter createRootAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '
	 * {@link org.eclipse.emf.emfstore.internal.fuzzy.emf.config.MutatorConfig <em>Mutator Config</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @see org.eclipse.emf.emfstore.internal.fuzzy.emf.config.MutatorConfig
	 * @generated
	 */
	public Adapter createMutatorConfigAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 *
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} // ConfigAdapterFactory
