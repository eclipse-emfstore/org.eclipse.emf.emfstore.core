/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Julian Sommerfeldt - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.fuzzy.emf.internal.diff;

import java.io.IOException;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.emfstore.fuzzy.emf.diff.spi.TestRunProvider;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.FuzzyUtil;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestRun;

/**
 * A {@link TestRunProvider} reading the infos out of files.
 *
 * @author Julian Sommerfeldt
 *
 */
public class FileTestRunProvider extends TestRunProvider {

	@Override
	public TestRun[] getTestRuns() throws IOException {

		final Resource run1Resource = FuzzyUtil
			.createResource("file://D:/downloads/1.xml"); //$NON-NLS-1$
		final Resource run2Resource = FuzzyUtil
			.createResource("file://D:/downloads/2.xml"); //$NON-NLS-1$

		if (FuzzyUtil.resourceExists(run1Resource)) {
			run1Resource.load(null);
		}
		if (FuzzyUtil.resourceExists(run2Resource)) {
			run2Resource.load(null);
		}

		return new TestRun[] { getTestRun(run1Resource),
			getTestRun(run2Resource) };
	}
}
