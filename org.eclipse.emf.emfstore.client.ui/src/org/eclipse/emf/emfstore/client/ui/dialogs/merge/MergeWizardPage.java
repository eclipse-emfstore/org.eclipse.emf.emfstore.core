/*******************************************************************************
 * Copyright 2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.ui.dialogs.merge;

import java.util.ArrayList;

import org.eclipse.emf.emfstore.client.model.changeTracking.merging.DecisionManager;
import org.eclipse.emf.emfstore.client.model.changeTracking.merging.conflict.Conflict;
import org.eclipse.emf.emfstore.client.model.changeTracking.merging.conflict.ConflictOption;
import org.eclipse.emf.emfstore.client.model.changeTracking.merging.conflict.ConflictOption.OptionType;
import org.eclipse.emf.emfstore.client.ui.dialogs.merge.ui.DecisionBox;
import org.eclipse.emf.emfstore.client.ui.dialogs.merge.util.UIDecisionConfig;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * Represents the main page of the merge wizard.
 * 
 * @author wesendon
 */
public class MergeWizardPage extends WizardPage {

	/**
	 * Name of wizard page.
	 */
	public static final String PAGE_NAME = "Resolve Conflicts";

	private ArrayList<DecisionBox> decisionBoxes;
	private DecisionManager decisionManager;

	/**
	 * Default Constructor.
	 * 
	 * @param decisionManager
	 *            a decisionManager
	 */
	protected MergeWizardPage(DecisionManager decisionManager) {
		super(PAGE_NAME);
		this.decisionManager = decisionManager;
		setTitle("Merge Conflicts");
		int countMyOperations = decisionManager.countMyOperations();
		int countTheirOperations = decisionManager.countTheirOperations();
		int countMyLeafOperations = decisionManager.countMyOperations();
		int countTheirLeafOperations = decisionManager.countTheirOperations();
		setDescription("Some of your " + countMyOperations + " composite changes and " + countMyLeafOperations
			+ " overall changes conflict with " + countTheirOperations + " composite changes and "
			+ countTheirLeafOperations + " overall changes from the repository."
			+ "\nIn order to resolve these issues, select an option for every conflict.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void createControl(final Composite parent) {
		parent.setLayout(new GridLayout());

		Composite topBar = createTopBar(parent);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(topBar);
		final ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(true, true).applyTo(scrolledComposite);

		final Composite client = new Composite(scrolledComposite, SWT.NONE);
		client.setLayout(new GridLayout());
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		client.setLayoutData(gridData);

		ColorSwitcher colorSwitcher = new ColorSwitcher();

		decisionBoxes = new ArrayList<DecisionBox>();
		for (Conflict conflict : decisionManager.getConflicts()) {
			decisionBoxes.add(new DecisionBox(client, decisionManager, colorSwitcher.getColor(), conflict));
		}

		scrolledComposite.setContent(client);

		Point computeSize = calcParentSize(parent);
		scrolledComposite.setMinSize(computeSize);
		setControl(scrolledComposite);
	}

	private Point calcParentSize(final Composite parent) {
		Point computeSize = parent.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		computeSize.x = parent.getBounds().width;
		// Due to resizing issues give a bit of extra space.
		computeSize.y = (computeSize.y + 50);
		return computeSize;
	}

	private Composite createTopBar(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new RowLayout(SWT.HORIZONTAL));
		composite.setSize(SWT.DEFAULT, 200);

		Button accecptMine = new Button(composite, SWT.PUSH);
		accecptMine.setText("Keep All My Changes");
		accecptMine.addSelectionListener(new SelectAllSelectionListener(OptionType.MyOperation));

		Button accecptTheirs = new Button(composite, SWT.PUSH);
		accecptTheirs.setText("Keep All Their Changes");
		accecptTheirs.addSelectionListener(new SelectAllSelectionListener(OptionType.TheirOperation));

		return composite;
	}

	/**
	 * Listener for select all mine and all their buttons.
	 * 
	 * @author wesendon
	 */
	private final class SelectAllSelectionListener implements SelectionListener {

		private final OptionType type;

		public SelectAllSelectionListener(OptionType type) {
			this.type = type;
		}

		public void widgetSelected(SelectionEvent e) {
			select();
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			select();
		}

		private void select() {
			if (type.equals(OptionType.MyOperation)) {
				boolean confirm = MessageDialog.openConfirm(getShell(), "Override changes of other users",
					"Are you sure you want to override " + decisionManager.countTheirOperations()
						+ " composite operations and " + decisionManager.countTheirLeafOperations()
						+ " overall changes of other users by keeping your changes?");
				if (!confirm) {
					return;
				}
			}
			for (DecisionBox box : decisionBoxes) {
				for (ConflictOption option : box.getConflict().getOptions()) {
					if (option.getType().equals(type)) {
						box.setSolution(option);
						break;
					}
				}
			}
		}
	}

	/**
	 * Small class which switches colors from row to row.
	 * 
	 * @author wesendon
	 */
	private final class ColorSwitcher {
		private boolean color;

		public ColorSwitcher() {
			color = false;
		}

		public Color getColor() {
			color = !color;
			return (color) ? UIDecisionConfig.getFirstDecisionBoxColor() : UIDecisionConfig.getSecondDecisionBoxColor();
		}
	}
}
