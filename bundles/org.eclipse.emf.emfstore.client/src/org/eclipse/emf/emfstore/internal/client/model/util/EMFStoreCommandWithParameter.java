/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * emueller
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.util;

import org.eclipse.emf.ecore.EObject;

/**
 * Command with a parameter.
 * 
 * @author emueller
 * @param <T> parameter type
 */
public abstract class EMFStoreCommandWithParameter<T> extends AbstractEMFStoreCommand {

	private T parameter;

	@Override
	protected void commandBody() {
		doRun(parameter);
	}

	/**
	 * The actual action that is being executed.
	 * 
	 * @param parameter the parameter of type T
	 */
	protected abstract void doRun(T parameter);

	/**
	 * Executes the command on the editing domain of the given {@link EObject}.
	 * 
	 * @param parameter the parameter
	 * @param element the EObject from which the editing domain is retrieved
	 * @param ignoreExceptions true if any thrown exception in the execution of the command should be ignored.
	 */
	public void run(T parameter, EObject element, boolean ignoreExceptions) {
		this.parameter = parameter;
		super.aRun(element, ignoreExceptions);
	}
}
