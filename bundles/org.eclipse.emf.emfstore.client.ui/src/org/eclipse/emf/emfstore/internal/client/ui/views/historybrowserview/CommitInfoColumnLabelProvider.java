/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * aleaum
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.views.historybrowserview;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo;
import org.eclipse.emf.emfstore.server.model.ESLogMessage;
import org.eclipse.jface.viewers.ColumnLabelProvider;

/**
 * @author aleaum
 *
 */
public class CommitInfoColumnLabelProvider extends ColumnLabelProvider {
	@Override
	public String getText(Object element) {
		if (element instanceof HistoryInfo) {
			final HistoryInfo historyInfo = (HistoryInfo) element;
			ESLogMessage logMessage = null;
			final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd, HH:mm"); //$NON-NLS-1$
			final StringBuilder builder = new StringBuilder();
			if (historyInfo.getLogMessage() != null) {
				logMessage = historyInfo.getLogMessage();
			} else if (historyInfo.getChangePackage() != null && historyInfo.getChangePackage().getLogMessage() != null) {
				logMessage = historyInfo.getChangePackage().getLogMessage();
			}
			if (logMessage != null) {
				builder.append(" ["); //$NON-NLS-1$
				builder.append(logMessage.getAuthor());
				final Date clientDate = logMessage.getClientDate();
				if (clientDate != null) {
					builder.append(" @ "); //$NON-NLS-1$
					builder.append(dateFormat.format(clientDate));
				}
				builder.append("] "); //$NON-NLS-1$
			}
			return builder.toString();

		}
		return null;
	}

	@Override
	public String getToolTipText(Object element) {
		return getText(element);
	}
}
