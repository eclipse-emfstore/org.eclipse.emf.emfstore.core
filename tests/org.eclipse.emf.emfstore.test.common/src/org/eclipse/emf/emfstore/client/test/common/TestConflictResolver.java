/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.test.common;

import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.AbstractConflictResolver;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.DecisionManager;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.ConflictOption.OptionType;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.VisualConflict;
import org.eclipse.emf.emfstore.internal.server.conflictDetection.ChangeConflictSet;

public class TestConflictResolver extends AbstractConflictResolver {

	private final int expectedConflicts;

	public TestConflictResolver(boolean isBranchMerge, int expectedConflicts) {
		super(isBranchMerge);
		this.expectedConflicts = expectedConflicts;
	}

	@Override
	protected boolean controlDecisionManager(DecisionManager decisionManager, ChangeConflictSet changeConflictSet) {
		int counter = 0;
		for (final VisualConflict conflict : decisionManager.getConflicts()) {
			conflict.setSolution(conflict.getOptionOfType(OptionType.MyOperation));
			counter++;
		}
		if (!decisionManager.isResolved()) {
			throw new RuntimeException("Conflicts not resolved"); //$NON-NLS-1$
		}
		if (counter > -1 && counter != expectedConflicts) {
			throw new RuntimeException("more or less conflicts then expected"); //$NON-NLS-1$
		}
		return true;
	}

}