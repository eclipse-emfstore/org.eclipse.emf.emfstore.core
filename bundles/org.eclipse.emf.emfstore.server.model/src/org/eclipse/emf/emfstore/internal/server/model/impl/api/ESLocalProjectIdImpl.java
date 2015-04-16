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
package org.eclipse.emf.emfstore.internal.server.model.impl.api;

import org.eclipse.emf.emfstore.server.model.ESLocalProjectId;

/**
 * Local project ID implementation class.
 *
 * @author emueller
 */
public class ESLocalProjectIdImpl implements ESLocalProjectId {

	private final String id;

	/**
	 * Constructor.
	 *
	 * @param id
	 *            the ID of the project as a string
	 */
	public ESLocalProjectIdImpl(String id) {
		this.id = id;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.common.model.ESUniqueIdentifier#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

}
