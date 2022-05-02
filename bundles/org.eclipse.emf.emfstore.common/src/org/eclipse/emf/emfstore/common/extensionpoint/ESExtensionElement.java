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

import static org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint.handleErrorOrNull;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

/**
 * This is the companion class to {@link ESExtensionPoint}.
 * It wraps a {@link IConfigurationElement} for convenience purposes.
 * As {@link ESExtensionPoint} it can be configured to return null or throw an runtime exception
 * {@link ESExtensionPointException}
 *
 * @author wesendon
 *
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class ESExtensionElement {

	private final IConfigurationElement element;
	private boolean exceptionInsteadOfNull;

	/**
	 * Default constructor.
	 *
	 * @param element element to be wrapped
	 */
	public ESExtensionElement(final IConfigurationElement element) {
		this(element, false);
	}

	/**
	 * Constructor, allowing to set whether exceptions should be thrown instead of returning null.
	 *
	 * @param element element to be wrapped
	 * @param throwExceptions if true exceptions are thrown instead of returning null
	 */
	public ESExtensionElement(final IConfigurationElement element, final boolean throwExceptions) {
		this.element = element;
		exceptionInsteadOfNull = throwExceptions;
	}

	/**
	 * Returns the value of the boolean attribute, if existing, or false otherwise.
	 *
	 * @param name attribute id
	 * @return Boolean or an {@link ESExtensionPointException} is thrown
	 */
	public Boolean getBoolean(final String name) {
		return getBoolean(name, false);
	}

	/**
	 * Returns the value of the boolean attribute, if existing, or given defaultValue otherwise.
	 *
	 * @param name attribute id
	 * @param defaultValue the default value
	 * @return Boolean or an {@link ESExtensionPointException} is thrown
	 */
	public Boolean getBoolean(final String name, final boolean defaultValue) {
		final String attribute = getAttribute(name);
		if (attribute == null) {
			return defaultValue;
		}
		return Boolean.parseBoolean(attribute);
	}

	/**
	 * Returns an Integer attribute.
	 *
	 * @param name attribute id
	 * @return Integer, null or an {@link ESExtensionPointException} is thrown
	 */
	public Integer getInteger(final String name) {
		try {
			return Integer.parseInt(getAttribute(name));
		} catch (final NumberFormatException e) {
			return (Integer) handleErrorOrNull(exceptionInsteadOfNull, e, Messages.ESExtensionPoint_ValueNotFound);
		}
	}

	/**
	 * Returns an attribute as string.
	 *
	 * @param name attribute id
	 * @return String, null or an {@link ESExtensionPointException} is thrown
	 */
	public String getAttribute(final String name) {
		final String attribute = element.getAttribute(name);
		if (attribute == null) {
			handleErrorOrNull(exceptionInsteadOfNull, null, Messages.ESExtensionPoint_ValueNotFound);
		}
		return attribute;
	}

	/**
	 * Returns a class, or rather the registered instance of this class.
	 *
	 * @param classAttributeName attribute name of the class attribute
	 * @param returnType expected class type
	 * @param <T> type of class
	 * @return Instance, null or a {@link ESExtensionPointException} is thrown
	 */
	@SuppressWarnings("unchecked")
	public <T> T getClass(final String classAttributeName, final Class<T> returnType) {
		try {
			final Object executableExtension = element.createExecutableExtension(classAttributeName);
			if (returnType.isInstance(executableExtension)) {
				return (T) executableExtension;
			}
			return (T) handleErrorOrNull(exceptionInsteadOfNull, null, Messages.ESExtensionPoint_ValueNotFound);
		} catch (final CoreException e) {
			return (T) handleErrorOrNull(exceptionInsteadOfNull, e, Messages.ESExtensionPoint_ValueNotFound);
		}
	}

	/**
	 * Returns the wrapped element.
	 *
	 * @return {@link IConfigurationElement}
	 */
	public IConfigurationElement getIConfigurationElement() {
		return element;
	}

	/**
	 * Set wrapper to throw exceptions or otherwise return null.
	 *
	 * @param throwException if true, exceptions are thrown
	 */
	public void setThrowException(boolean throwException) {
		exceptionInsteadOfNull = throwException;
	}

	/**
	 * Returns whether exceptions are thrown or null is returned.
	 *
	 * @return boolean
	 */
	public boolean getThrowException() {
		return exceptionInsteadOfNull;
	}
}
