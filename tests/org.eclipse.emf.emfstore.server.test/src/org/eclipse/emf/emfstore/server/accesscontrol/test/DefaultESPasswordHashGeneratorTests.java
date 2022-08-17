/*******************************************************************************
 * Copyright (c) 2011-2016 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.accesscontrol.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.emf.emfstore.internal.server.accesscontrol.DefaultESPasswordHashGenerator;
import org.eclipse.emf.emfstore.server.auth.ESPasswordHashGenerator.ESHashAndSalt;
import org.junit.Test;

public class DefaultESPasswordHashGeneratorTests {

	private static final String PASSWORD = "IAmPassword"; //$NON-NLS-1$
	private static final String NOT_PASSWORD = "IAmNotPassword"; //$NON-NLS-1$

	private static final String TEST_HASH = "eaf6acf17676f0b6bd554af88350c905fd0a444ec962f1840e7fabf182c79e881c3589ff66667b1f56e73a84b6585b57ef3d82e914b3584be18d3ad6e9641a9f"; //$NON-NLS-1$
	private static final String TEST_SALT = "nPkctDB1Q45EOv6lyn5DiG14vEzAUeYhNbtKOWutolQayr2idOctVc2xpo6q4p6oUmYU1cG1q2DzQgJ0zHKctIP1SrKzEJzdAqYesRhoXLTLvGbuElGNaYBbd3eb8cRZ"; //$NON-NLS-1$

	/* stateless */
	private final DefaultESPasswordHashGenerator passwordHashGenerator = new DefaultESPasswordHashGenerator();

	@Test
	public void testHashPasswordSamePasswordDifferentSalts() {
		final ESHashAndSalt hash1 = passwordHashGenerator.hashPassword(PASSWORD);
		final ESHashAndSalt hash2 = passwordHashGenerator.hashPassword(PASSWORD);
		assertFalse(hash1.getSalt().equals(hash2.getSalt()));
		assertFalse(hash1.getHash().equals(hash2.getHash()));
	}

	@Test
	public void testVerifyPasswordMatching() {
		assertTrue(passwordHashGenerator.verifyPassword(PASSWORD, TEST_HASH, TEST_SALT));
	}

	@Test
	public void testVerifyPasswordNotMatching() {
		assertFalse(passwordHashGenerator.verifyPassword(NOT_PASSWORD, TEST_HASH, TEST_SALT));
	}

	@Test
	public void testVerifyPasswordInvalidInput() {
		assertFalse(passwordHashGenerator.verifyPassword(null, TEST_HASH, TEST_SALT));
		assertFalse(passwordHashGenerator.verifyPassword(PASSWORD, null, TEST_SALT));
		assertFalse(passwordHashGenerator.verifyPassword(PASSWORD, TEST_HASH, null));
	}

	@Test
	public void testGenerateSalt() {
		final String salt = passwordHashGenerator.generateSalt();
		assertEquals(128, salt.length());
	}

	@Test
	public void testGenerateSaltWthCustomLength() {
		final String salt = passwordHashGenerator.generateSalt(100);
		assertEquals(100, salt.length());
	}

}
