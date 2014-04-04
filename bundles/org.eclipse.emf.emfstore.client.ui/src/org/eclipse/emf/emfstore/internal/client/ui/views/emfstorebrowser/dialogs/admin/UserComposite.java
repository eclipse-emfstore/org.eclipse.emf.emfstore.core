/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Hodaie
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.views.emfstorebrowser.dialogs.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.internal.client.model.AdminBroker;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.EMFStoreMessageDialog;
import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;

/**
 * This shows attributes of a ACUser (name, description) and show a list of groups this user belongs to. You can use
 * drag and drop to drop a group on list of user's groups to add user to that group.
 * 
 * @author Hodaie
 */
public class UserComposite extends PropertiesComposite {

	private ACUser user;
	private final OrgUnitManagementGUI orgUnitMgmtGUI;

	/**
	 * Constructor.
	 * 
	 * @param parent parent
	 * @param style style
	 * @param adminBroker used to communicate with server
	 * @param orgUnitMgmtGUI used to find out what which tab is active, so that if needed update its list viewer
	 */
	public UserComposite(Composite parent, int style, AdminBroker adminBroker, OrgUnitManagementGUI orgUnitMgmtGUI) {
		super(parent, style, adminBroker);
		this.orgUnitMgmtGUI = orgUnitMgmtGUI;
		createControls();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void removeOrgUnit(ACOrgUnit group) {
		try {
			getAdminBroker().removeGroup(user.getId(), ((ACGroup) group).getId());
		} catch (final AccessControlException ex) {
			MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				Messages.UserComposite_Insufficient_Access_Rights,
				Messages.UserComposite_Not_Allowed_To_Remove_Member_From_Group);
		} catch (final ESException ex) {
			EMFStoreMessageDialog.showExceptionDialog(ex);
		}
		getTableViewer().refresh();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addExistingOrgUnit(ACOrgUnit group) {
		try {
			if (group != null) {
				getAdminBroker().addMember(((ACGroup) group).getId(), user.getId());
			}
		} catch (final AccessControlException ex) {
			MessageDialog.openWarning(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				Messages.UserComposite_Insufficient_Access_Rights,
				Messages.UserComposite_Not_Allowed_To_Add_Member_To_Group);
		} catch (final ESException ex) {
			EMFStoreMessageDialog.showExceptionDialog(ex);
		}
		getTableViewer().refresh();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addNewOrgUnit() {
		try {
			final List<ACGroup> groups = getGroups();
			for (final ACGroup newGroup : groups) {
				getAdminBroker().addMember(newGroup.getId(), user.getId());
			}
		} catch (final AccessControlException ex) {
			MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				Messages.UserComposite_Insufficient_Access_Rights,
				Messages.UserComposite_Not_Allowed_To_Add_Member_To_Group);
		} catch (final ESException ex) {
			EMFStoreMessageDialog.showExceptionDialog(ex);
		}
		getTableViewer().refresh();
	}

	/**
	 * This is used when adding a group using add button. It shows an element selection dialog.
	 * 
	 * @return a list of selected groups to which this ACUser will be added.
	 */
	private List<ACGroup> getGroups() {

		final List<ACOrgUnit> allGroups = new ArrayList<ACOrgUnit>();
		final List<ACGroup> groups = new ArrayList<ACGroup>();

		try {
			allGroups.addAll(getAdminBroker().getGroups());
			final List<ACGroup> groupsToRemove = new ArrayList<ACGroup>();
			groupsToRemove.addAll(getAdminBroker().getGroups(user.getId()));

			allGroups.removeAll(groupsToRemove);

			final Object[] result = showDialog(allGroups, Messages.UserComposite_Select_Group);

			for (int i = 0; i < result.length; i++) {
				if (result[i] instanceof ACGroup) {
					groups.add((ACGroup) result[i]);
				}
			}
		} catch (final AccessControlException ex) {
			MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				Messages.UserComposite_Insufficient_Access_Rights,
				Messages.UserComposite_Not_Allowed_To_List_Groups);
		} catch (final ESException ex) {
			EMFStoreMessageDialog.showExceptionDialog(ex);
		}
		return groups;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.ui.views.emfstorebrowser.dialogs.admin.PropertiesComposite#getTabTitle()
	 */
	@Override
	protected String getTabTitle() {
		return Messages.UserComposite_Groups;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateControls(EObject input) {
		if (input != null && input instanceof ACUser) {

			user = (ACUser) input;

			getTxtName().setText(user.getName());
			getTxtDescription().setText(user.getDescription() == null ? StringUtils.EMPTY : user.getDescription());
			getTableViewer().setInput(user);

			// disable the text fields for editing name and description when displaying the super user
			final String superUserName = ServerConfiguration.getProperties().getProperty(
				ServerConfiguration.SUPER_USER,
				ServerConfiguration.SUPER_USER_DEFAULT);

			if (user.getName().equals(superUserName)) {
				getTxtName().setEnabled(false);
				getTxtDescription().setEnabled(false);
			} else {
				getTxtName().setEnabled(true);
				getTxtDescription().setEnabled(true);
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addDragNDropSupport() {
		// add drag support
		super.addDragNDropSupport();

		// add drop support
		final int ops = DND.DROP_COPY;
		final Transfer[] transfers = new Transfer[] { LocalSelectionTransfer.getTransfer() };
		final DropTargetListener dropListener = new DropTargetAdapter() {
			@Override
			public void dragEnter(DropTargetEvent event) {
				if (PropertiesForm.getDragSource().equals(Messages.UserComposite_Projects)
					|| PropertiesForm.getDragSource().equals(Messages.UserComposite_Users)) {
					event.detail = DND.DROP_NONE;
				} else {
					event.detail = DND.DROP_COPY;
				}
			}

			@Override
			public void drop(DropTargetEvent event) {
				final List<ACOrgUnit> orgUnits = PropertiesForm.getDragNDropObjects();
				for (final ACOrgUnit orgUnit : orgUnits) {
					addExistingOrgUnit(orgUnit);
				}
				PropertiesForm.setDragNDropObjects(Collections.<ACOrgUnit> emptyList());
				getTableViewer().refresh();
			}
		};
		getTableViewer().addDropSupport(ops, transfers, dropListener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveOrgUnitAttributes() {
		if (getTxtName() == null || getTxtDescription() == null) {
			return;
		}
		if (user == null) {
			return;
		}

		final String superUserName = ServerConfiguration.getProperties().getProperty(ServerConfiguration.SUPER_USER,
			ServerConfiguration.SUPER_USER_DEFAULT);
		if (getTxtName().getText().compareToIgnoreCase(superUserName) == 0) {
			getTxtName().setText(Messages.UserComposite_New_User);
			MessageDialog.openInformation(getShell(), Messages.UserComposite_Illegal_Username,
				Messages.UserComposite_Username_Super_Not_Allowed);
			return;
		}
		if (getTxtName().getText().equals(StringUtils.EMPTY)) {
			getTxtName().setText(Messages.UserComposite_New_User);
			MessageDialog.openInformation(getShell(), Messages.UserComposite_Empty_Username,
				Messages.UserComposite_Empty_Username_Not_Allowed);
			return;
		}

		if (!(user.getName().equals(getTxtName().getText()) && user.getDescription().equals(
			getTxtDescription().getText()))) {
			try {
				getAdminBroker().changeOrgUnit(user.getId(), getTxtName().getText(), getTxtDescription().getText());
				((Form) getParent().getParent()).setText(Messages.UserComposite_User + getTxtName().getText());
				orgUnitMgmtGUI.getActiveTabContent().getTableViewer().refresh();

			} catch (final ESException e) {
				EMFStoreMessageDialog.showExceptionDialog(e);
			}
		}
	}

}
