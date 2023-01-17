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
package org.eclipse.emf.emfstore.internal.server;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.emfstore.internal.server.exceptions.FatalESException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the emfstore life cycle.
 */
public class Activator extends Plugin {

	/**
	 * The plug-in ID.
	 */
	public static final String PLUGIN_ID = "org.eclipse.emf.emfstore.server"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws FatalESException {
		try {
			super.start(context);
			// BEGIN SUPRESS CATCH EXCEPTION
		} catch (final Exception e) {
			throw new FatalESException("Plugin Bundle start failed!", e); //$NON-NLS-1$
		}
		// END SUPRESS CATCH EXCEPTION
		plugin = this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws FatalESException {
		plugin = null;
		try {
			super.stop(context);
			// BEGIN SUPRESS CATCH EXCEPTION
		} catch (final Exception e) {
			throw new FatalESException("Plugin Bundle stop failed!", e); //$NON-NLS-1$
		}
		// END SUPRESS CATCH EXCEPTION
	}

	/**
	 * Logs a new entry for this plugin using the Eclipse platform log.
	 *
	 * @param message
	 *            message
	 * @param statusInt
	 *            severity. Use one of constants in
	 *            org.eclipse.core.runtime.Status class.
	 * @param exception
	 *            exception, may be null
	 */
	public void log(String message, int statusInt, Throwable exception) {
		final Bundle bundle = getBundle();
		if (bundle == null) {
			System.err
				.println(MessageFormat.format("Could not get bundle for id {0}. Log message: {1}", PLUGIN_ID, message)); //$NON-NLS-1$
			return;
		}
		final Status status = new Status(statusInt, bundle.getSymbolicName(), statusInt, message, exception);
		Platform.getLog(bundle).log(status);
	}

	/**
	 * Logs a new warning entry for this plugin using the Eclipse platform log.
	 *
	 * @param message The message to log
	 */
	public void logWarning(String message) {
		log(message, IStatus.WARNING, null);
	}

	/**
	 * Logs a new info entry for this plugin using the Eclipse platform log.
	 *
	 * @param message The message to log
	 */
	public void logInfo(String message) {
		log(message, IStatus.INFO, null);
	}

	/**
	 * Logs a new info entry for this plugin using the Eclipse platform log.
	 *
	 * @param message The message to log
	 * @param exception The exception
	 */
	public void logInfo(String message, Throwable exception) {
		log(message, IStatus.INFO, exception);
	}

	/**
	 * Returns the shared instance.
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}