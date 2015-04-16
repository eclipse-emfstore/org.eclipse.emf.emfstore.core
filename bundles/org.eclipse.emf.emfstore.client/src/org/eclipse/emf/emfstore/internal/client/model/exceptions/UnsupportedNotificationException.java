/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Maximilian Koegel - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.exceptions;

/**
 * Represents exceptional condition where the notification is unknown to a receiver.
 *
 * @author koegel
 */
@SuppressWarnings("serial")
public class UnsupportedNotificationException extends Exception {

	/**
	 * Default constructor.
	 *
	 * @param message the message
	 */
	public UnsupportedNotificationException(String message) {
		super(message);
	}

}
