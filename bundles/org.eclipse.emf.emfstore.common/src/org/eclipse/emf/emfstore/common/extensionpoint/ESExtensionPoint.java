/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto von Wesendonk - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.common.extensionpoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.emfstore.internal.common.Activator;

/**
 * This class is a convenience wrapper for eclipse extension points. It can be configured to return null if a value
 * can't be found, but also to throw Exceptions. The latter normally requires a catch block but you don't have to null
 * check.
 *
 * @author wesendon
 */
public class ESExtensionPoint {

	private List<ESExtensionElement> elements;
	private final String id;
	private boolean exceptionInsteadOfNull;
	private Comparator<ESExtensionElement> comparator;

	/**
	 * Default constructor.
	 *
	 * @param id extension point id
	 */
	public ESExtensionPoint(String id) {
		this(id, false);
	}

	/**
	 * Constructor with option of set the throw exception option.
	 *
	 * @param id extension point id
	 * @param throwException if true, an {@link ESExtensionPointException} is thrown instead of returning null
	 */
	public ESExtensionPoint(String id, boolean throwException) {
		this.id = id;
		exceptionInsteadOfNull = throwException;
		comparator = getDefaultComparator();
		reload();
	}

	/**
	 * Constructor with option of set the throw exception option.
	 *
	 * @param id extension point id
	 * @param throwException if true, an {@link ESExtensionPointException} is thrown instead of returning null
	 * @param comparator the comparator which defines the order of the {@link ESExtensionElement}s
	 * @since 1.1
	 */
	public ESExtensionPoint(String id, boolean throwException, Comparator<ESExtensionElement> comparator) {
		this.id = id;
		exceptionInsteadOfNull = throwException;
		this.comparator = comparator;
		reload();
	}

	/**
	 * Reloads extensions from the registry.
	 */
	public void reload() {
		setElements(new ArrayList<ESExtensionElement>());
		for (final IConfigurationElement element : Platform.getExtensionRegistry().getConfigurationElementsFor(id)) {
			getElements().add(new ESExtensionElement(element, exceptionInsteadOfNull));
		}
		Collections.sort(getElements(), comparator);
	}

	/**
	 * @return the extension elements
	 * @since 1.10
	 */
	protected List<ESExtensionElement> getElements() {
		return elements;
	}

	/**
	 * @param elements the elements to set
	 * @since 1.10
	 */
	protected void setElements(List<ESExtensionElement> elements) {
		this.elements = elements;
	}

	/**
	 * Returns the default comparator, it doesn't sort but uses the natural order. This method is intended for
	 * overriding if other default is preferred.
	 *
	 * @return comparator
	 */
	protected Comparator<ESExtensionElement> getDefaultComparator() {
		return new Comparator<ESExtensionElement>() {
			public int compare(ESExtensionElement o1, ESExtensionElement o2) {
				return 0;
			}
		};
	}

	/**
	 * Gets a class from the element with highest priority ({@link #getElementWithHighestPriority()}, default
	 * {@link #getFirst()}). Or rather the registered instance of that class.
	 *
	 * @param classAttributeName class attribute name
	 * @param returnType Class of expected return value
	 * @param <T> the type of the class
	 * @return the result or either null, or an runtime exception is thrown in the case of
	 *         {@link #setThrowException(boolean)} is true.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getClass(String classAttributeName, Class<T> returnType) {
		final ESExtensionElement first = getElementWithHighestPriority();
		if (first != null) {
			return first.getClass(classAttributeName, returnType);
		}
		return (T) handleErrorOrNull(exceptionInsteadOfNull, null, Messages.ESExtensionPoint_ValueNotFound);
	}

	/**
	 * Gets all available classes with the specified type from the elements.
	 *
	 * @param classAttributeName class attribute name
	 * @param returnType Class of expected return value
	 * @param <T> the type of the class
	 * @return the result list that is possibly empty
	 * @since 1.5
	 */
	public <T> List<T> getClasses(String classAttributeName, Class<T> returnType) {
		final List<ESExtensionElement> extensionElements = getExtensionElements();
		final List<T> result = new ArrayList<T>();
		for (final ESExtensionElement extensionElement : extensionElements) {
			final T res = extensionElement.getClass(classAttributeName, returnType);
			if (res != null) {
				result.add(res);
			}
		}

		return result;
	}

	/**
	 * Returns the value of the boolean attribute, if existing, or given false otherwise, from the element with
	 * highest priority ({@link #getElementWithHighestPriority()}, default {@link #getFirst()}).
	 *
	 * @param name attribute id
	 * @return the result or either null, or an runtime exception is thrown in the case of
	 *         {@link #setThrowException(boolean)} is true.
	 */
	public Boolean getBoolean(String name) {
		return getBoolean(name, false);
	}

	/**
	 * Returns the value of the boolean attribute, if existing, or given defaultValue otherwise, from the element with
	 * highest priority ({@link #getElementWithHighestPriority()}, default {@link #getFirst()}).
	 *
	 * @param name attribute id
	 * @param defaultValue the default value if attribute does not exist
	 * @return the result or either null, or an runtime exception is thrown in the case of
	 *         {@link #setThrowException(boolean)} is true.
	 */
	public Boolean getBoolean(String name, boolean defaultValue) {
		final ESExtensionElement element = getElementWithHighestPriority();
		if (element != null) {
			return element.getBoolean(name, defaultValue);
		}
		handleErrorOrNull(exceptionInsteadOfNull, null, Messages.ESExtensionPoint_ValueNotFound);
		return defaultValue;
	}

	/**
	 * Gets a Integer from the element with highest priority ({@link #getElementWithHighestPriority()}, default
	 * {@link #getFirst()}).
	 *
	 * @param name attribute id
	 * @return the result or either null, or an runtime exception is thrown in the case of
	 *         {@link #setThrowException(boolean)} is true.
	 */
	public Integer getInteger(String name) {
		final ESExtensionElement element = getElementWithHighestPriority();
		if (element != null) {
			return element.getInteger(name);
		}
		return (Integer) handleErrorOrNull(exceptionInsteadOfNull, null, Messages.ESExtensionPoint_ValueNotFound);
	}

	/**
	 * Gets an attribute in form of a string from the element with highest priority (
	 * {@link #getElementWithHighestPriority()}, default {@link #getFirst()}).
	 *
	 * @param name attribute id
	 * @return the result or either null, or an runtime exception is thrown in the case of
	 *         {@link #setThrowException(boolean)} is true.
	 */
	public String getAttribute(String name) {
		final ESExtensionElement element = getElementWithHighestPriority();
		if (element != null) {
			return element.getAttribute(name);
		}
		return (String) handleErrorOrNull(exceptionInsteadOfNull, null, Messages.ESExtensionPoint_ValueNotFound);
	}

	/**
	 * Returns the element with highest priority, by default {@link #getFirst()} is used. This method is intended to be
	 * overriden in order to modify default behavior.
	 *
	 * @return {@link ESExtensionElement}
	 */
	public ESExtensionElement getElementWithHighestPriority() {
		return getFirst();
	}

	/**
	 * Set a custom comparator which defines the order of the {@link ESExtensionElement}.
	 *
	 * @param comparator the comparator
	 */
	public void setComparator(Comparator<ESExtensionElement> comparator) {
		this.comparator = comparator;
	}

	/**
	 * Returns the first {@link ESExtensionElement} in the list.
	 *
	 * @return {@link ESExtensionElement}, null or a {@link ESExtensionPointException} is thrown, depending on your
	 *         config ( {@link #setThrowException(boolean)}
	 */
	public ESExtensionElement getFirst() {
		if (getElements().size() > 0) {
			return getElements().get(0);
		}
		return (ESExtensionElement) handleErrorOrNull(exceptionInsteadOfNull, null,
			Messages.ESExtensionPoint_ValueNotFound);
	}

	/**
	 * Returns the wrapped extension elements.
	 *
	 * @return list of {@link ESExtensionElement}
	 */
	public List<ESExtensionElement> getExtensionElements() {
		return Collections.unmodifiableList(getElements());
	}

	/**
	 * Set whether null should be returned or exception should be thrown by this class.
	 *
	 * @param b true to throw exceptions
	 * @return returns this, in order to allow chaining method calls
	 */
	public ESExtensionPoint setThrowException(boolean b) {
		exceptionInsteadOfNull = b;
		return this;
	}

	// public void batch(ForEach expt) {
	// for (ESExtensionElement element : elements) {
	// boolean throwException = element.getThrowException();
	// element.setThrowException(true);
	// try {
	// expt.execute(element);
	// } catch (ESExtensionPointException e) {
	// // do nothing
	// }
	// element.setThrowException(throwException);
	// }
	// }

	/**
	 * This method handles on basis of {@link #setThrowException(boolean)} whether null is returned or an exception is
	 * thrown.
	 *
	 * @param useException chosen option
	 * @param expOrNull exception which will be wrapped, or null, for which an exception can be generated
	 * @param message the exception message in case an exception should be thrown
	 * @return null, or a {@link ESExtensionPointException} is thrown
	 */
	protected static Object handleErrorOrNull(boolean useException, Exception expOrNull, String message) {
		if (useException) {
			if (expOrNull == null) {
				throw new ESExtensionPointException(message);
			}
			logException(expOrNull);
			throw new ESExtensionPointException(expOrNull);
		}
		return null;
	}

	/**
	 * Convenience method for logging.
	 *
	 * @param e exception
	 */
	protected static void logException(Exception e) {
		Activator.getDefault().logException(Messages.ESExtensionPoint_ExceptionOccurred, e);
	}

	/**
	 * Returns the number of {@link ESExtensionElement}.
	 *
	 * @return size
	 */
	public int size() {
		return getElements().size();
	}

	/**
	 * Returns the ID of the extension point.
	 *
	 * @return the ID of the extension point
	 */
	public String getId() {
		return id;
	}

}
