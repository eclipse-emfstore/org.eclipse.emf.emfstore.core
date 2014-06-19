/*******************************************************************************
 * Copyright (c) 2011-2013 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.startup;

import org.eclipse.emf.emfstore.internal.server.startup.UpdateXMIAttributeRule;

/**
 * @author jfaltermeier
 * 
 */
public class ProjectAndChangePackageRule implements UpdateXMIAttributeRule {

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.server.startup.UpdateXMIAttributeRule#getNewAttribute(java.lang.String)
	 */
	public String getNewAttribute(String oldAttr) {
		return oldAttr.split("\\.")[0] + "#/"; //$NON-NLS-1$ //$NON-NLS-2$
	}

}
