package org.eclipse.emf.emfstore.client.ui.commands.controller;

import org.eclipse.emf.emfstore.client.model.ServerInfo;
import org.eclipse.emf.emfstore.client.model.connectionmanager.SessionProvider;
import org.eclipse.emf.emfstore.client.ui.commands.handlers.AbstractEMFStoreUIController;
import org.eclipse.emf.emfstore.client.ui.dialogs.login.LoginDialog;
import org.eclipse.emf.emfstore.client.ui.dialogs.login.LoginDialogController;
import org.eclipse.emf.emfstore.common.ExtensionPoint;
import org.eclipse.emf.emfstore.server.exceptions.EmfStoreException;
import org.eclipse.swt.widgets.Shell;

public class UIServerLoginController extends AbstractEMFStoreUIController {

	private final ServerInfo serverInfo;

	public UIServerLoginController(Shell shell, ServerInfo serverInfo) {
		super(shell);
		this.serverInfo = serverInfo;
	}

	public void openLoginDialog() {
		LoginDialog loginDialog;
		try {
			loginDialog = new LoginDialog(shell, new LoginDialogController(new ExtensionPoint(SessionProvider.ID)
				.getClass("class", SessionProvider.class).provideUsersession(serverInfo)));
			loginDialog.open();
		} catch (EmfStoreException e) {
			// TODO
			e.printStackTrace();
		}

	}
}
