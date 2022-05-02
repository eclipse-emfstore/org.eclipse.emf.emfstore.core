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
package org.eclipse.emf.emfstore.internal.server.accesscontrol;

import org.eclipse.osgi.util.NLS;

/**
 * Access control related messages.
 *
 * @author emueller
 * @generated
 */
public final class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.emf.emfstore.internal.server.accesscontrol.messages"; //$NON-NLS-1$
	public static String AccessControl_CustomAuthorizationInitFailed;
	public static String AccessControl_CustomOrgUnitProviderInitFailed;
	public static String AccessControl_IllegalDelayValueInProperties;
	public static String AccessControl_MultipleExtensionsDiscovered;
	public static String AccessControlImpl_Given_Group_Does_Not_Exist;
	public static String AccessControlImpl_Given_OrgUnit_Does_Not_Exist;
	public static String AccessControlImpl_Given_User_Does_Not_Exist;
	public static String AccessControlImpl_Insufficient_Rights;
	public static String AccessControlImpl_No_Access;
	public static String AccessControlImpl_Not_Allowed_To_Remove_Other_Admin;
	public static String AccessControlImpl_PARole_Missing_Privilege;
	public static String AccessControlImpl_SessionID_Is_Null;
	public static String AccessControlImpl_SessionID_Unknown;
	public static String AccessControlImpl_Unknown_Access_Type;
	public static String DefaultESAuthorizationService_UnknownSession;
	public static String LoginService_CustomLoginServiceInitFailed;
	public static String LoginService_VerifyUserTooManyFailedRequests;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
