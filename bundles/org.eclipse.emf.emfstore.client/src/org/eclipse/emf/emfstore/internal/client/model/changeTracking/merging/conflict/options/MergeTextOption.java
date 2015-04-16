/*******************************************************************************
 * Copyright (c) 2008-2014 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto von Wesendonk - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.options;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.ConflictOption;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.util.DecisionUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AttributeOperation;

/**
 * Special option to merge longer texts.
 *
 * @author wesendon
 */
public class MergeTextOption extends ConflictOption {

	private final List<ConflictOption> list;
	private String text;

	/**
	 * Default constructor.
	 */
	public MergeTextOption() {
		super(Messages.MergeTextOption_EditedOrMergedValue, OptionType.MergeText);
		list = new ArrayList<ConflictOption>();
		setDetailProvider(DecisionUtil.WIDGET_MULTILINE_EDITABLE);
	}

	/**
	 * Add options which should be merged. ATM: only "my" and "their" are supported
	 *
	 * @param option other option
	 */
	public void add(ConflictOption option) {
		list.add(option);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFullOptionLabel() {
		String result = StringUtils.EMPTY;
		for (final ConflictOption option : list) {
			result += " " + option.getFullOptionLabel(); //$NON-NLS-1$
		}
		return result;
	}

	/**
	 * Returns "my" text.
	 *
	 * @return text
	 */
	public String getMyText() {
		final ConflictOption option = DecisionUtil.getConflictOptionByType(list, OptionType.MyOperation);
		return option == null ? StringUtils.EMPTY : option.getFullOptionLabel();
	}

	/**
	 * Returns "their" text.
	 *
	 * @return text
	 */
	public String getTheirString() {
		final ConflictOption option = DecisionUtil.getConflictOptionByType(list, OptionType.TheirOperation);
		return option == null ? StringUtils.EMPTY : option.getFullOptionLabel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<AbstractOperation> getOperations() {
		if (text != null) {
			for (final ConflictOption option : list) {
				if (option.getType().equals(OptionType.MyOperation)) {
					if (option.getOperations().size() == 0) {
						continue;
					}
					final AbstractOperation tmp = option.getOperations().iterator().next();
					if (tmp instanceof AttributeOperation) {
						option.getOperations().remove(tmp);
						final AttributeOperation mergedOp = (AttributeOperation) ModelUtil.clone(tmp);
						mergedOp.setIdentifier(EcoreUtil.generateUUID());
						mergedOp.setNewValue(text);
						option.getOperations().add(mergedOp);
						return option.getOperations();
					}
				}
			}
		}

		return super.getOperations();
	}

	/**
	 * Set resulted merged text.
	 *
	 * @param text text
	 */
	public void setMergedText(String text) {
		this.text = text;
	}

	/**
	 * Get merged text.
	 *
	 * @return text
	 */
	public String getMergedText() {
		return text;
	}
}
