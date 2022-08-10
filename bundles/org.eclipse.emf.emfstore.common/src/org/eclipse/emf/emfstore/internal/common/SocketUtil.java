/*******************************************************************************
 * Copyright (c) 2011-2019 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.common;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Util related to {@link Socket Sockets} and {@link ServerSocket ServerSockets} and their factories.
 */
public final class SocketUtil {

	private static final String TLSV12 = "TLSv1.2"; //$NON-NLS-1$
	private static final String TLSV13 = "TLSv1.3"; //$NON-NLS-1$

	private SocketUtil() {
		// util
	}

	/**
	 * Attempts to enable only TLSv1.2 and TLSv1.3 protocols for the given socket.
	 *
	 * @param socket {@link Socket}
	 * @return the passed socket
	 * @throws FatalSocketException in case no secure connection possible
	 */
	public static Socket enableTLSv12PlusAndReturn(Socket socket) throws FatalSocketException {
		if (!SSLSocket.class.isInstance(socket)) {
			return socket;
		}
		final SSLSocket sslSocket = SSLSocket.class.cast(socket);
		sslSocket.setEnabledProtocols(getProtocolsToKeep(sslSocket.getEnabledProtocols()));
		return sslSocket;
	}

	/**
	 * Attempts to enable only TLSv1.2 and TLSv1.3 protocols for the given server socket.
	 *
	 * @param serverSocket {@link ServerSocket}
	 * @return the passed socket
	 * @throws FatalSocketException in case no secure connection possible
	 */
	public static ServerSocket enableTLSv12PlusAndReturn(ServerSocket serverSocket) throws FatalSocketException {
		if (!SSLServerSocket.class.isInstance(serverSocket)) {
			return serverSocket;
		}
		final SSLServerSocket sslServerSocket = SSLServerSocket.class.cast(serverSocket);
		sslServerSocket.setEnabledProtocols(getProtocolsToKeep(sslServerSocket.getEnabledProtocols()));
		return sslServerSocket;
	}

	private static String[] getProtocolsToKeep(final String[] enabledProtocols) throws FatalSocketException {
		final List<String> remainingProtocols = new ArrayList<String>();
		for (final String protocol : enabledProtocols) {
			if (protocol.equals(TLSV12) || protocol.equals(TLSV13)) {
				remainingProtocols.add(protocol);
			}
		}
		if (remainingProtocols.isEmpty()) {
			throw new FatalSocketException();
		}
		return remainingProtocols.toArray(new String[remainingProtocols.size()]);
	}

	/**
	 * Wraps the given {@link SSLSocketFactory} into a factory that attempts to create Sockets that only use TLSv1.2 and
	 * TLSv1.3 protocols.
	 *
	 * @param socketFactory the factory to wrap
	 * @return a wrapper
	 */
	public static SSLSocketFactory enableTLSv12Plus(SSLSocketFactory socketFactory) {
		return new TLSv12PlusSocketFactory(socketFactory);
	}

	/**
	 * Wrapper around a {@link SSLSocketFactory} which parses the enabled protocols and allows only TLSv1.2 and TLSv1.3.
	 */
	private static class TLSv12PlusSocketFactory extends SSLSocketFactory {

		private final SSLSocketFactory delegate;

		TLSv12PlusSocketFactory(SSLSocketFactory delegate) {
			this.delegate = delegate;
		}

		@Override
		public int hashCode() {
			return delegate.hashCode();
		}

		@Override
		public Socket createSocket() throws IOException {
			try {
				return enableTLSv12PlusAndReturn(delegate.createSocket());
			} catch (final FatalSocketException ex) {
				throw new IOException(ex.getMessage());
			}
		}

		@Override
		public boolean equals(Object obj) {
			return delegate.equals(obj);
		}

		@Override
		public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
			try {
				return enableTLSv12PlusAndReturn(delegate.createSocket(host, port));
			} catch (final FatalSocketException ex) {
				throw new IOException(ex.getMessage());
			}
		}

		@Override
		public String[] getDefaultCipherSuites() {
			return delegate.getDefaultCipherSuites();
		}

		@Override
		public String[] getSupportedCipherSuites() {
			return delegate.getSupportedCipherSuites();
		}

		@Override
		public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
			try {
				return enableTLSv12PlusAndReturn(delegate.createSocket(s, host, port, autoClose));
			} catch (final FatalSocketException ex) {
				throw new IOException(ex.getMessage());
			}
		}

		@Override
		public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
			throws IOException, UnknownHostException {
			try {
				return enableTLSv12PlusAndReturn(delegate.createSocket(host, port, localHost, localPort));
			} catch (final FatalSocketException ex) {
				throw new IOException(ex.getMessage());
			}
		}

		@Override
		public Socket createSocket(InetAddress host, int port) throws IOException {
			try {
				return enableTLSv12PlusAndReturn(delegate.createSocket(host, port));
			} catch (final FatalSocketException ex) {
				throw new IOException(ex.getMessage());
			}
		}

		@Override
		public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)
			throws IOException {
			try {
				return enableTLSv12PlusAndReturn(delegate.createSocket(address, port, localAddress, localPort));
			} catch (final FatalSocketException ex) {
				throw new IOException(ex.getMessage());
			}
		}

		@Override
		public String toString() {
			return delegate.toString();
		}
	}
}
