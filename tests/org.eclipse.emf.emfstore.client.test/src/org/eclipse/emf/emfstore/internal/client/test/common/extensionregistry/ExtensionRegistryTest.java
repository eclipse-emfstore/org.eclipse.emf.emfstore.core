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
package org.eclipse.emf.emfstore.internal.client.test.common.extensionregistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.emf.emfstore.internal.client.model.Configuration;
import org.eclipse.emf.emfstore.internal.client.model.util.ChecksumErrorHandler;
import org.eclipse.emf.emfstore.internal.common.ExtensionRegistry;
import org.eclipse.emf.emfstore.server.ESLocationProvider;
import org.junit.After;
import org.junit.Test;

public class ExtensionRegistryTest {

	class Bar {
	}

	@After
	public void teardown() {
		ExtensionRegistry.INSTANCE.remove("foo.bar");
	}

	@Test
	public void testGetDefaultErrorChecksumHandler() {
		assertEquals(ChecksumErrorHandler.CANCEL, Configuration.getClientBehavior().getChecksumErrorHandler());
	}

	@Test
	public void testGetType() {
		final Bar bar = ExtensionRegistry.INSTANCE.get("foo.bar", Bar.class);
		assertEquals(null, bar);
	}

	@Test
	public void testGetTypeWithDefault() {
		final Bar bar = new Bar();
		final Bar defaultBar = ExtensionRegistry.INSTANCE.get("foo.bar", Bar.class, bar, false);
		assertEquals(bar, defaultBar);
	}

	@Test
	public void testRegisterType() {
		final Bar bar = new Bar();
		ExtensionRegistry.INSTANCE.set("foo.bar", bar);
		assertEquals(bar, ExtensionRegistry.INSTANCE.get("foo.bar", Bar.class));
	}

	@Test
	public void testGetTypeWithDefaultAndSetItAsDefault() {
		final Bar bar = new Bar();
		final Bar defaultBar = ExtensionRegistry.INSTANCE.get("foo.bar", Bar.class, bar, true);
		assertEquals(bar, defaultBar);
		assertEquals(bar, ExtensionRegistry.INSTANCE.get("foo.bar", Bar.class));
	}

	@Test
	public void testGetExtensionElement() {
		final ESLocationProvider locationProvider = ExtensionRegistry.INSTANCE.get(
			"org.eclipse.emf.emfstore.server.locationProvider.providerClass",
			ESLocationProvider.class);
		assertNotNull(locationProvider);
	}
}
