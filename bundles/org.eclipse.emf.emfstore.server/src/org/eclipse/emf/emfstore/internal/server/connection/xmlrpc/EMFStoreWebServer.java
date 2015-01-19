/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.connection.xmlrpc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.apache.xmlrpc.webserver.WebServer;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.EMFStoreController;
import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.eclipse.emf.emfstore.internal.server.connection.ServerKeyStoreManager;
import org.eclipse.emf.emfstore.internal.server.exceptions.FatalESException;
import org.eclipse.emf.emfstore.internal.server.exceptions.ServerKeyStoreException;

/**
 * Customized XML RPC web server implementation that support white listing of SSL ciphers.
 * 
 * @author emueller
 * 
 */
public class EMFStoreWebServer extends WebServer {

	private final String[] validCiphers;

	/**
	 * Constructor.
	 * 
	 * @param port
	 *            the port to be used by the web server
	 */
	public EMFStoreWebServer(int port) {
		super(port);
		validCiphers = ServerConfiguration.getSplittedProperty(
			ServerConfiguration.SSL_CIPHERS);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.apache.xmlrpc.webserver.WebServer#allowConnection(java.net.Socket)
	 */
	@Override
	protected boolean allowConnection(Socket socket) {
		if (SSLSocket.class.isInstance(socket)) {
			final SSLSocket ss = (SSLSocket) socket;
			if (validCiphers != null) {
				ss.setEnabledCipherSuites(validCiphers);
			}
			ss.setEnabledProtocols(new String[] { "TLSv1", "TLSv1.1", "TLSv1.2" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		return super.allowConnection(socket);
	}

	@Override
	protected ServerSocket createServerSocket(int pPort, int backlog, InetAddress addr) throws IOException {
		SSLServerSocketFactory serverSocketFactory = null;
		try {
			final SSLContext context = SSLContext.getInstance("TLS"); //$NON-NLS-1$
			context.init(ServerKeyStoreManager.getInstance().getKeyManagerFactory().getKeyManagers(), null,
				null);
			serverSocketFactory = context.getServerSocketFactory();
		} catch (final NoSuchAlgorithmException exception) {
			shutdown(serverSocketFactory, exception);
		} catch (final KeyManagementException exception) {
			shutdown(serverSocketFactory, exception);
		} catch (final ServerKeyStoreException exception) {
			shutdown(serverSocketFactory, exception);
		}

		return serverSocketFactory.createServerSocket(pPort, backlog, addr);
	}

	private void shutdown(SSLServerSocketFactory serverSocketFactory, Exception e) {
		if (serverSocketFactory == null) {
			ModelUtil.logException(Messages.XmlRpcBuiltinWebServer_ServerSocketInitFailed, e);
			EMFStoreController.getInstance().shutdown(new FatalESException());
		}
	}
}
