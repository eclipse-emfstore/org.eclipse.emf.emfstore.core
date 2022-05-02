/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.action;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;
import org.eclipse.emf.emfstore.internal.client.model.AdminBroker;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.PropertiesForm;
import org.eclipse.emf.emfstore.internal.client.ui.util.PasswordHelper;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Action for creating a user.
 *
 * @author emueller
 *
 */
public class CreateUserAction extends CreateOrgUnitAction {

	private static final String USER_FIELD_NAME = Messages.CreateUserAction_UserName_Field;
	private static final String PW_FIELD_NAME = Messages.CreateUserAction_Password_Field;
	private Boolean passwordControlsEnabled;

	/**
	 * Creates the create user action.
	 *
	 * @param adminBroker
	 *            the {@link AdminBroker} that actually creates the user
	 * @param tableViewer
	 *            the {@link TableViewer} containing all the users
	 * @param form
	 *            the {@link PropertiesForm} containing user details
	 */
	public CreateUserAction(AdminBroker adminBroker, TableViewer tableViewer, PropertiesForm form) {
		super(Messages.CreateUserAction_ActionTitle, adminBroker, tableViewer, form);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.action.CreateOrgUnitAction#getInputFieldNames()
	 */
	@Override
	protected Set<String> getInputFieldNames() {
		final Set<String> fieldNames = new LinkedHashSet<String>();
		fieldNames.add(USER_FIELD_NAME);

		if (passwordControlsEnabled()) {
			fieldNames.add(PW_FIELD_NAME);
		}

		return fieldNames;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.action.CreateOrgUnitAction#getPrimaryFieldName()
	 */
	@Override
	protected String getPrimaryFieldName() {
		return USER_FIELD_NAME;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.action.CreateOrgUnitAction#orgUnitName()
	 */
	@Override
	protected String orgUnitName() {
		return Messages.CreateUserAction_OrgUnitName;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.action.CreateOrgUnitAction#createOrgUnit(java.lang.String)
	 */
	@Override
	protected ACOrgUnitId createOrgUnit(Map<String, String> fieldValues) throws ESException {
		final String userName = fieldValues.get(USER_FIELD_NAME);
		final String pwd = fieldValues.get(PW_FIELD_NAME);

		if (StringUtils.isBlank(userName)) {
			throw new ESException(Messages.CreateUserAction_UserName_Empty);
		} else if (passwordControlsEnabled() && StringUtils.isBlank(pwd)) {
			throw new ESException(Messages.CreateUserAction_Password_Empty);
		}

		final ACOrgUnitId userId = getAdminBroker().createUser(userName);

		if (passwordControlsEnabled()) {
			getAdminBroker().changeUser(userId, userName, pwd);
		}

		return userId;
	}

	@Override
	protected boolean validateFieldValues(Map<String, String> fieldValues) {
		final String pwd = fieldValues.get(PW_FIELD_NAME);
		if (passwordControlsEnabled() && !PasswordHelper.INSTANCE.matchesPattern(pwd)) {
			final Shell shell = Display.getCurrent().getActiveShell();
			MessageDialog
				.openWarning(shell,
					Messages.CreateUserAction_InvalidPasswordTitle,
					PasswordHelper.INSTANCE.getInvalidPatternMessage());
			return false;
		}
		return super.validateFieldValues(fieldValues);
	}

	private boolean passwordControlsEnabled() {

		if (passwordControlsEnabled == null) {
			final ESExtensionPoint showPasswordControls = new ESExtensionPoint(
				"org.eclipse.emf.emfstore.client.ui.showPasswordControls"); //$NON-NLS-1$

			passwordControlsEnabled = showPasswordControls.getBoolean("enabled", false); //$NON-NLS-1$
		}

		return passwordControlsEnabled;
	}
}
