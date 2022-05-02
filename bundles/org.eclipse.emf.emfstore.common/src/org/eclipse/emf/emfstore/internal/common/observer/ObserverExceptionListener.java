/*******************************************************************************
 * Copyright (c) 2011-2018 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.common.observer;

import org.eclipse.emf.emfstore.common.ESObserver;

/**
 * Implementors of this interface may register at the {@link ObserverBus} to receive exceptions that have been thrown
 * from {@link org.eclipse.emf.emfstore.common.ESObserver observers}.
 */
public interface ObserverExceptionListener {

	/**
	 * Called when an exception is caught by the {@link ObserverBus}.
	 *
	 * @param observer the {@link ESObserver} causing the exception
	 * @param throwable the {@link Throwable}
	 */
	void onException(ESObserver observer, Throwable throwable);

}
