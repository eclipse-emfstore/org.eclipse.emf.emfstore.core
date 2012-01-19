package org.eclipse.emf.emfstore.client.ui.handlers;

import org.eclipse.emf.emfstore.client.model.util.EMFStoreCommandWithResult;

public abstract class EMFStoreUICommandWithResult<T> extends EMFStoreCommandWithResult<T> {

	@Override
	protected void commandBody() {
		try {
			super.commandBody();
		} catch (RequiredSelectionException e) {
			// TODO Handle Exception
		}
	}
}
