/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * wesendon
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.connection;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.KeyManagerFactory;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.eclipse.emf.emfstore.internal.server.exceptions.ServerKeyStoreException;

/**
 * The ServerKeyStoreManager loads the keystore, which is needed for decryption of user passwords and for RMI
 * encryption.
 *
 * @author wesendon
 */
public final class ServerKeyStoreManager {

	private static ServerKeyStoreManager instance;
	private KeyStore keyStore;

	private ServerKeyStoreManager() {
	}

	/**
	 * Returns the instance of the ServerKeyStoreManager.
	 *
	 * @return an instance
	 */
	public static synchronized ServerKeyStoreManager getInstance() {
		if (instance == null) {
			instance = new ServerKeyStoreManager();
		}
		return instance;
	}

	/**
	 * Decrypts a password with the server's password.
	 *
	 * @param password encrypted password
	 * @return decrypted password
	 * @throws ServerKeyStoreException in case of failure
	 */
	public String decrypt(String password) throws ServerKeyStoreException {
		try {
			if (password == null) {
				throw new ServerKeyStoreException("Password is null.");
			}
			final byte[] passwordBytes = Base64.decodeBase64(password.getBytes());
			final Cipher cipher = Cipher.getInstance(ServerConfiguration.KEYSTORE_CIPHER_ALGORITHM_DEFAULT);
			cipher.init(Cipher.DECRYPT_MODE, getDecryptionKey());
			return new String(cipher.doFinal(passwordBytes));
		} catch (final NoSuchAlgorithmException e) {
			throw new ServerKeyStoreException(e);
		} catch (final NoSuchPaddingException e) {
			throw new ServerKeyStoreException(e);
		} catch (final InvalidKeyException e) {
			throw new ServerKeyStoreException(e);
		} catch (final IllegalBlockSizeException e) {
			throw new ServerKeyStoreException(e);
		} catch (final BadPaddingException e) {
			throw new ServerKeyStoreException(e);
		}
	}

	private PrivateKey getDecryptionKey() throws ServerKeyStoreException {
		try {
			return (PrivateKey) getKeyStore().getKey(getKeyStoreAlias(), getKeyStorePassword());
		} catch (final ServerKeyStoreException e) {
			throw new ServerKeyStoreException(e);
		} catch (final KeyStoreException e) {
			throw new ServerKeyStoreException(e);
		} catch (final NoSuchAlgorithmException e) {
			throw new ServerKeyStoreException(e);
		} catch (final UnrecoverableKeyException e) {
			throw new ServerKeyStoreException(e);
		}
	}

	/**
	 * Returns the keystore of the server.
	 *
	 * @return the server key store
	 * @throws ServerKeyStoreException
	 *             in case key store initialization fails
	 */
	public KeyStore getKeyStore() throws ServerKeyStoreException {
		if (keyStore == null) {
			FileInputStream fileInputStream = null;
			try {
				keyStore = KeyStore.getInstance("JKS"); //$NON-NLS-1$
				fileInputStream = new FileInputStream(ServerConfiguration.getServerKeyStorePath());
				keyStore.load(fileInputStream, getKeyStorePassword());
			} catch (final NoSuchAlgorithmException e) {
				throw new ServerKeyStoreException(e);
			} catch (final CertificateException e) {
				throw new ServerKeyStoreException(e);
			} catch (final FileNotFoundException e) {
				throw new ServerKeyStoreException(e);
			} catch (final IOException e) {
				throw new ServerKeyStoreException(e);
			} catch (final KeyStoreException e) {
				throw new ServerKeyStoreException(e);
			} finally {
				try {
					if (fileInputStream != null) {
						fileInputStream.close();
					}
				} catch (final IOException e) {
					throw new ServerKeyStoreException(e);
				}
			}
		}
		return keyStore;
	}

	/**
	 * Unloads the keystore by setting it to null.
	 */
	public void unloadKeyStore() {
		keyStore = null;
	}

	/**
	 * Creates a {@link KeyManagerFactory} for the RMI encryption.
	 *
	 * @return KeyManagerFactory
	 * @throws ServerKeyStoreException in case of failure
	 */
	public KeyManagerFactory getKeyManagerFactory() throws ServerKeyStoreException {
		try {
			final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
				ServerConfiguration.getProperties()
					.getProperty(
						ServerConfiguration.KEYSTORE_CERTIFICATE_TYPE,
						ServerConfiguration.KEYSTORE_CERTIFICATE_TYPE_DEFAULT));
			keyManagerFactory.init(getKeyStore(), getKeyStorePassword());
			return keyManagerFactory;
		} catch (final NoSuchAlgorithmException e) {
			throw new ServerKeyStoreException(e);
		} catch (final KeyStoreException e) {
			throw new ServerKeyStoreException(e);
		} catch (final UnrecoverableKeyException e) {
			throw new ServerKeyStoreException(e);
		}
	}

	/**
	 * Sets java runtime properties for SSL.
	 */
	public void setJavaSSLProperties() {
		System.setProperty("javax.net.ssl.keyStore", ServerConfiguration.getServerKeyStorePath()); //$NON-NLS-1$
		System.setProperty("javax.net.ssl.trustStore", ServerConfiguration.getServerKeyStorePath()); //$NON-NLS-1$
		System.setProperty("javax.net.ssl.keyStorePassword", Arrays.toString(getKeyStorePassword())); //$NON-NLS-1$
		System.setProperty("javax.net.ssl.trustStorePassword", Arrays.toString(getKeyStorePassword())); //$NON-NLS-1$
	}

	/**
	 * Returns the keystore password as a char array.
	 *
	 * @return the password
	 */
	public char[] getKeyStorePassword() {
		return ServerConfiguration.getProperties()
			.getProperty(
				ServerConfiguration.KEYSTORE_PASSWORD,
				ServerConfiguration.KEYSTORE_PASSWORD_DEFAULT)
			.toCharArray();
	}

	private String getKeyStoreAlias() {
		return ServerConfiguration.getProperties().getProperty(
			ServerConfiguration.KEYSTORE_ALIAS,
			ServerConfiguration.KEYSTORE_ALIAS_DEFAULT);
	}
}
