/*******************************************************************************
 * Copyright (c) 2008-2015 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto von Wesendonk - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.accesscontrol.authentication.verifiers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.authentication.verifiers.SimplePropertyFileUserVerifier.Hash;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.exceptions.FatalESException;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider;
import org.eclipse.emf.emfstore.server.model.ESSessionId;
import org.eclipse.emf.emfstore.server.model.ESUser;

/**
 * This verifier can be used to store user and passwords in a property file. Entries in the property file look should
 * look like this: <b>user = password</b>
 *
 * @author wesendonk
 */
public class SimplePropertyFileUserVerifier extends UserVerifier {

	private final Properties passwordFile;

	private final Hash hash;

	private final String filePath;

	/**
	 * Hash algorithms supported by SPFV verifier.
	 *
	 * @author wesendon
	 */
	public enum Hash {
		/**
		 * NONE - no hash, MD5 - md5 hash, SHA1 - sha1 hash.
		 */
		NONE, MD5, SHA1
	}

	/**
	 * Default constructor. No hash will be used for passwords
	 *
	 * @see #SimplePropertyFileUserVerifier(String, Hash)
	 * @param filePath path to password file
	 * @throws FatalESException in case of failure
	 */
	public SimplePropertyFileUserVerifier(ESOrgUnitProvider orgUnitProvider, String filePath) throws FatalESException {
		this(orgUnitProvider, filePath, Hash.NONE);
	}

	/**
	 * Constructor with ability to select hash algorithm for password.
	 *
	 * @param filePath path to file
	 * @param hash selected hash
	 * @throws FatalESException if hash is null
	 */
	public SimplePropertyFileUserVerifier(ESOrgUnitProvider orgUnitProvider, String filePath, Hash hash)
		throws FatalESException {
		super(orgUnitProvider);
		this.filePath = filePath;
		if (hash == null) {
			throw new FatalESException(Messages.SimplePropertyFileVerifier_HashMayNotBeNull);
		}
		this.hash = hash;

		passwordFile = new Properties();
		loadPasswordFile(filePath);
	}

	private void loadPasswordFile(String filePath) {
		final File propertyFile = new File(filePath);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(propertyFile);
			passwordFile.load(fis);
		} catch (final IOException e) {
			ModelUtil.logInfo(Messages.SimplePropertyFileVerifier_CouldNotLoadPasswordFile + filePath);
			// Run with empty password file
			// throw new AccessControlException("Couldn't load password file from path: "+filePath);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (final IOException e) {
					ModelUtil.logInfo("Couldn't load password file from path: " + filePath); //$NON-NLS-1$
				}
			}
		}
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.accesscontrol.authentication.verifiers.PasswordVerifier#verifyPassword(org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	protected boolean verifyPassword(String username, String password)
		throws AccessControlException {
		loadPasswordFile(filePath);
		final String expectedPassword = passwordFile.getProperty(username);
		password = hashPassword(password);
		if (expectedPassword == null || !expectedPassword.equals(password)) {
			return false;
		}
		return true;
	}

	private String hashPassword(String password) {

		if (password == null || hash.equals(Hash.NONE)) {
			return password;
		}

		try {
			MessageDigest md = null;
			switch (hash) {
			case SHA1:
				md = MessageDigest.getInstance("SHA-1"); //$NON-NLS-1$
				break;
			case MD5:
				md = MessageDigest.getInstance("MD5"); //$NON-NLS-1$
				break;
			default:
			}
			if (md != null) {
				return new String(md.digest(password.getBytes()));
			}
		} catch (final NoSuchAlgorithmException e) {
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESUserVerifier#resolve(org.eclipse.emf.emfstore.server.model.ESSessionId)
	 */
	public ESUser resolve(ESSessionId api) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESUserVerifier#isValid(org.eclipse.emf.emfstore.server.model.ESSessionId)
	 */
	public boolean isValid(ESSessionId sessionId) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESUserVerifier#getUser(org.eclipse.emf.emfstore.server.model.ESSessionId)
	 */
	public ESUser getUser(ESSessionId sessionId) {
		// TODO Auto-generated method stub
		return null;
	}
}
