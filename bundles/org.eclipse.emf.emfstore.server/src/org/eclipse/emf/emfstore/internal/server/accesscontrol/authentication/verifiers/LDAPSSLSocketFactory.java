/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.accesscontrol.authentication.verifiers;

import static org.eclipse.emf.emfstore.internal.common.SocketUtil.enableTLSv12PlusAndReturn;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.eclipse.emf.emfstore.internal.common.FatalSocketException;
import org.eclipse.emf.emfstore.internal.server.connection.ServerKeyStoreManager;
import org.eclipse.emf.emfstore.internal.server.exceptions.ServerKeyStoreException;

/**
 * LDAP SSL socket factory used for LDAP verification.
 *
 * @author emueller
 *
 */
public class LDAPSSLSocketFactory extends SSLSocketFactory {

	private SSLSocketFactory socketFactory;

	/**
	 * Constructor.
	 */
	public LDAPSSLSocketFactory() {
		SSLContext context;
		try {
			context = SSLContext.getInstance("TLSv1.3"); //$NON-NLS-1$
			final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
				TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(ServerKeyStoreManager.getInstance().getKeyStore());
			context.init(ServerKeyStoreManager.getInstance().getKeyManagerFactory().getKeyManagers(),
				trustManagerFactory.getTrustManagers(),
				null);
			socketFactory = context.getSocketFactory();
		} catch (final NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (final ServerKeyStoreException e) {
			e.printStackTrace();
		} catch (final KeyStoreException e) {
			e.printStackTrace();
		} catch (final KeyManagementException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a new instance of this factory.
	 *
	 * @return a new instance of this factory
	 */
	public static synchronized SocketFactory getDefault() {
		return new LDAPSSLSocketFactory();
	}

	@Override
	public Socket createSocket(Socket arg0, String arg1, int arg2, boolean arg3) throws IOException {
		try {
			return enableTLSv12PlusAndReturn(socketFactory.createSocket(arg0, arg1, arg2, arg3));
		} catch (final FatalSocketException ex) {
			throw new IOException(ex.getMessage());
		}
	}

	@Override
	public String[] getDefaultCipherSuites() {
		return socketFactory.getDefaultCipherSuites();
	}

	@Override
	public String[] getSupportedCipherSuites() {
		return socketFactory.getSupportedCipherSuites();
	}

	@Override
	public Socket createSocket(String arg0, int arg1) throws IOException, UnknownHostException {
		try {
			return enableTLSv12PlusAndReturn(socketFactory.createSocket(arg0, arg1));
		} catch (final FatalSocketException ex) {
			throw new IOException(ex.getMessage());
		}
	}

	@Override
	public Socket createSocket(InetAddress arg0, int arg1) throws IOException {
		try {
			return enableTLSv12PlusAndReturn(socketFactory.createSocket(arg0, arg1));
		} catch (final FatalSocketException ex) {
			throw new IOException(ex.getMessage());
		}
	}

	@Override
	public Socket createSocket(String arg0, int arg1, InetAddress arg2, int arg3) throws IOException,
		UnknownHostException {
		try {
			return enableTLSv12PlusAndReturn(socketFactory.createSocket(arg0, arg1, arg2, arg3));
		} catch (final FatalSocketException ex) {
			throw new IOException(ex.getMessage());
		}
	}

	@Override
	public Socket createSocket(InetAddress arg0, int arg1, InetAddress arg2, int arg3) throws IOException {
		try {
			return enableTLSv12PlusAndReturn(socketFactory.createSocket(arg0, arg1, arg2, arg3));
		} catch (final FatalSocketException ex) {
			throw new IOException(ex.getMessage());
		}
	}

}