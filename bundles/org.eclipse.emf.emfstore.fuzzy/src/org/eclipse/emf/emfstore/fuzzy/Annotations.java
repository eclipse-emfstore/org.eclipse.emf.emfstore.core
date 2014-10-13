/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Julian Sommerfeldt - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.fuzzy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotations used in tests using the {@link ESFuzzyRunner}.
 * 
 * @author Julian Sommerfeldt
 * 
 */
public final class Annotations {

	/**
	 * Annotation to declare the field in the test, where to set the data.
	 * 
	 * @author Julian Sommerfeldt
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD })
	public @interface Data {
	}

	/**
	 * Annotations to declare the field for the {@link ESFuzzyUtil}.
	 * 
	 * @author Julian Sommerfeldt
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD })
	public @interface Util {
	}

	/**
	 * An annotation to set the fuzzy data provider for the {@link ESFuzzyRunner}.
	 * 
	 * @author Julian Sommerfeldt
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE })
	public @interface DataProvider {

		/***/
		Class<?> value();
	}

	/**
	 * An optional annotation to declare options to use in the fuzzy data provider.
	 * 
	 * @author Julian Sommerfeldt
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD })
	public @interface Options {
	}
}
