/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.util;

import org.eclipse.osgi.util.NLS;

/**
 * Internal utility relatd messages.
 *
 * @author emueller
 * @generated
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.emf.emfstore.internal.client.ui.util.messages"; //$NON-NLS-1$
	public static String EMFStorePreferenceHelper_SavePreferenceFailed;
	public static String PasswordHelper_PasswordDoesNotMatchDefault;
	public static String PasswordHelper_PatternInvalid;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
