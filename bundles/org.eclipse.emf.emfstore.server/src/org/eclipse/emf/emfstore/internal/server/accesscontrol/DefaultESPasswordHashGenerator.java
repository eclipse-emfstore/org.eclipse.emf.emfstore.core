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

import java.security.SecureRandom;

import org.apache.commons.codec.digest.DigestUtils;
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
	 * {@link SecureRandom} instance used by the generator.
	 */
	protected static final SecureRandom SECURERANDOM = new SecureRandom();

	/**
	 * Default length of the generated salt.
	 */
	protected static final int SALT_PREFIX_LENGTH = 128;

	/** Alphanumeric characters. */
	protected static final String ALPHANUMERIC = "1234567890" + //$NON-NLS-1$
		"ABCDEFGHIJKLMNOPQRSTUVWXYZ" + //$NON-NLS-1$
		"abcdefghijklmnopqrstuvwxyz"; //$NON-NLS-1$

	private static final char[] ALPHANUMERIC_CHARS = ALPHANUMERIC.toCharArray();

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
	public String generateSalt() {
		return generateSalt(SALT_PREFIX_LENGTH);
	}

	/**
	 * @param length the length of the generated salt
	 * @return the generated salt
	 */
	public String generateSalt(int length) {
		final char[] randomChars = new char[length];
		for (int i = 0; i < length; i++) {
			randomChars[i] = ALPHANUMERIC_CHARS[SECURERANDOM.nextInt(ALPHANUMERIC_CHARS.length)];
		}
		return new String(randomChars);
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
