/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto von Wesendonk - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.connectionmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.emf.emfstore.client.exceptions.ESCertificateException;
import org.eclipse.emf.emfstore.client.provider.ESClientConfigurationProvider;
import org.eclipse.emf.emfstore.client.provider.ESKeyStoreManager;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;
import org.eclipse.emf.emfstore.internal.client.model.Configuration;
import org.eclipse.emf.emfstore.internal.client.model.ServerInfo;
import org.eclipse.emf.emfstore.internal.client.model.util.WorkspaceUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.FileUtil;

/**
 * The KeyStoreManager manages the client's KeyStore in which the SSL
 * certificates for multiple EMFStore servers can be stored.
 *
 * @author wesendon
 */

public final class KeyStoreManager implements ESKeyStoreManager {

	private static final String JAVAX_NET_SSL_TRUST_STORE_PASSWORD = "javax.net.ssl.trustStorePassword"; //$NON-NLS-1$
	private static final String JAVAX_NET_SSL_KEY_STORE_PASSWORD = "javax.net.ssl.keyStorePassword"; //$NON-NLS-1$
	private static final String JAVAX_NET_SSL_KEY_STORE = "javax.net.ssl.keyStore"; //$NON-NLS-1$
	private static final String JAVAX_NET_SSL_TRUST_STORE = "javax.net.ssl.trustStore"; //$NON-NLS-1$
	private static final String PROVIDER_CLASS = "providerClass"; //$NON-NLS-1$
	private static final String ORG_ECLIPSE_EMF_EMFSTORE_CLIENT_DEFAULT_CONFIGURATION_PROVIDER = "org.eclipse.emf.emfstore.client.defaultConfigurationProvider"; //$NON-NLS-1$
	/**
	 * Name of keyStore file.
	 */
	public static final String KEYSTORENAME = "emfstoreClient.keystore"; //$NON-NLS-1$
	private static final String KEYSTOREPASSWORD = "654321"; //$NON-NLS-1$
	private static final String CERTIFICATE_TYPE = "X.509"; //$NON-NLS-1$
	private static final String CIPHER_ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"; //$NON-NLS-1$

	/**
	 * Certificate Alias for development test certificate.
	 */
	public static final String DEFAULT_CERTIFICATE = "emfstore test certificate (do not use in production!)"; //$NON-NLS-1$

	private static KeyStoreManager instance;

	private String defaultCertificate;
	private KeyStore keyStore;

	private KeyStoreManager() {
		defaultCertificate = null;
		setupKeys();
		loadConfiguration();
	}

	private void loadConfiguration() {
		final ESClientConfigurationProvider provider = new ESExtensionPoint(
			ORG_ECLIPSE_EMF_EMFSTORE_CLIENT_DEFAULT_CONFIGURATION_PROVIDER).getClass(PROVIDER_CLASS,
				ESClientConfigurationProvider.class);
		if (provider == null) {
			return;
		}
		provider.initDefaultCertificates(this);

	}

	/**
	 * Returns an instance of the {@link KeyStoreManager}.
	 *
	 * @return {@link KeyStoreManager}
	 */
	public static synchronized KeyStoreManager getInstance() {
		if (instance == null) {
			instance = new KeyStoreManager();
		}
		return instance;
	}

	/**
	 * This method sets the JVM properties in order to use SSL encryption.
	 */
	public void setupKeys() {
		// No changes to exception handling here, due to call nature.
		if (!keyStoreExists()) {
			// create directory ~/.emfstore/ if necessary
			final File emfstoreDir = new File(Configuration.getFileInfo().getWorkspaceDirectory());
			if (!emfstoreDir.exists()) {
				emfstoreDir.mkdir();
			}
			final InputStream inputStream = getClass().getResourceAsStream(KEYSTORENAME);
			try {
				// configure file
				final File clientKeyTarget = new File(Configuration.getFileInfo().getWorkspaceDirectory()
					+ KEYSTORENAME);
				// copy to destination
				FileUtil.copyFile(inputStream, clientKeyTarget);
			} catch (final IOException e) {
				// TODO OW: exception? - now the user will be alerted to the
				// problem as soon as he tries to connect.
				// throw new ConnectionException("Couldn't find keystore.");
			} finally {
				try {
					inputStream.close();
				} catch (final IOException e) {
					// TODO: ignore exception for now, as above
				}
			}
		}

		System.setProperty(JAVAX_NET_SSL_TRUST_STORE, getPathToKeyStore());
		System.setProperty(JAVAX_NET_SSL_KEY_STORE, getPathToKeyStore());
		System.setProperty(JAVAX_NET_SSL_KEY_STORE_PASSWORD, KEYSTOREPASSWORD);
		System.setProperty(JAVAX_NET_SSL_TRUST_STORE_PASSWORD, KEYSTOREPASSWORD);
	}

	/**
	 * Lists all certificates in the client's KeyStore.
	 *
	 * @return string representation of the certificates
	 * @throws ESCertificateException
	 *             is thrown when problems occur with the CertificateStore, i.e.
	 *             illegal operations.
	 */
	public ArrayList<String> getCertificates() throws ESCertificateException {
		loadKeyStore();
		final ArrayList<String> certificates = new ArrayList<String>();
		try {
			final Enumeration<String> aliases = keyStore.aliases();
			for (; aliases.hasMoreElements();) {
				final String tmp = aliases.nextElement();
				certificates.add(tmp);
			}
		} catch (final KeyStoreException e) {
			final String message = Messages.KeyStoreManager_Loading_Certificate_Failed;
			WorkspaceUtil.logException(message, e);
			throw new ESCertificateException(message, e);
		}
		return certificates;
	}

	/**
	 * Deletes a certificate in the keystore.
	 *
	 * @param alias
	 *            alias of certificate
	 * @throws ESCertificateException
	 *             is thrown when problems occur with the CertificateStore, i.e.
	 *             illegal operations.
	 */
	public void deleteCertificate(String alias) throws ESCertificateException {
		if (isDefaultCertificate(alias)) {
			throw new ESCertificateException(Messages.KeyStoreManager_Cannot_Delete_Default_Certificate);
		}

		loadKeyStore();
		try {
			keyStore.deleteEntry(alias);
			storeKeyStore();
		} catch (final KeyStoreException e) {
			final String message = Messages.KeyStoreManager_Deleting_Certificate_Failed;
			WorkspaceUtil.logException(message, e);
			throw new ESCertificateException(message, e);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.provider.ESKeyStoreManager#addCertificate(java.lang.String,
	 *      java.lang.String)
	 */
	public void addCertificate(String alias, String path) throws ESCertificateException {
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(path);
			addCertificate(alias, fileInputStream);
		} catch (final FileNotFoundException e) {
			final String message = Messages.KeyStoreManager_Storing_Certificate_Failed;
			WorkspaceUtil.logException(message, e);
			throw new ESCertificateException(message, e);
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (final IOException e) {
					final String message = "Storing certificate failed!"; //$NON-NLS-1$
					WorkspaceUtil.logException(message, e);
					throw new ESCertificateException(message, e);
				}
			}
		}
	}

	/**
	 * Remove certificate with the given alias.
	 *
	 * @param alias the certificate alias
	 * @throws ESCertificateException if removal fails
	 */
	public void removeCertificate(String alias) throws ESCertificateException {
		try {
			keyStore.deleteEntry(alias);
			storeKeyStore();
		} catch (final KeyStoreException e) {
			final String message = Messages.KeyStoreManager_Keystore_Not_Initialized;
			WorkspaceUtil.logException(message, e);
			throw new ESCertificateException(message, e);
		} catch (final ESCertificateException e) {
			final String message = "Storing certificate failed!"; //$NON-NLS-1$
			WorkspaceUtil.logException(message, e);
			throw new ESCertificateException(message, e);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.provider.ESKeyStoreManager#addCertificate(java.lang.String,
	 *      java.io.InputStream)
	 */
	public void addCertificate(String alias, InputStream certificate) throws ESCertificateException {
		if (!isDefaultCertificate(alias)) {
			loadKeyStore();
			try {
				final CertificateFactory factory = CertificateFactory.getInstance(CERTIFICATE_TYPE);
				final Certificate newCertificate = factory.generateCertificate(certificate);
				keyStore.setCertificateEntry(alias, newCertificate);
				storeKeyStore();
			} catch (final CertificateException e) {
				final String message = Messages.KeyStoreManager_Choose_Valid_Certificate;
				throw new ESCertificateException(message);
			} catch (final KeyStoreException e) {
				final String message = "Storing certificate failed!"; //$NON-NLS-1$
				WorkspaceUtil.logException(message, e);
				throw new ESCertificateException(message, e);
			}
		}
	}

	private void storeKeyStore() throws ESCertificateException {
		loadKeyStore();
		try {
			final FileOutputStream fileOutputStream = new FileOutputStream(getPathToKeyStore());
			keyStore.store(fileOutputStream, KEYSTOREPASSWORD.toCharArray());
			fileOutputStream.close();
		} catch (final KeyStoreException e) {
			final String message = "Storing certificate failed!"; //$NON-NLS-1$
			WorkspaceUtil.logWarning(message, e);
			throw new ESCertificateException(message, e);
		} catch (final NoSuchAlgorithmException e) {
			final String message = "Storing certificate failed!"; //$NON-NLS-1$
			WorkspaceUtil.logWarning(message, e);
			throw new ESCertificateException(message, e);
		} catch (final CertificateException e) {
			final String message = "Storing certificate failed!"; //$NON-NLS-1$
			WorkspaceUtil.logWarning(message, e);
			throw new ESCertificateException(message, e);
		} catch (final FileNotFoundException e) {
			final String message = "Storing certificate failed!"; //$NON-NLS-1$
			WorkspaceUtil.logWarning(message, e);
			throw new ESCertificateException(message, e);
		} catch (final IOException e) {
			final String message = "Storing certificate failed!"; //$NON-NLS-1$
			WorkspaceUtil.logWarning(message, e);
			throw new ESCertificateException(message, e);
		}
	}

	/**
	 * Reloads the keystore.
	 *
	 * @throws ESCertificateException
	 *             in case of failure
	 */
	public void reloadKeyStore() throws ESCertificateException {
		keyStore = null;
		loadKeyStore();
	}

	private void loadKeyStore() throws ESCertificateException {
		if (keyStore == null) {
			try {
				keyStore = KeyStore.getInstance("JKS"); //$NON-NLS-1$
				final FileInputStream fileInputStream = new FileInputStream(getPathToKeyStore());
				keyStore.load(fileInputStream, KEYSTOREPASSWORD.toCharArray());
				fileInputStream.close();
			} catch (final KeyStoreException e) {
				final String message = "Loading certificate failed!"; //$NON-NLS-1$
				WorkspaceUtil.logWarning(message, e);
				throw new ESCertificateException(message, e);
			} catch (final NoSuchAlgorithmException e) {
				final String message = "Loading certificate failed!"; //$NON-NLS-1$
				WorkspaceUtil.logWarning(message, e);
				throw new ESCertificateException(message, e);
			} catch (final CertificateException e) {
				final String message = "Loading certificate failed!"; //$NON-NLS-1$
				WorkspaceUtil.logWarning(message, e);
				throw new ESCertificateException(message, e);
			} catch (final FileNotFoundException e) {
				final String message = "Loading certificate failed!"; //$NON-NLS-1$
				WorkspaceUtil.logWarning(message, e);
				throw new ESCertificateException(message, e);
			} catch (final IOException e) {
				final String message = "Loading certificate failed!"; //$NON-NLS-1$
				WorkspaceUtil.logWarning(message, e);
				throw new ESCertificateException(message, e);
			}
		}
	}

	/**
	 * Returns a SSL Context. This is need for encryption, used by the
	 * SSLSocketFactory.
	 *
	 * @return SSL Context
	 * @throws ESCertificateException
	 *             in case of failure retrieving the context
	 */
	public SSLContext getSSLContext() throws ESCertificateException {
		try {
			loadKeyStore();
			final KeyManagerFactory managerFactory = KeyManagerFactory.getInstance("SunX509"); //$NON-NLS-1$
			managerFactory.init(keyStore, KEYSTOREPASSWORD.toCharArray());
			final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509"); //$NON-NLS-1$
			trustManagerFactory.init(keyStore);
			final SSLContext sslContext = SSLContext.getInstance("TLSv1.3"); //$NON-NLS-1$
			sslContext.init(managerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});

			return sslContext;
		} catch (final NoSuchAlgorithmException e) {
			throw new ESCertificateException(Messages.KeyStoreManager_29, e);
		} catch (final UnrecoverableKeyException e) {
			throw new ESCertificateException("Loading certificate failed!", e); //$NON-NLS-1$
		} catch (final KeyStoreException e) {
			throw new ESCertificateException("Loading certificate failed!", e); //$NON-NLS-1$
		} catch (final KeyManagementException e) {
			throw new ESCertificateException("Loading certificate failed!", e); //$NON-NLS-1$
		}
	}

	/**
	 * True if a KeyStore file exists.
	 *
	 * @return boolean
	 */
	public boolean keyStoreExists() {
		final File keyStore = new File(getPathToKeyStore());
		return keyStore.exists();
	}

	/**
	 * Returns the path to the KeyStore.
	 *
	 * @return a path
	 */
	public String getPathToKeyStore() {
		return Configuration.getFileInfo().getWorkspaceDirectory() + KEYSTORENAME;
	}

	/**
	 * Encrypts a password.
	 *
	 * @param password
	 *            the password to be encrypted
	 * @param server
	 *            the server from which to fetch the public key that is used for encryption
	 * @return the encrypted password
	 */
	public String encrypt(String password, ServerInfo server) {
		try {
			final Certificate publicKey = getCertificateForEncryption(server);
			final PublicKey key = publicKey.getPublicKey();
			final byte[] inpBytes = password.getBytes();
			final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			final byte[] encryptededByteAr = cipher.doFinal(inpBytes);
			final byte[] base64EncodedByteAr = Base64.encodeBase64(encryptededByteAr);
			return new String(base64EncodedByteAr);
			// TODO: OW When new login proxy object with encryption handler is
			// implemented, handle exceptions
		} catch (final NoSuchAlgorithmException e) {
			// nothing to do
			e.printStackTrace();
		} catch (final NoSuchPaddingException e) {
			// nothing to do
			e.printStackTrace();
		} catch (final InvalidKeyException e) {
			// nothing to do
			e.printStackTrace();
		} catch (final IllegalBlockSizeException e) {
			// nothing to do
			e.printStackTrace();
		} catch (final BadPaddingException e) {
			// nothing to do
			e.printStackTrace();
		} catch (final ESCertificateException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		WorkspaceUtil.logException(Messages.KeyStoreManager_Could_Not_Encrypt_Password, new ESCertificateException(
			Messages.KeyStoreManager_34));
		return ""; //$NON-NLS-1$
	}

	private Certificate getCertificateForEncryption(ServerInfo server) throws ESCertificateException {
		Certificate publicKey;
		if (server == null) {
			publicKey = getCertificate(getDefaultCertificate());
		} else {
			publicKey = getCertificate(server.getCertificateAlias());
		}
		if (publicKey == null) {
			publicKey = getCertificate(getDefaultCertificate());
			if (publicKey == null) {
				throw new ESCertificateException(Messages.KeyStoreManager_Unable_To_Get_Password);
			}
		}
		return publicKey;
	}

	/**
	 * Test whether a given alias is the default certificate alias.
	 *
	 * @param alias
	 *            alias under test
	 * @return true if default, false else
	 */
	public boolean isDefaultCertificate(String alias) {
		return getDefaultCertificate().equals(alias);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.provider.ESKeyStoreManager#getDefaultCertificate()
	 */
	public String getDefaultCertificate() {
		if (defaultCertificate != null) {
			return defaultCertificate;
		} else if (Configuration.getVersioningInfo().isDeveloperVersion()) {
			return DEFAULT_CERTIFICATE;
		} else {
			return DEFAULT_CERTIFICATE;
		}
	}

	/**
	 * Returns true if the given alias maps to an existing certificate.
	 *
	 * @param alias
	 *            Certificate alias
	 * @return boolean
	 * @throws ESCertificateException
	 *             is thrown when problems occur with the CertificateStore, i.e.
	 *             illegal operations.
	 */
	public boolean contains(String alias) throws ESCertificateException {
		if (getCertificate(alias) == null) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.provider.ESKeyStoreManager#setDefaultCertificate(java.lang.String)
	 */
	public void setDefaultCertificate(String defaultCertificate) {
		this.defaultCertificate = defaultCertificate;
	}

	/**
	 * Returns the certificate mapped by the given alias. Returns null if no
	 * such certificate exists.
	 *
	 * @param alias
	 *            String
	 * @return Certificate
	 * @throws ESCertificateException
	 *             is thrown when problems occur with the CertificateStore, i.e.
	 *             illegal operations.
	 */
	public Certificate getCertificate(String alias) throws ESCertificateException {
		if (alias == null) {
			return null;
		}
		loadKeyStore();
		try {
			return keyStore.getCertificate(alias);
		} catch (final KeyStoreException e) {
			throw new ESCertificateException("Loading certificate failed!"); //$NON-NLS-1$
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.provider.ESKeyStoreManager#certificateExists(java.lang.String)
	 */
	public boolean certificateExists(String alias) throws ESCertificateException {
		try {
			return getCertificate(alias) != null;
		} catch (final ESCertificateException e) {
			if (!(e.getCause() instanceof FileNotFoundException)) {
				throw e;
			}
		}
		return false;
	}
}