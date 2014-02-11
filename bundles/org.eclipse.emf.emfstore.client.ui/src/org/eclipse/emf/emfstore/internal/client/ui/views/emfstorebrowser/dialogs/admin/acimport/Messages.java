/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.views.emfstorebrowser.dialogs.admin.acimport;

import org.eclipse.osgi.util.NLS;

/**
 * Import wizard related messages.
 * 
 * @author emueller
 * 
 */
public final class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.emf.emfstore.internal.client.ui.views.emfstorebrowser.dialogs.admin.acimport.messages"; //$NON-NLS-1$
	public static String ImportController_Insufficient_Access_Rights;
	public static String ImportController_Not_Allowed_To_Create_Groups;
	public static String ImportController_Not_Allowed_To_Create_Users;
	public static String ImportController_Not_Allowed_To_List_Groups;
	public static String ImportController_Not_Allowed_To_List_Users;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
