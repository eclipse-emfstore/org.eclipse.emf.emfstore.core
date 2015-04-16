/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Slawomir Chodnicki - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.changeTracking.notification.filter;

import org.eclipse.emf.emfstore.client.handler.ESNotificationFilter;
import org.eclipse.emf.emfstore.common.model.ESObjectContainer;
import org.eclipse.emf.emfstore.common.model.util.ESNotificationInfo;

/**
 * Filters notifications for transient features, as these have no effect on the persisted model state.
 *
 * @author chodnick
 */
public class TransientFilter implements ESNotificationFilter {

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.handler.ESNotificationFilter#check(org.eclipse.emf.emfstore.common.model.util.ESNotificationInfo,
	 *      org.eclipse.emf.emfstore.common.model.ESObjectContainer)
	 */
	public boolean check(ESNotificationInfo notificationInfo, ESObjectContainer<?> container) {
		return notificationInfo.isTransient();
	}

}
