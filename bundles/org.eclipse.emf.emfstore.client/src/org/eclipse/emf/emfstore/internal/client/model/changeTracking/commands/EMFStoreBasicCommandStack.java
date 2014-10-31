/*******************************************************************************
 * Copyright (c) 2008-2014 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Maximilian Koegel - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.changeTracking.commands;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.emfstore.client.changetracking.ESCommandObserver;
import org.eclipse.emf.emfstore.client.changetracking.ESCommandStack;
import org.eclipse.emf.emfstore.internal.client.model.util.AbstractEMFStoreCommand;

/**
 * Basic Command Stack for EMFStore. Allows tracking of command start and end.
 * 
 * @author koegel
 */
public class EMFStoreBasicCommandStack extends BasicCommandStack implements ESCommandStack {

	private final EMFStoreCommandNotifier notifier;
	private Command currentCommand;

	/**
	 * Default constructor.
	 */
	public EMFStoreBasicCommandStack() {
		super();
		notifier = new EMFStoreCommandNotifier();
	}

	@Override
	protected void handleError(Exception exception) {
		notifier.notifiyListenersAboutCommandFailed(currentCommand, exception);
		currentCommand = null;
	}

	@Override
	public void undo() {
		if (canUndo()) {
			notifier.notifiyListenersAboutStart(mostRecentCommand);
			super.undo();
			rethrowComamndInCaseOfError(mostRecentCommand);
			notifier.notifiyListenersAboutCommandCompleted(mostRecentCommand);
		}
	}

	@Override
	public void redo() {
		if (canRedo()) {
			notifier.notifiyListenersAboutStart(mostRecentCommand);
			super.redo();
			rethrowComamndInCaseOfError(mostRecentCommand);
			notifier.notifiyListenersAboutCommandCompleted(mostRecentCommand);
		}
	}

	@Override
	public void execute(Command command) {

		if (currentCommand == null) {
			currentCommand = command;
			notifier.notifiyListenersAboutStart(command);
		}

		super.execute(command);

		rethrowComamndInCaseOfError(command);

		if (currentCommand == command) {
			// check again if command was really completed.
			if (mostRecentCommand == command) {
				notifier.notifiyListenersAboutCommandCompleted(command);
			}
			currentCommand = null;
		}
	}

	private void rethrowComamndInCaseOfError(Command command) {
		// handle EMFStore commands
		if (command instanceof AbstractEMFStoreCommand) {
			final AbstractEMFStoreCommand emfStoreCmd = (AbstractEMFStoreCommand) command;

			// rethrow runtime exceptions if neccessary
			if (!emfStoreCmd.shouldIgnoreExceptions() && emfStoreCmd.getRuntimeException() != null) {
				throw emfStoreCmd.getRuntimeException();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.changetracking.ESCommandStack#addCommandStackObserver(org.eclipse.emf.emfstore.client.changetracking.ESCommandObserver)
	 */
	public void addCommandStackObserver(ESCommandObserver observer) {
		notifier.addCommandStackObserver(observer);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.changetracking.ESCommandStack#removeCommandStackObserver(org.eclipse.emf.emfstore.client.changetracking.ESCommandObserver)
	 */
	public void removeCommandStackObserver(ESCommandObserver observer) {
		notifier.removeCommandStackObserver(observer);
	}

}
