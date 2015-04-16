/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.dialogs;

import java.util.List;

import org.eclipse.emf.emfstore.internal.client.ui.controller.Messages;
import org.eclipse.emf.emfstore.internal.server.model.versioning.BranchInfo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog for setting a tag.
 *
 */
public class CreateTagDialog extends BranchSelectionDialog {

	private Field tagNameField;
	private String tagName;

	/**
	 * Constructor.
	 *
	 * @param parentShell parent shell to display the dialog
	 * @param branches list of branches
	 */
	public CreateTagDialog(Shell parentShell, List<BranchInfo> branches) {
		super(parentShell, branches);
	}

	@Override
	protected void addCreationField(Composite container) {

		tagNameField = createField(container,
			Messages.UIAddTagController_TagNameLabel,
			Messages.UIAddTagController_TagNameTextDefault);

	}

	private Field createField(Composite container, String labelText, String defaultText) {
		final Field field = new Field(container);
		field.setLabelText(labelText);
		field.setDefaultText(defaultText);
		field.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		return field;
	}

	@Override
	protected void okPressed() {
		tagName = tagNameField.getText();
		super.okPressed();
	}

	/**
	 * Hook to set header texts.
	 */
	@Override
	protected void setHeaderTexts() {
		getShell().setText("Create Tag");
		setTitle("Create tag for project");
		setMessage("Please specify a tag name and select the branch you want to create a tag for");
	}

	/**
	 * Returns the tag name entered by the user.
	 *
	 * @return the tag name
	 */
	public String getTagName() {
		return tagName;
	}

	/**
	 * Helper class for creating a label and a text next to each other.
	 */
	private class Field extends Composite {

		private final Label label;
		private final Text text;

		public Field(Composite parent) {
			super(parent, SWT.NONE);
			setLayout(new GridLayout(2, false));
			label = new Label(this, SWT.NONE);
			text = new Text(this, SWT.BORDER);
			label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
			text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		}

		public String getText() {
			return text.getText();
		}

		public void setDefaultText(String defaultText) {
			text.setText(defaultText);
		}

		public void setLabelText(String labelText) {
			label.setText(labelText);
		}

	}
}
