/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Gürcan Karakoc, Michael Deser - initial API and implementation
 * Maximilian Koegel - added delete user action
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;
import org.eclipse.emf.emfstore.internal.client.model.AdminBroker;
import org.eclipse.emf.emfstore.internal.client.ui.Activator;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.EMFStoreMessageDialog;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.acimport.wizard.AcUserImportAction;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.action.CreateUserAction;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.action.DeleteUserAction;
import org.eclipse.emf.emfstore.internal.client.ui.util.PasswordHelper;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * User tab content in the manage users dialog.
 *
 * @author gurcankarakoc, deser
 */
public class UserTabContent extends TabContent implements IPropertyChangeListener {

	private static final String DELETE_ICON = "icons/delete.gif"; //$NON-NLS-1$
	private static final String KEY_ICON = "icons/key.png"; //$NON-NLS-1$
	private static final String USER_ICON = "icons/user.png"; //$NON-NLS-1$

	/**
	 * Action to change the password of a user.
	 */
	private final class ChangePasswordAction extends Action {

		private ChangePasswordAction() {
			super(Messages.UserTabContent_Change_Password);
		}

		/**
		 * {@inheritDoc}
		 *
		 * @see org.eclipse.jface.action.Action#getImageDescriptor()
		 */
		@Override
		public ImageDescriptor getImageDescriptor() {
			return Activator.getImageDescriptor(KEY_ICON);
		}

		@Override
		public void run() {
			final IStructuredSelection selection = (IStructuredSelection) getTableViewer().getSelection();
			final Iterator<?> iterator = selection.iterator();

			while (iterator.hasNext()) {

				final ACUser user = (ACUser) iterator.next();

				if (user == null) {
					return;
				}

				final Display display = Display.getCurrent();
				final Shell activeShell = display.getActiveShell();
				final InputDialog inputDialog = new InputDialog(
					activeShell,
					Messages.UserTabContent_Enter_New_Password_For_User
						+ " '" + user.getName() + "'", //$NON-NLS-1$//$NON-NLS-2$
					Messages.UserTabContent_Enter_New_Password,
					StringUtils.EMPTY, null);

				if (inputDialog.open() == Window.OK) {
					final String newPassword = inputDialog.getValue();
					if (!PasswordHelper.INSTANCE.matchesPattern(newPassword)) {
						MessageDialog
							.openWarning(activeShell,
								Messages.UserTabContent_InvalidPassword,
								PasswordHelper.INSTANCE.getInvalidPatternMessage());
						continue;
					}
					try {
						getAdminBroker().changeUser(user.getId(), user.getName(), newPassword);
					} catch (final AccessControlException ex) {
						MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							Messages.UserTabContent_Insufficient_Access_Rights,
							Messages.UserTabContent_Not_Allowed_To_Change_Password);
					} catch (final ESException ex) {
						EMFStoreMessageDialog.showExceptionDialog(ex);
					}
				}
			}
			getTableViewer().refresh();
		}
	}

	/**
	 * @param string the name of tab.
	 * @param adminBroker AdminBroker is needed to communicate with server.
	 * @param frm used to set input to properties form and update its table viewer upon. deletion of OrgUnits.
	 */
	public UserTabContent(String string, AdminBroker adminBroker, PropertiesForm frm) {
		super(string, adminBroker, frm);
		setTab(this);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.TabContent#initActions()
	 */
	@Override
	protected List<Action> initActions() {
		final Action createNewUserAction = new CreateUserAction(getAdminBroker(), getTableViewer(), getForm());
		createNewUserAction.setImageDescriptor(Activator.getImageDescriptor(USER_ICON));
		createNewUserAction.setToolTipText(Messages.UserTabContent_Create_New_User);

		final Action deleteUserAction = new DeleteUserAction(getAdminBroker(), getTableViewer(), getForm());
		deleteUserAction.setImageDescriptor(Activator.getImageDescriptor(DELETE_ICON));
		deleteUserAction.setToolTipText(Messages.UserTabContent_Delete_User);

		final Action importOrgUnitAction = new AcUserImportAction(getAdminBroker());
		importOrgUnitAction.addPropertyChangeListener(this);

		final Action changePassword = new ChangePasswordAction();
		changePassword.setToolTipText(Messages.UserTabContent_Change_Password);

		final List<Action> actions = new ArrayList<Action>();
		actions.add(createNewUserAction);
		actions.add(deleteUserAction);
		actions.add(importOrgUnitAction);

		final ESExtensionPoint showPasswordControls = new ESExtensionPoint(
			"org.eclipse.emf.emfstore.client.ui.showPasswordControls"); //$NON-NLS-1$

		if (showPasswordControls.getBoolean("enabled", false)) { //$NON-NLS-1$
			actions.add(changePassword);
		}

		return actions;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ITableLabelProvider getLabelProvider() {
		return new ITableLabelProvider() {

			public void addListener(ILabelProviderListener listener) {
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
			}

			public void dispose() {
			}

			public Image getColumnImage(Object element, int columnIndex) {
				return Activator.getImageDescriptor(USER_ICON).createImage();
			}

			public String getColumnText(Object element, int columnIndex) {
				return ((ACUser) element).getName();
			}

		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStructuredContentProvider getContentProvider() {
		return new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				// return a list of Users in project space
				final List<ACUser> users = new ArrayList<ACUser>();
				try {
					users.addAll(getAdminBroker().getUsers());
				} catch (final AccessControlException ex) {
					MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						Messages.UserTabContent_Insufficient_Access_Rights,
						Messages.UserTabContent_Not_Allowed_To_List_Users);
				} catch (final ESException ex) {
					EMFStoreMessageDialog.showExceptionDialog(ex);
				}
				return users.toArray(new ACUser[users.size()]);
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
		};
	}

	/**
	 * Refresh the tableViewer after a property change. (Used e.g. after importing users via e.g. CSV.)
	 *
	 * @param event The event to deal with.
	 */
	public void propertyChange(PropertyChangeEvent event) {
		getTableViewer().refresh();
	}

}
