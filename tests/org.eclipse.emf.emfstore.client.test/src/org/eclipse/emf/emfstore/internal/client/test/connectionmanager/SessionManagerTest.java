/*******************************************************************************
 * Copyright (c) 2011-2018 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.test.connectionmanager;

import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionElement;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;
import org.eclipse.emf.emfstore.internal.client.model.connectionmanager.SessionManager;
import org.junit.Test;

@SuppressWarnings("restriction")
public class SessionManagerTest {

	@Test
	public void testSessionProviderExtensionPointNoPrios() {
		/* setup */
		final ESExtensionElement noPrio1 = createExtensionElement(null);
		final ESExtensionElement noPrio2 = createExtensionElement(null);
		final ESExtensionPoint testConfiguration = createExtensionPoint(Arrays.asList(
			noPrio1,
			noPrio2));

		/* act */
		final ESExtensionElement result = SessionManager
			.analyseUsersessionExtensionPoint(testConfiguration);

		/* assert */
		assertSame(noPrio1, result);
	}

	@Test
	public void testSessionProviderExtensionPointOnePrio() {
		/* setup */
		final ESExtensionElement noPrio1 = createExtensionElement(null);
		final ESExtensionElement noPrio2 = createExtensionElement(1);
		final ESExtensionPoint testConfiguration = createExtensionPoint(Arrays.asList(
			noPrio1,
			noPrio2));

		/* act */
		final ESExtensionElement result = SessionManager
			.analyseUsersessionExtensionPoint(testConfiguration);

		/* assert */
		assertSame(noPrio2, result);
	}

	@Test
	public void testSessionProviderExtensionPointSamePrios() {
		/* setup */
		final ESExtensionElement noPrio1 = createExtensionElement(1);
		final ESExtensionElement noPrio2 = createExtensionElement(1);
		final ESExtensionPoint testConfiguration = createExtensionPoint(Arrays.asList(
			noPrio1,
			noPrio2));

		/* act */
		final ESExtensionElement result = SessionManager
			.analyseUsersessionExtensionPoint(testConfiguration);

		/* assert */
		assertSame(noPrio1, result);
	}

	@Test
	public void testSessionProviderExtensionPointDifferentPrios() {
		/* setup */
		final ESExtensionElement noPrio1 = createExtensionElement(1);
		final ESExtensionElement noPrio2 = createExtensionElement(2);
		final ESExtensionPoint testConfiguration = createExtensionPoint(Arrays.asList(
			noPrio1,
			noPrio2));

		/* act */
		final ESExtensionElement result = SessionManager
			.analyseUsersessionExtensionPoint(testConfiguration);

		/* assert */
		assertSame(noPrio2, result);
	}

	private static ESExtensionPoint createExtensionPoint(final List<ESExtensionElement> elements) {
		return new ESExtensionPoint("") {
			@Override
			public void reload() {
				setElements(elements);
			}
		};
	}

	private static ESExtensionElement createExtensionElement(final Integer integer) {
		return new ESExtensionElement(null) {

			@Override
			public Integer getInteger(String name) {
				return integer;
			}
		};
	}

}
