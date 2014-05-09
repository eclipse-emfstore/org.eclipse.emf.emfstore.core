/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.controller;

import org.eclipse.osgi.util.NLS;

/**
 * UI controller related messages.
 * 
 * @author emueller
 * 
 */
public final class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.emf.emfstore.internal.client.ui.controller.messages"; //$NON-NLS-1$
	public static String UIAddTagController_ErrorReason;
	public static String UIAddTagController_ErrorTitle;
	public static String UIAddTagController_TagNameTextDefault;
	public static String UIAddTagController_TagNameLabel;
	public static String UIAddTagController_Title;
	public static String UIShareProjectController_SharedSucceeded_Message;
	public static String UIShareProjectController_ShareFailed;
	public static String UIShareProjectController_ShareSucceeded;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
