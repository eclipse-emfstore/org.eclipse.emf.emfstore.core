/*******************************************************************************
 * Copyright (c) 2011-2019 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.common;

/**
 * Exception that will be thrown when no or exclusively insecure protocols available.
 */
public class FatalSocketException extends Exception {

	private static final long serialVersionUID = 3046442855592787845L;

	/**
	 * Creates a new {@link FatalSocketException}.
	 */
	FatalSocketException() {
		super("No secure protocols available."); //$NON-NLS-1$
	}

}
