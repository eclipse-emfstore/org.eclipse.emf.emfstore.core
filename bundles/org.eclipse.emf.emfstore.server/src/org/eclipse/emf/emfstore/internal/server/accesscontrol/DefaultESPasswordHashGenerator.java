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
package org.eclipse.emf.emfstore.internal.server.accesscontrol;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.eclipse.emf.emfstore.server.auth.ESPasswordHashGenerator;

/**
 * Default implemention of the {@link ESPasswordHashGenerator} using a 128 char String as a salt and SHA512 as a hash
 * function.
 *
 * @author Johannes Faltermeier
 *
 */
public class DefaultESPasswordHashGenerator implements ESPasswordHashGenerator {

	/**
	 * Default length of the generated salt.
	 */
	protected static final int SALT_PREFIX_LENGTH = 128;

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESPasswordHashGenerator#hashPassword(java.lang.String)
	 */
	public ESHashAndSalt hashPassword(String password) {
		final String salt = generateSalt();
		final String hash = createHash(password, salt);
		return org.eclipse.emf.emfstore.server.auth.ESHashAndSalt.create(hash, salt);
	}

	/**
	 * @return the generated salt
	 */
	protected String generateSalt() {
		return RandomStringUtils.randomAlphanumeric(SALT_PREFIX_LENGTH);
	}

	private String createHash(String password, final String salt) {
		String hash = DigestUtils.sha512Hex(password + salt);
		for (int i = 0; i < 128; i++) {
			if (i % 2 == 0) {
				hash = DigestUtils.sha512Hex(hash + salt);
			} else {
				hash = DigestUtils.sha512Hex(salt + hash);
			}
		}
		return hash;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESPasswordHashGenerator#verifyPassword(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public boolean verifyPassword(String password, String hash, String salt) {
		if (password == null || hash == null || salt == null) {
			return false;
		}
		final String hashToMatch = createHash(password, salt);
		return hash.equals(hashToMatch);
	}

}
