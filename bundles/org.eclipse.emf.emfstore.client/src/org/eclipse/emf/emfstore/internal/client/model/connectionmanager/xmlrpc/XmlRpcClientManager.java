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
package org.eclipse.emf.emfstore.internal.client.model.connectionmanager.xmlrpc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcSun15HttpTransportFactory;
import org.eclipse.emf.emfstore.client.exceptions.ESCertificateException;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionElement;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;
import org.eclipse.emf.emfstore.internal.client.model.Configuration;
import org.eclipse.emf.emfstore.internal.client.model.ServerInfo;
import org.eclipse.emf.emfstore.internal.client.model.connectionmanager.ConnectionManager;
import org.eclipse.emf.emfstore.internal.client.model.connectionmanager.KeyStoreManager;
import org.eclipse.emf.emfstore.internal.common.ESCollections;
import org.eclipse.emf.emfstore.internal.common.SocketUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.FileUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.SerializationException;
import org.eclipse.emf.emfstore.internal.server.connection.xmlrpc.util.EObjectTypeFactory;
import org.eclipse.emf.emfstore.internal.server.exceptions.ConnectionException;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.model.SessionId;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageEnvelope;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageProxy;
import org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningFactory;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.util.ChangePackageUtil;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.xml.sax.SAXException;

import com.google.common.base.Optional;

/**
 * Manager for XML RPC server calls.
 *
 * @author wesendon
 */
public class XmlRpcClientManager {

	private final String serverInterface;
	private XmlRpcClient client;
	private static boolean serializationOptionsInitialized;
	private static boolean gzipCompressionEnabled;
	private static boolean gzipRequestingEnabled;

	/**
	 * Default constructor.
	 *
	 * @param serverInterface name of interface
	 */
	public XmlRpcClientManager(String serverInterface) {
		this.serverInterface = serverInterface;
	}

	/**
	 * Initializes the connection.
	 *
	 * @param serverInfo server info
	 * @throws ConnectionException in case of failure
	 */
	public void initConnection(ServerInfo serverInfo) throws ConnectionException {
		try {
			initSerializationOptions();
			final XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			config.setServerURL(createURL(serverInfo));
			config.setEnabledForExceptions(true);
			config.setEnabledForExtensions(true);
			config.setConnectionTimeout(Configuration.getXMLRPC().getXMLRPCConnectionTimeout());
			config.setReplyTimeout(Configuration.getXMLRPC().getXMLRPCReplyTimeout());
			config.setContentLengthOptional(true);
			config.setGzipCompressing(gzipCompressionEnabled);
			config.setGzipRequesting(gzipRequestingEnabled);

			client = new XmlRpcClient();
			client.setTypeFactory(new EObjectTypeFactory(client));

			final XmlRpcSun15HttpTransportFactory factory = new XmlRpcSun15HttpTransportFactory(client);

			try {
				factory.setSSLSocketFactory(SocketUtil.enableTLSv12Plus(
					KeyStoreManager.getInstance().getSSLContext().getSocketFactory()));
			} catch (final ESCertificateException e) {
				throw new ConnectionException(Messages.XmlRpcClientManager_Could_Not_Load_Certificate, e);
			}
			client.setTransportFactory(factory);
			client.setConfig(config);
		} catch (final MalformedURLException e) {
			throw new ConnectionException(Messages.XmlRpcClientManager_Malformed_URL_Or_Port, e);
		}
	}

	private URL createURL(ServerInfo serverInfo) throws MalformedURLException {
		checkUrl(serverInfo.getUrl());
		return new URL("https", serverInfo.getUrl(), serverInfo.getPort(), "/xmlrpc"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void checkUrl(String url) throws MalformedURLException {
		if (url != null && !url.equals(StringUtils.EMPTY)) {
			if (!(url.contains(":") || url.contains("/"))) { //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
		}
		throw new MalformedURLException();
	}

	/**
	 * Executes a server call with return value.
	 *
	 * @param <T> return type
	 * @param methodName method name
	 * @param returnType return type
	 * @param parameters parameters
	 * @return returned object from server
	 * @throws ESException in case of failure
	 */
	public <T> T callWithResult(String methodName, Class<T> returnType, Object... parameters) throws ESException {
		return executeCall(methodName, returnType, parameters);
	}

	/**
	 * Executes a server call with list return value.
	 *
	 * @param <T> return type
	 * @param methodName method name
	 * @param returnType list return type
	 * @param parameters parameters
	 * @return list return type
	 * @throws ESException in case of failure
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> callWithListResult(String methodName, Class<T> returnType, Object... parameters)
		throws ESException {
		final List<T> result = new ArrayList<T>();
		final Object[] callResult = executeCall(methodName, Object[].class, parameters);
		if (callResult == null) {
			return result;
		}
		for (final Object obj : callResult) {
			result.add((T) obj);
		}
		return result;
	}

	/**
	 * Executes a server call without return value.
	 *
	 * @param methodName method name
	 * @param parameters parameters
	 * @throws ESException in case of failure
	 */
	public void call(String methodName, Object... parameters) throws ESException {
		executeCall(methodName, null, parameters);
	}

	@SuppressWarnings("unchecked")
	private <T> T executeCall(String methodName, Class<T> returnType, Object[] params) throws ESException {
		if (client == null) {
			throw new ConnectionException(ConnectionManager.REMOTE);
		}

		final Object[] adjustedParams = adjustParameters(params);

		try {
			final T result = (T) client.execute(serverInterface + "." + methodName, adjustedParams); //$NON-NLS-1$
			return adjustResult(
				ESCollections.find(params, SessionId.class),
				ESCollections.find(params, ProjectId.class),
				result);

		} catch (final XmlRpcException e) {
			if (e.getCause() instanceof ESException) {
				throw (ESException) e.getCause();
			} else if (e.linkedException instanceof SAXException
				&& ((SAXException) e.linkedException).getException() instanceof SerializationException) {
				final SerializationException serialE = (SerializationException) ((SAXException) e.linkedException)
					.getException();
				throw new org.eclipse.emf.emfstore.internal.server.exceptions.SerializationException(serialE);
			} else {
				throw new ConnectionException(ConnectionManager.REMOTE + e.getMessage(), e);
			}
		}
	}

	private Object[] adjustParameters(final Object[] params) throws ESException {
		if (!Configuration.getClientBehavior().getChangePackageFragmentSize().isPresent()) {
			return params;
		}

		final Optional<SessionId> maybeSessionId = ESCollections.find(params, SessionId.class);
		final Optional<ProjectId> maybeProjectId = ESCollections.find(params, ProjectId.class);

		if (!maybeSessionId.isPresent() || !maybeProjectId.isPresent()) {
			// do not attempt to split
			return params;
		}

		for (int i = 0; i < params.length; i++) {
			final Object param = params[i];
			if (FileBasedChangePackage.class.isInstance(param) && !ChangePackageProxy.class.isInstance(param)) {
				params[i] = uploadInFragments(
					maybeSessionId.get(),
					maybeProjectId.get(),
					FileBasedChangePackage.class.cast(param));
			}
		}

		return params;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T> T adjustResult(
		final Optional<SessionId> maybeSessionId,
		final Optional<ProjectId> maybeProjectId,
		final T result) throws ESException {
		if (result instanceof Object[]) {
			final Object[] objects = (Object[]) result;
			for (int i = 0; i < objects.length; i++) {
				final Object item = objects[i];
				objects[i] = adjustResult(maybeSessionId, maybeProjectId, item);
			}
			return (T) objects;
		} else if (result instanceof List) {
			final List l = (List) result;
			for (int i = 0; i < l.size(); i++) {
				l.set(i, adjustResult(maybeSessionId, maybeProjectId, result));
			}
		} else if (result instanceof ChangePackageProxy) {
			return (T) downloadAndResolveChangePackage((ChangePackageProxy) result, maybeSessionId, maybeProjectId);
		}

		return result;
	}

	private AbstractChangePackage downloadAndResolveChangePackage(final ChangePackageProxy proxy,
		final Optional<SessionId> maybeSession, Optional<ProjectId> maybeProjectId) throws ESException {

		if (!maybeSession.isPresent()) {
			throw new ESException(Messages.XmlRpcClientManager_NoValidSessionId);
		}

		if (!maybeProjectId.isPresent()) {
			throw new ESException(Messages.XmlRpcClientManager_NoValidProjectId);
		}

		int fragmentIndex = 0;
		final FileBasedChangePackage changePackage = VersioningFactory.eINSTANCE
			.createFileBasedChangePackage();
		changePackage.initialize(FileUtil.createLocationForTemporaryChangePackage());
		final File file = new File(changePackage.getTempFilePath());
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);

			ChangePackageEnvelope envelope;
			do {
				envelope = executeCall("downloadChangePackageFragment", ChangePackageEnvelope.class, new Object[] { //$NON-NLS-1$
					maybeSession.get(),
					maybeProjectId.get(),
					proxy.getId(),
					fragmentIndex
				});
				for (final String s : envelope.getFragment()) {
					writer.write(s + System.getProperty("line.separator")); //$NON-NLS-1$
				}
				fragmentIndex += 1;
			} while (!envelope.isLast());
		} catch (final IOException ex) {
			throw new ESException(Messages.XmlRpcClientManager_DownloadOfFragmentFailed, ex);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (final IOException ex) {
					throw new ESException(Messages.XmlRpcClientManager_DownloadOfFragmentFailed, ex);
				}
			}
		}

		try {
			changePackage.setLogMessage(
				ModelUtil.clone(proxy.getLogMessage()));
			changePackage.save();
		} catch (final IOException ex) {
			throw new ESException(Messages.XmlRpcClientManager_SaveChangePackageFailed, ex);
		}
		return changePackage;
	}

	private ChangePackageProxy uploadInFragments(SessionId sessionId,
		ProjectId projectId, FileBasedChangePackage changePackage)
		throws ESException {

		// get() is guarded
		final Iterator<ChangePackageEnvelope> envelopes = ChangePackageUtil.splitChangePackage(
			changePackage,
			Configuration.getClientBehavior().getChangePackageFragmentSize().get());

		String proxyId = null;
		try {
			while (envelopes.hasNext()) {
				proxyId = uploadChangePackageFragment(
					sessionId,
					projectId,
					envelopes.next());
			}
		} catch (final XmlRpcException ex) {
			throw new ESException(Messages.XmlRpcClientManager_UploadChangePackageFragmentCallFailed, ex);
		}

		final ChangePackageProxy proxy = VersioningFactory.eINSTANCE.createChangePackageProxy();
		proxy.setLogMessage(ModelUtil.clone(changePackage.getLogMessage()));
		proxy.setId(proxyId);
		return proxy;
	}

	private String uploadChangePackageFragment(final SessionId sessionId,
		final ProjectId projectId, final ChangePackageEnvelope envelope) throws XmlRpcException {
		return (String) client.execute(serverInterface + "." + "uploadChangePackageFragment", //$NON-NLS-1$ //$NON-NLS-2$
			new Object[] {
				sessionId,
				projectId,
				envelope
			});
	}

	/**
	 * Initializes the serialization options for compressed server communication.
	 */
	private static void initSerializationOptions() {

		if (serializationOptionsInitialized) {
			return;
		}
		// init compression with false if not configured
		gzipRequestingEnabled = false;
		gzipCompressionEnabled = false;
		final ESExtensionElement element = new ESExtensionPoint(
			"org.eclipse.emf.emfstore.common.model.serializationOptions") //$NON-NLS-1$
				.getFirst();

		if (element != null) {
			gzipCompressionEnabled = element.getBoolean("GzipCompression"); //$NON-NLS-1$
			gzipRequestingEnabled = element.getBoolean("GzipRequesting"); //$NON-NLS-1$
		}

		serializationOptionsInitialized = true;
	}

}
