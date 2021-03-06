/*******************************************************************************
 * Copyright (c) 2012-2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Julian Sommerfeldt - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.fuzzy.emf.junit;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.emfstore.modelmutator.ESAbstractModelMutator;
import org.eclipse.emf.emfstore.modelmutator.ESModelMutatorConfiguration;
import org.junit.runner.notification.RunListener;
import org.junit.runners.model.TestClass;

/**
 * A Data Provider for the JUnit Runner: {@link org.eclipse.emf.emfstore.fuzzy.emf.junit.ESFuzzyRunner ESFuzzyRunner}.
 * <br>
 * <br>
 * An implementation of it must have a zero-parameter constructor.
 *
 * @author Julian Sommerfeldt
 *
 * @param <T>
 *            Type to specify the values created by this data provider.
 * @since 2.0
 *
 */
public interface ESFuzzyDataProvider<T> {

	/**
	 * Creates and returns the data for the next run.<br>
	 * <br>
	 * Note that it is strongly recommended to instantiate the data in this
	 * method for every call and not in the instantiation of the class, e.g. the
	 * init method, to avoid overloading the memory.
	 *
	 * @param count
	 *            The count of the testcase.
	 * @return The data for the next run of the test class.
	 */
	T get(int count);

	/**
	 * This method is called after the {@link ESFuzzyDataProvider} was created and
	 * everything was set BEFORE the first run.<br/>
	 * Should be used to to create internal stuff depending on e.g. the {@link TestClass}.
	 */
	void init();

	/**
	 * @return The total size(count) of the repetition of the tests.
	 */
	int size();

	/**
	 * @param testClass
	 *            The {@link TestClass} of the calling {@link org.eclipse.emf.emfstore.fuzzy.emf.junit.ESFuzzyRunner
	 *            ESFuzzyRunner}.
	 */
	void setTestClass(TestClass testClass);

	/**
	 * @return A list of listeners to add to the runner, e.g. to get information
	 *         about errors. <code>null</code> permitted.
	 */
	List<RunListener> getListener();

	/**
	 * @return A list of {@link ESFuzzyTest}s to specify, which tests the
	 *         {@link org.eclipse.emf.emfstore.fuzzy.emf.junit.ESFuzzyRunner ESFuzzyRunner} should run.
	 *         <code>null</code> means
	 *         run all tests.
	 */
	List<ESFuzzyTest> getTestsToRun();

	/**
	 * @return The {@link ESFuzzyUtil} for this {@link ESFuzzyDataProvider}. <code>null</code> permitted.
	 */
	ESFuzzyUtil getUtil();

	/**
	 * Sets the options for the {@link ESFuzzyDataProvider}. May be {@code null}.
	 *
	 * @param options
	 *            The options for the {@link ESFuzzyDataProvider}. Can be <code>null</code>.
	 */
	void setOptions(Map<String, Object> options);

	/**
	 * Sets the mutator to be used in order to generate and mutate data.
	 *
	 * @param modelMutator
	 *            the {@link ESAbstractModelMutator} to be used
	 */
	void setMutator(ESAbstractModelMutator modelMutator);

	/**
	 * Returns the {@link ESModelMutatorConfiguration} used by the data provider.
	 *
	 * @return the {@link ESModelMutatorConfiguration} used by the data provider
	 */
	ESModelMutatorConfiguration getModelMutatorConfiguration();
}
