/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.impl.api;

import java.util.Set;

import org.eclipse.emf.emfstore.internal.common.APIUtil;
import org.eclipse.emf.emfstore.internal.common.api.InternalAPIDelegator;
import org.eclipse.emf.emfstore.internal.server.conflictDetection.ChangeConflictSet;
import org.eclipse.emf.emfstore.server.ESConflict;
import org.eclipse.emf.emfstore.server.ESConflictSet;

/**
 * <p>
 * Mapping between {@link ESConflictSet} and {@link ChangeConflictSet}.
 * </p>
 * <p>
 * Note that this class does not inherit from {@link org.eclipse.emf.emfstore.internal.common.api.AbstractAPIImpl
 * AbstractAPIImpl} since {@link ChangeConflictSet} is not a modeled class.
 * </p>
 *
 * @author emueller
 *
 */
public class ESConflictSetImpl implements ESConflictSet, InternalAPIDelegator<ESConflictSet, ChangeConflictSet> {

	private final ChangeConflictSet changeConflict;

	/**
	 * Constructor.
	 *
	 * @param changeConflict
	 *            the delegate
	 */
	public ESConflictSetImpl(ChangeConflictSet changeConflict) {
		this.changeConflict = changeConflict;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.api.InternalAPIDelegator#toInternalAPI()
	 */
	@Override
	public ChangeConflictSet toInternalAPI() {
		return changeConflict;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.ESConflictSet#getConflicts()
	 */
	@Override
	public Set<ESConflict> getConflicts() {
		final Set<ESConflict> conflicts = APIUtil
			.toExternal(toInternalAPI().getConflictBuckets());
		return conflicts;

	}
}
