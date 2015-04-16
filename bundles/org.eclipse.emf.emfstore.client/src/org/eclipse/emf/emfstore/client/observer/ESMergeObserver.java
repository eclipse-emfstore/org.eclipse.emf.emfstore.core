/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 * Edgar Mueller - API annotation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.observer;

import java.util.List;

import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.common.ESObserver;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;

/**
 * Callback that is called during the merge process.<br/>
 * The life-cycle of the merging process can be divided into three steps:
 *
 * <ol>
 * <li>first all local changes are reverted</li>
 * <li>then the remote changes are applied</li>
 * <li>finally, the local changes are re-applied</li>
 * </ol>
 *
 * The changes being applied in the 2nd and 3rd steps are filtered by means of a conflict resolver.<br/>
 *
 * @author emueller
 *
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ESMergeObserver extends ESObserver {

	/**
	 * Called before all local changes are reverted.
	 *
	 * @param project
	 *            the {@link ESLocalProject} upon which local changes have been reverted
	 * @param changePackage
	 *            the {@link ESChangePackage} containing the operations being reverted
	 *
	 * @noreference This method is not intended to be referenced by clients.
	 */
	void preRevertMyChanges(ESLocalProject project, ESChangePackage changePackage);

	/**
	 * Called after local changes have been reverted and before incoming
	 * changes are applied.
	 *
	 * @param project
	 *            the {@link ESLocalProject} upon which local changes have been reverted
	 *
	 * @noreference This method is not intended to be referenced by clients.
	 */
	void postRevertMyChanges(ESLocalProject project);

	/**
	 * Called after incoming changes have been applied upon the {@link ESLocalProject} and before
	 * our changes are re-applied.
	 *
	 * @param project
	 *            the {@link ESLocalProject} upon which local changes have been reverted
	 * @param theirChangePackages
	 *            a list of {@link ESChangePackage}s containing the changes that have been applied
	 *            upon the project
	 *
	 * @noreference This method is not intended to be referenced by clients.
	 */
	void postApplyTheirChanges(ESLocalProject project, List<ESChangePackage> theirChangePackages);

	/**
	 * Called after the merge result has been re-applied, i.e. after the incoming changes
	 * from other parties have been applied upon the given project.
	 *
	 * @param project
	 *            the {@link ESLocalProject} upon which changes should have been re-applied
	 * @param changePackage
	 *            the {@link ESChangePackage} containing the changes to be applied upon the project
	 *
	 * @noreference This method is not intended to be referenced by clients.
	 */
	void postApplyMergedChanges(ESLocalProject project, ESChangePackage changePackage);
}
