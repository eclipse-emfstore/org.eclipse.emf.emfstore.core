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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
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

	private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1"; //$NON-NLS-1$

	private static final int PBKDF2_ITERATION_COUNT = 65536;

	private static final char[] ALPHANUMERIC_CHARS = ALPHANUMERIC.toCharArray();

	private static final int KEY_LENGTH = 128;

	private final boolean useLegacyHashFunction;

	private final SecretKeyFactory secretKeyFactory;

	public DefaultESPasswordHashGenerator(boolean useLegacyHashFunction) throws NoSuchAlgorithmException {
		if (!useLegacyHashFunction) {
			secretKeyFactory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
		} else {
			secretKeyFactory = null;
		}
		this.useLegacyHashFunction = useLegacyHashFunction;
	}

	public DefaultESPasswordHashGenerator() throws NoSuchAlgorithmException {
		this(true);
	}

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
		if (useLegacyHashFunction) {
			return iteratedSHA512(password, salt);
		}
		return pBKDF2(password, salt);
	}

	protected final String pBKDF2(String password, String salt) {
		try {
			final KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), //
				PBKDF2_ITERATION_COUNT, KEY_LENGTH);
			final byte[] encoded = secretKeyFactory.generateSecret(spec).getEncoded();
			final byte[] base64EncodedByteAr = Base64.encodeBase64(encoded);
			return new String(base64EncodedByteAr);
		} catch (final InvalidKeySpecException ex) {
			/* we expect a valid key spec since we create it above */
			ModelUtil.logException(ex);
			return null;
		}
	}

	@Deprecated
	protected final String iteratedSHA512(String password, final String salt) {
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

	/**
	 * @return whether the legacy hash function should be used
	 */
	protected boolean useLegacyHashFunction() {
		return useLegacyHashFunction;
	}

}
