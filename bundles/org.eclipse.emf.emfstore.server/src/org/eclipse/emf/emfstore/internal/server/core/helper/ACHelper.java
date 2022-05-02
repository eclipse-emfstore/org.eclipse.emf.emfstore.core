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
package org.eclipse.emf.emfstore.internal.server.core.helper;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.emfstore.internal.common.APIUtil;
import org.eclipse.emf.emfstore.internal.server.core.MonitorProvider;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.Role;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESGroupImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESUserImpl;
import org.eclipse.emf.emfstore.server.auth.ESOrgUnitResolver;
import org.eclipse.emf.emfstore.server.model.ESGroup;
import org.eclipse.emf.emfstore.server.model.ESOrgUnit;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitId;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider;
import org.eclipse.emf.emfstore.server.model.ESUser;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * Access control related helper class.
 */
public final class ACHelper {

	private ACHelper() {
		/* static util */
	}

	/**
	 * Uses the given {@link ESOrgUnitProvider} to find the {@link ACOrgUnit} related to the given {@link ESOrgUnitId},
	 * if present.
	 *
	 * @param orgUnitProvider the provider
	 * @param orgUnitId the id
	 * @return the OrgUnit
	 */
	public static Optional<ACOrgUnit<?>> getOrgUnit(ESOrgUnitProvider orgUnitProvider, ESOrgUnitId orgUnitId) {
		Preconditions.checkNotNull(orgUnitId, "orgUnitId must not be null"); //$NON-NLS-1$
		final ACOrgUnitId internalId = APIUtil.toInternal(ACOrgUnitId.class, orgUnitId);

		synchronized (MonitorProvider.getInstance().getMonitor()) {
			for (final ESUser user : orgUnitProvider.getUsers()) {
				final ACUser internalAPI = (ACUser) ESUserImpl.class.cast(user).toInternalAPI();
				if (internalAPI.getId().equals(internalId)) {
					return Optional.<ACOrgUnit<?>> of(internalAPI);
				}
			}
			for (final ESGroup group : orgUnitProvider.getGroups()) {
				final ACGroup internalAPI = (ACGroup) ESGroupImpl.class.cast(group).toInternalAPI();
				if (internalAPI.getId().equals(internalId)) {
					return Optional.<ACOrgUnit<?>> of(internalAPI);
				}
			}
			return Optional.absent();
		}
	}

	/**
	 * Uses the given {@link ESOrgUnitResolver} to get all personal and inherited {@link Role roles} for the given
	 * {@link ACOrgUnit}.
	 * 
	 * @param orgUnitResolver the resolver
	 * @param internalOrgUnit the OrgUnit
	 * @return the roles
	 */
	public static List<Role> getAllRoles(ESOrgUnitResolver orgUnitResolver, ACOrgUnit<?> internalOrgUnit) {
		final ESOrgUnit orgUnit = internalOrgUnit.toAPI();
		final List<ACGroup> groups = APIUtil.toInternal(orgUnitResolver.getGroups(orgUnit));
		final ArrayList<Role> roles = new ArrayList<Role>();
		for (final ACGroup group : groups) {
			roles.addAll(group.getRoles());
		}
		roles.addAll(internalOrgUnit.getRoles());
		return roles;
	}

}
