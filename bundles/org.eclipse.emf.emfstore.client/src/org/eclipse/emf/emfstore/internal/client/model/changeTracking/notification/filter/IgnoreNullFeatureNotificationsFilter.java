/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Maximilian Koegel, Otto von Wesendonk - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.changeTracking.notification.filter;

import org.eclipse.emf.emfstore.client.handler.ESNotificationFilter;
import org.eclipse.emf.emfstore.common.model.ESObjectContainer;
import org.eclipse.emf.emfstore.common.model.util.ESNotificationInfo;

/**
 * Filter to ignore notifications with the illegal state of having no value for
 * the feature member.
 *
 * @author mkoegel
 * @author ovonwesen
 */
public class IgnoreNullFeatureNotificationsFilter implements ESNotificationFilter {

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.handler.ESNotificationFilter#check(org.eclipse.emf.emfstore.common.model.util.ESNotificationInfo,
	 *      org.eclipse.emf.emfstore.common.model.ESObjectContainer)
	 */
	public boolean check(ESNotificationInfo notificationInfo, ESObjectContainer<?> container) {
		return notificationInfo.getFeature() == null;
	}

}