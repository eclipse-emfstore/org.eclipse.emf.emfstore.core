/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Johannes Faltermeier
 ******************************************************************************/
package org.eclipse.emf.emfstore.server;

import java.util.List;

import org.eclipse.emf.ecore.EPackage;

/**
 * Interface for dynamic model provider.
 *
 * @author jfaltermeier
 * @since 1.1
 *
 */
public interface ESDynamicModelProvider {

	/**
	 * Returns a list of all dynamic models which shall be added to the server's EPackage-Registry.
	 *
	 * @return a list of all dynamic models
	 */
	List<EPackage> getDynamicModels();
}
