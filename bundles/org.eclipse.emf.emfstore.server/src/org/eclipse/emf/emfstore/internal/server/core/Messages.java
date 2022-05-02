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
package org.eclipse.emf.emfstore.internal.server.core;

import org.eclipse.osgi.util.NLS;

/**
 * Server core related messages.
 *
 * @author emueller
 * @generated
 */
public final class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.emf.emfstore.internal.server.core.messages"; //$NON-NLS-1$
	public static String AbstractSubEmfstoreInterface_Bad_Parameters;
	public static String AbstractSubEmfstoreInterface_Exception_On_Execution;
	public static String AbstractSubEmfstoreInterface_Method_Not_Accessible;
	public static String AdminEmfStoreImpl_Assign_Role_Privilege_Not_Set;
	public static String AdminEmfStoreImpl_Could_Not_Find_OrgUnit;
	public static String AdminEmfStoreImpl_Group_Already_Exists;
	public static String AdminEmfStoreImpl_Group_Does_Not_Exist;
	public static String AdminEmfStoreImpl_IllegalRequestToAddInitialRole;
	public static String AdminEmfStoreImpl_Not_Allowed_To_Assign_ServerAdminRole;
	public static String AdminEmfStoreImpl_Not_Allowed_To_Create_Participant_With_ServerAdminRole;
	public static String AdminEmfStoreImpl_OnlyAllowedForRequstingUser;
	public static String AdminEmfStoreImpl_OrgUnit_Does_Not_Exist;
	public static String AdminEmfStoreImpl_RemovePA_Violation_1;
	public static String AdminEmfStoreImpl_RemovePA_Violation_2;
	public static String AdminEmfStoreImpl_SamePassword;
	public static String AdminEmfStoreImpl_Unknown_ProjectID;
	public static String EMFStoreImpl_Missing_SessionAndOrProjectId;
	public static String EMFStoreImpl_NoCompletedChangePackageFound;
	public static String EMFStoreImpl_NoFragmentAdapterFound;
	public static String EMFStoreImpl_NoValidSessiondIdFound;
	public static String EMFStoreImpl_ServerCallObserverNotifier_Failed;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
